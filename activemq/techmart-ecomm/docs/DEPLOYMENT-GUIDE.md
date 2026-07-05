# TechMart Online — Deployment Guide
# Assignment: JIAT/BCD I/EX/01
# Jakarta EE 10 | Payara 6 | MySQL 8 | Maven

-------------------------------------------------------------------------------------------------------------------------

## Prerequisites

| Software        | Version   | Download                          |
|-----------------|-----------|-----------------------------------|
| Java JDK        | 17+       | https://adoptium.net              |
| Payara Server   | 6.x       | https://www.payara.fish/downloads |
| MySQL           | 8.x       | https://dev.mysql.com/downloads   |
| Maven           | 3.8+      | https://maven.apache.org/download |
| MySQL Connector | 8.0.33    | Via Maven local repo              |

-----------------------------------------------------------------------------------------------------------------------

## Step 1 — Database Setup

Open MySQL and run:
```sql
source database/schema.sql;
source database/sample-data.sql;
```

Verify:
```sql
USE techmart_db;
SHOW TABLES;
SELECT COUNT(*) FROM products;
```
Expected: 4 tables, 8 products.

-----------------------------------------------------------------------------------------------------------------------

## Step 2 — MySQL Driver Installation

Locate the driver in your Maven local repository:
C:\Users[USERNAME].m2\repository\com\mysql
mysql-connector-j\8.0.33\mysql-connector-j-8.0.33.jar

Copy to Payara domain lib:
```powershell
copy "mysql-connector-j-8.0.33.jar" `
     "PAYARA_HOME\glassfish\domains\domain1\lib\"
```

--------------------------------------------------------------------------------------------------------------------

## Step 3 — Start Payara

```powershell
cd PAYARA_HOME\bin
.\asadmin start-domain
```

Wait for: `Command start-domain executed successfully.`

----------------------------------------------------------------------------------------------------------------------

## Step 4 — Create JDBC Resources

```powershell
.\asadmin create-jdbc-connection-pool `
  --datasourceclassname com.mysql.cj.jdbc.MysqlDataSource `
  --restype javax.sql.DataSource `
  --property "ServerName=localhost:Port=3306:
DatabaseName=techmart_db:User=techmart_user:
Password=techmart123:useSSL=false:
allowPublicKeyRetrieval=true:serverTimezone=UTC" `
  TechMartPool

.\asadmin create-jdbc-resource `
  --connectionpoolid TechMartPool `
  jdbc/TechMartDS

.\asadmin ping-connection-pool TechMartPool
```

--------------------------------------------------------------------------------------------------------------------

## Step 5 — Create JMS Resources

```powershell
.\asadmin create-jms-resource `
  --restype jakarta.jms.ConnectionFactory `
  jms/TechMartConnectionFactory

.\asadmin create-jms-resource `
  --restype jakarta.jms.Queue `
  --property Name=OrderQueue `
  jms/OrderQueue

.\asadmin create-jms-resource `
  --restype jakarta.jms.Topic `
  --property Name=InventoryTopic `
  jms/InventoryTopic
```

-----------------------------------------------------------------------------------------------------------------------

## Step 6 — Build the Project

```powershell
cd techmart-ecomm
mvn clean package -DskipTests
```

Expected output:
[INFO] TechMart EJB Module ......... SUCCESS
[INFO] TechMart Web Module ......... SUCCESS
[INFO] TechMart EAR Module ......... SUCCESS
[INFO] BUILD SUCCESS

EAR file location:
techmart-ear\target\techmart-ear-1.0-SNAPSHOT.ear

---

## Step 7 — Deploy to Payara

```powershell
.\asadmin deploy `
  --contextroot /techmart `
  --name techmart-ecomm `
  "techmart-ear\target\techmart-ear-1.0-SNAPSHOT.ear"
```

-----------------------------------------------------------------------------------------------------------------------

## Step 8 — Verify Deployment

### Check Application is Running
```powershell
.\asadmin list-applications
```
Expected: `techmart-ecomm`

### Open Application URLs
| URL                                      | Purpose              |
|------------------------------------------|----------------------|
| http://localhost:8080/techmart/products/ | Products page        |
| http://localhost:8080/techmart/cart/     | Shopping cart        |
| http://localhost:8080/techmart/orders/   | Orders page          |
| http://localhost:8080/techmart/metrics/  | Performance metrics  |
| http://localhost:8080/techmart/metrics/json | JSON API          |

### Admin Login Credentials
Email:    admin@techmart.com
Password: admin123
Role:     ADMIN

-------------------------------------------------------------------------------------------------------------------------

## Step 9 — Performance Verification

### Check Metrics Dashboard
Open: http://localhost:8080/techmart/metrics/

Verify these values appear:
Cache Size:         8 products
Cache Init Time:    > 0ms
Uptime:             increasing

### Run Quick Load Test
```powershell
cd C:\Apache24\bin
.\ab -n 100 -c 10 http://localhost:8080/techmart/products/
```

Expected:
Failed requests: 0
Requests per second: > 50
Time per request: < 1000ms

-------------------------------------------------------------------------------------------------------------------------

## Step 10 — Check Server Logs

```powershell
Get-Content `
  "PAYARA_HOME\glassfish\domains\domain1\logs\server.log" `
  -Tail 50
```

Look for:
InventoryManagerBean initializing...
Inventory cache loaded: 8 products in Xms

---------------------------------------------------------------------------------------------------------------------

## Troubleshooting

| Problem                  | Solution                              |
|--------------------------|---------------------------------------|
| Ping pool fails          | Check MySQL is running                |
|                          | Verify techmart_user credentials      |
| 404 on /techmart         | Check context root in deploy command  |
| 500 on products page     | Check server.log for stack trace      |
| JMS resource not found   | Re-run JMS creation commands          |
| Cache shows 0 products   | Re-run sample-data.sql                |

-------------------------------------------------------------------------------------------------------------------------

## Undeployment

```powershell
.\asadmin undeploy techmart-ecomm
```

