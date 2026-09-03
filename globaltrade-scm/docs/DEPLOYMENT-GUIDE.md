# GlobalTrade Supply Chain Management System — Deployment & Security Configuration Guide

## 1. Prerequisites

| Requirement | Version |
|---|---|
| JDK | 17 |
| Apache Maven | 3.9+ |
| Payara Server | 6 (Community, 6.2025.11) |
| MySQL | 8.x |

## 2. Building the Project

From the reactor root:

mvn clean install


This builds all four modules in order — `scm-common`, `scm-ejb`, `scm-web`, `scm-ear` — and produces the deployable artifact at `scm-ear/target/globaltrade-scm.ear`.

The project deliberately keeps EJB business logic (`scm-ejb`), REST endpoints (`scm-web`), and shared DTOs/exceptions (`scm-common`) as separate Maven modules bundled together only at the `scm-ear` level. This split directory structure means each tier can be modified and rebuilt independently, and the EAR is what makes the WAR's `@EJB`-injected fields resolvable against the EJB-JAR at deploy time — the two tiers are only wired together once, at the top.

## 3. Database Setup

Run once against a MySQL instance, connected as an administrative user:

```sql
CREATE DATABASE IF NOT EXISTS scm_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER IF NOT EXISTS 'scm_app'@'%' IDENTIFIED BY '<choose a real password>';
GRANT ALL PRIVILEGES ON scm_db.* TO 'scm_app'@'%';
FLUSH PRIVILEGES;
```

`scm_app` is scoped to `scm_db` only — the application never runs as a MySQL administrative account.

Schema tables are created automatically on first deploy via `eclipselink.ddl-generation=create-or-extend-tables` in `persistence.xml`. Seed data for demo shipments, vendors, and inventory can be loaded separately via the `01-seed-data.sql` script included in `scm-ejb/src/main/resources/db/`.

## 4. Application Server Configuration

### 4.1 MySQL JDBC Driver

The MySQL Connector/J JAR must be placed directly in Payara's own classpath — not just declared as a Maven dependency — since the connection pool below is resolved by the server itself:

<payara-install>/glassfish/domains/domain1/lib/mysql-connector-j-8.4.0.jar


Restart the domain after adding it.

### 4.2 JDBC Connection Pool

Created through the Payara Admin Console rather than bundled in the EAR — deliberately, after the bundled `glassfish-resources.xml` approach proved unreliable during development, since Payara didn't consistently process it before the persistence unit needed the pool at deploy time. Configuring it directly through the console is the more dependable path for this deployment.

Resources → JDBC → JDBC Connection Pools → New:

| Field | Value |
|---|---|
| Pool Name | `scmConnectionPool` |
| Resource Type | `javax.sql.DataSource` |
| Datasource Classname | `com.mysql.cj.jdbc.MysqlDataSource` |

Additional Properties:

| Property | Value |
|---|---|
| serverName | `localhost` |
| portNumber | `3306` |
| databaseName | `scm_db` |
| user | `scm_app` |
| password | *(the password set in Section 3)* |
| useSSL | `false` |
| allowPublicKeyRetrieval | `true` |

Transaction isolation level: **`read-committed`**, set explicitly rather than left at MySQL's InnoDB default of `repeatable-read`. The one write-conflict scenario that matters in this system — concurrent inventory quantity updates on the same row — is already guarded at the application level by optimistic locking (`InventoryItem.version`, a JPA `@Version` field). Given that guard already exists, `repeatable-read`'s extra locking overhead protects against a scenario the application doesn't rely on the database to prevent, while adding real lock contention on a table under regular concurrent writes.

Verify with the **Ping** button before proceeding.

### 4.3 JDBC Resource

Resources → JDBC → JDBC Resources → New:

| Field | Value |
|---|---|
| JNDI Name | `jdbc/scmDS` |
| Pool Name | `scmConnectionPool` |

This JNDI name must match `persistence.xml`'s `<jta-data-source>` value exactly.

## 5. Deploying the Application

Applications → Deploy An Application:

- **Location**: browse to `scm-ear/target/globaltrade-scm.ear`
- **Type**: Enterprise Application
- **Status**: Enabled

