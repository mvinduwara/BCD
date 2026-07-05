# TechMart Payara 6 Configuration Reference
# Assignment: JIAT/BCD I/EX/01
# Performance-Focused Settings

----------------------------------------------------------------------------------------------------

## 1. JDBC Connection Pool — TechMartPool

### Creation Command
asadmin create-jdbc-connection-pool
--datasourceclassname com.mysql.cj.jdbc.MysqlDataSource
--restype javax.sql.DataSource
--property "ServerName=localhost:Port=3306:
DatabaseName=techmart_db:User=techmart_user:
Password=techmart123:useSSL=false:
allowPublicKeyRetrieval=true:serverTimezone=UTC"
TechMartPool

### Pool Settings (Performance Tuning)
| Parameter              | Value  | Purpose                          |
|------------------------|--------|----------------------------------|
| Initial Pool Size      | 8      | Connections always ready         |
| Maximum Pool Size      | 32     | Max concurrent DB connections    |
| Pool Resize Quantity   | 2      | Incremental scaling              |
| Idle Timeout           | 300s   | Release unused connections       |
| Max Wait Time          | 60000ms| Timeout before error             |
| Connection Validation  | true   | Detect stale connections         |

### Ping Test
asadmin ping-connection-pool TechMartPool
Expected: `Command ping-connection-pool executed successfully.`

-----------------------------------------------------------------------------------------------

## 2. JDBC Resource

### Creation Command
asadmin create-jdbc-resource
--connectionpoolid TechMartPool
jdbc/TechMartDS

------------------------------------------------------------------------------------------------

## 3. JMS Resources
### Connection Factory
asadmin create-jms-resource
--restype jakarta.jms.ConnectionFactory
jms/TechMartConnectionFactory

### Order Queue (Point-to-Point)
asadmin create-jms-resource
--restype jakarta.jms.Queue
--property Name=OrderQueue
jms/OrderQueue

### Inventory Topic (Publish-Subscribe)
asadmin create-jms-resource
--restype jakarta.jms.Topic
--property Name=InventoryTopic
jms/InventoryTopic

### Verify All JMS Resources
asadmin list-jms-resources

Expected output:
jms/TechMartConnectionFactory
jms/OrderQueue
jms/InventoryTopic

-----------------------------------------------------------------------------------------------------------------

## 4. MySQL Driver Setup

Copy MySQL JDBC driver to Payara domain lib:
copy mysql-connector-j-8.0.33.jar
PAYARA_HOME\glassfish\domains\domain1\lib\

Then restart Payara to load the driver.

---

## 5. Performance Monitoring Endpoints

| Endpoint                        | Purpose                        |
|---------------------------------|--------------------------------|
| /techmart/metrics/              | Live HTML dashboard            |
| /techmart/metrics/json          | JSON API for monitoring tools  |

### JSON API Response Structure
```json
{
  "timestamp": "yyyy-MM-dd HH:mm:ss",
  "inventory": {
    "cacheSize": 8,
    "cacheHits": 150,
    "cacheMisses": 3,
    "cacheInitTimeMs": 45
  },
  "messaging": {
    "ordersProcessed": 12,
    "inventoryUpdates": 5,
    "notificationsSent": 24,
    "avgNotificationMs": 102.5
  },
  "performance": {
    "totalRequests": 1000,
    "avgResponseMs": 26.11,
    "uptimeSeconds": 3600
  }
}
```