If redeploying after a structural change (module bundle names, packaging configuration), fully **Undeploy** the existing application first rather than using Payara's incremental Redeploy — the incremental path has shown internal errors when reconciling significant structural changes against cached deployment state.

## 6. Verifying the Deployment

GET http://localhost:8080/scm/api/shipments


An unauthenticated request to this endpoint should return HTTP 403 with `{"message":"You do not have permission to perform this action"}` — this confirms `@RolesAllowed` is being enforced at the EJB tier, not that something is broken. A working end-to-end check is signing in through the frontend and confirming the dashboard loads real data.

`asadmin list-timers` should show five registered timers — one per `@Singleton` timer bean (shipment status, inventory monitoring, vendor evaluation, customs deadlines, route optimization).

---

## 7. Security Configuration

### 7.1 Authentication Architecture

Authentication is handled by Payara's built-in **JDBCRealm** (`com.sun.enterprise.security.auth.realm.jdbc.JDBCRealm`), configured entirely through the Admin Console rather than a custom `LoginModule`. This was a deliberate architectural choice: JDBCRealm is a stable, long-documented part of the platform requiring no custom Java code, which removes an entire category of integration risk compared to hand-writing and registering a raw JAAS `LoginModule` against a container-specific realm subclass.

Configurations → *(server-config)* → Security → Realms → New:

| Field | Value |
|---|---|
| Realm Name | `scmRealm` |
| Class Name | `com.sun.enterprise.security.auth.realm.jdbc.JDBCRealm` |
| JAAS Context | `jdbcRealm` |
| JNDI | `jdbc/scmDS` |
| User Table | `personnel` |
| User Name Column | `username` |
| Password Column | `password_hash` |
| Group Table | `personnel` |
| Group Table User Name Column | `username` |
| Group Name Column | `role` |
| Digest Algorithm | `SHA-256` |

User and group data share a single table because `role` is already a column on `Personnel` — no separate join table is needed for this schema.

`scm-web/WEB-INF/web.xml` registers the realm for container-managed login (`<login-config><realm-name>scmRealm</realm-name></login-config>`), and `AuthResource.login()` triggers authentication programmatically via `HttpServletRequest.login(username, password)`, followed by an explicit `getSession(true)` call to force a real HTTP session into existence — without it, the authenticated identity does not persist past the single login request.

### 7.2 Role-Based Access Control

Four roles, matching the business requirement for distinct logistics personas: `COORDINATOR`, `CUSTOMS_AGENT`, `WAREHOUSE_MANAGER`, `VENDOR_REPRESENTATIVE`.

`scm-ear/META-INF/glassfish-application.xml` maps each application role to the corresponding **group** JDBCRealm attaches at login (not a principal — role assignment here comes from the `role` column via group mapping, not from a per-user principal name):

```xml
<security-role-mapping>
    <role-name>COORDINATOR</role-name>
    <group-name>COORDINATOR</group-name>
</security-role-mapping>
```

*(repeated for each of the four roles)*

Enforcement itself happens declaratively, via `@RolesAllowed` on individual service bean methods — e.g. `VendorServiceBean.create()` is restricted to `COORDINATOR` only, while `findAll()`/`findById()` on the same bean are open to any authenticated role that legitimately needs read access. The restriction is applied per-method deliberately, not at the class level, since a blanket class-level restriction would either lock out roles that need read access or open up writes to roles that shouldn't have them.

A failed `@RolesAllowed` check throws `EJBAccessException` from the container automatically. `EjbAccessExceptionMapper` catches this once, globally, and converts it into a clean `403 Forbidden` JSON response — no individual REST resource needs its own try/catch for this.

### 7.3 Session Security

`web.xml`:

```xml
<session-config>
    <session-timeout>30</session-timeout>
    <cookie-config>
        <http-only>true</http-only>
    </cookie-config>
</session-config>
```

`http-only` prevents any injected script from reading the session cookie via `document.cookie`. The 30-minute timeout bounds how long a stolen or abandoned session remains valid. `secure` is intentionally **not** set — enabling it would stop the cookie being sent at all over plain `http://localhost` during development. This is a deployment-profile decision: on for any real HTTPS environment, off only for local testing.

### 7.4 Programmatic Authorization

Declarative `@RolesAllowed` can express "must hold this role," but cannot express "and only for records this specific user owns" — that requires a runtime check against the actual data being requested, which no annotation on a method signature can encode.

`PurchaseOrderServiceBean.confirm()` demonstrates this: a `VENDOR_REPRESENTATIVE` may confirm a purchase order, but only if it belongs to their own linked vendor. This is checked programmatically via `SessionContext.isCallerInRole()` and `getCallerPrincipal()`, cross-referenced against the `Personnel.vendor` relationship. A `COORDINATOR` is not held to this same check — broader oversight is treated as a legitimate role distinction, not an oversight in the code.

### 7.5 Password Storage — Known Limitations

Passwords are stored as an unsalted SHA-256 digest (hex-encoded), matching what JDBCRealm's built-in digest comparison supports without additional custom code. This is a deliberate simplification for this deployment, not a production-appropriate scheme, and is worth stating plainly rather than glossing over:

- **No per-user salt.** Identical passwords produce identical stored hashes, and the built-in realm's digest comparison has no mechanism for per-row salt lookup without a custom digest algorithm implementation.
- **SHA-256 is a fast hash**, designed for speed — which is exactly the wrong property for password storage, since it makes brute-force attempts cheap at scale.

A production deployment should use an adaptive, deliberately slow algorithm (bcrypt, Argon2, or PBKDF2) via a vetted library, with per-user salting, rather than either of these shortcuts.

8. Performance and Security Monitoring Setup
   The application relies on external diagnostic tools to monitor container resource allocation, EJB transaction latency, and the resilience of the security architecture under concurrent loads.

8.1 JVM Profiling with VisualVM
VisualVM (v2.2.1) is utilized to monitor the Payara Server's live JVM resource consumption and profile the execution time of the supply chain business logic.

Connecting: Launch VisualVM and double-click the active Payara/GlassFish server process (pid) from the local applications list.

Resource Monitoring: Navigate to the Monitor tab during active application usage. The Heap graph should display a healthy "sawtooth" pattern, confirming that the container is efficiently garbage-collecting the short-lived objects generated by the stateless session beans without memory leaks. The Threads tab will verify that the server is successfully scaling up isolated worker threads to handle asynchronous EJB timer executions without blocking HTTP listeners.

CPU Sampling: To measure the specific overhead of the custom interceptors and CMT transactions, navigate to the Sampler tab.

Click the Settings checkbox.

Select Profile only classes and set the filter explicitly to the application's root package: com.globaltrade.scm.**.

Click CPU to start sampling, execute several supply chain workflows via the frontend, and click Stop. The resulting metrics isolate the exact execution latency of the application's EJB logic.

8.2 Security Stress Testing with Apache Benchmark (ab)
Apache Benchmark (v2.4) is used from the command line to validate that the Role-Based Access Control (RBAC) architecture can repel high-volume unauthorized traffic without degrading server performance or crashing the container.

Run the following command against a protected REST endpoint (e.g., Shipments or Inventory) from the terminal:

Bash
ab -n 1000 -c 100 http://localhost:8080/scm/api/shipments/
This simulates 100 concurrent users sending a total of 1,000 requests. Because no authentication headers are provided, the expected outcome is a 100% failure rate at the HTTP level (reported as Non-2xx responses by Apache Benchmark), confirming that the container's security interceptor successfully rejected every request with a 403 Forbidden status. Crucially, the Failed requests metric (which indicates dropped connections or server crashes) must remain at 0, proving the system remains stable under hostile load.

8.3 Frontend Authorization Validation
Live programmatic authorization is verified by authenticating through the Vue.js frontend console using the seeded demo accounts:

coordinator1 (Password: password123)

vendor1 (Password: password123)

customs1 (Password: password123)

To validate programmatic security controls, log in as vendor1 and attempt to manipulate or access a Purchase Order not assigned to that specific vendor's ID. The backend EJB container will catch the ownership mismatch during the SessionContext.isCallerInRole() check, preventing the transaction and returning a clean access denied error to the UI.