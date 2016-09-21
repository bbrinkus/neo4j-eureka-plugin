# Neo4j Eureka Plugin

## Neo4j Configuration

### Neo4j (3.0+)

`NEO4J_HOME/conf/neo4j.conf`

```
dbms.security.auth_enabled=false
dbms.unmanaged_extension_classes=com.brinkus.labs.neo4j.eureka.unmanaged=/eureka
```

### Neo4j (<3.0)

`NEO4J_HOME/conf/neo4j-server.properties`

```
dbms.security.auth_enabled=false
org.neo4j.server.thirdparty_jaxrs_classes=com.brinkus.labs.neo4j.eureka.unmanaged=/eureka
```

## Plugin Configuration

The plugin configuration (YAML based): `NEO4J_HOME/conf/neo4j-eureka.properties`  

```
# List of the service discovery servers host and port information
services:
  -
    host: discovery1.dev.brinkus.com
    port: 8761
  -
    host: discovery2.dev.brinkus.com
    port: 8762
# Registration information used in discovery service
registration:
  # Application's name
  name: neo4j
  # Virtual Internet Protocol address
  vipAddress: neo4j
  # Fully qualifed hostname of the instance
  hostname: neo4j.dev.brinkus.com
  # IP address of the instance
  ipAddress: 127.0.0.1
  # With AWS use the DNS resolvation name
  awsDnsHostname: true
  # The unsecured port
  port:
    port: 7474
    enabled: true
  # The secure port
  securePort:
    port: 7473
    enabled: false
  statusPageUrl: http://neo4j.dev.brinkus.com:7474/browser
  healthCheckUrl: http://neo4j.dev.brinkus.com:7474/eureka/health
  homePageUrl: http://neo4j.dev.brinkus.com:7474/
```


## Eureka communication

1. Get delta
    ```
    GET /eureka/apps/delta HTTP/1.1
    ```

2. Register new application instance (STARTING state)
    ```
    POST /eureka/apps/NEO4J 
    {
        "instance":{
            "hostName":"neo4j.dev.brinkus.com",
            "app":"NEO4J",
            "ipAddr":"192.168.123.10",
            "status":"STARTING",
            "overriddenstatus":"UNKNOWN",
            "port":{
                "@enabled":"true",
                "$":"7474"
            },
            "securePort":{
                "@enabled":"false",
                "$":"7473"
            },
            "countryId":1,
            "dataCenterInfo":{
                "@class":"com.netflix.appinfo.InstanceInfo$DefaultDataCenterInfo",
                "name":"MyOwn"
            },
            "leaseInfo":{
                "renewalIntervalInSecs":30,
                "durationInSecs":90,
                "registrationTimestamp":0,
                "lastRenewalTimestamp":0,
                "evictionTimestamp":0,
                "serviceUpTimestamp":0
            },
            "metadata":{
                "@class":"java.util.Collections$EmptyMap"
            },
            "appGroupName":"UNKNOWN",
            "homePageUrl":"http://neo4j.dev.brinkus.com:7474/",
            "statusPageUrl":"http://neo4j.dev.brinkus.com:7474/",
            "healthCheckUrl":"http://neo4j.dev.brinkus.com:7474/",
            "vipAddress":"neo4j",
            "isCoordinatingDiscoveryServer":false,
            "lastUpdatedTimestamp":1460241157641,
            "lastDirtyTimestamp":1460241157641
        }
    }
    ```

3. Update existing instance status 
    1. Change status with a PUT
    ```
    PUT /eureka/apps/NEO4J/neo4j.dev.brinkus.com/status?value=UP
    ```
    
    2. Change status with a POST
    ```
    POST /eureka/apps/NEO4J 
    {
        "instance":{
            "hostName":"neo4j.dev.brinkus.com",
            "app":"NEO4J",
            "ipAddr":"192.168.123.10",
            "status":"UP",
            "overriddenstatus":"UNKNOWN",
            "port":{
                "@enabled":"true",
                "$":"7474"
            },
            "securePort":{
                "@enabled":"false",
                "$":"7473"
            },
            "countryId":1,
            "dataCenterInfo":{
                "@class":"com.netflix.appinfo.InstanceInfo$DefaultDataCenterInfo",
                "name":"MyOwn"
            },
            "leaseInfo":{
                "renewalIntervalInSecs":30,
                "durationInSecs":90,
                "registrationTimestamp":0,
                "lastRenewalTimestamp":0,
                "evictionTimestamp":0,
                "serviceUpTimestamp":0
            },
            "metadata":{
                "@class":"java.util.Collections$EmptyMap"
            },
            "appGroupName":"UNKNOWN",
            "homePageUrl":"http://neo4j.dev.brinkus.com:7474/",
            "statusPageUrl":"http://neo4j.dev.brinkus.com:7474/",
            "healthCheckUrl":"http://neo4j.dev.brinkus.com:7474/",
            "vipAddress":"neo4j",
            "isCoordinatingDiscoveryServer":false,
            "lastUpdatedTimestamp":1460241157641,
            "lastDirtyTimestamp":1460241198279
        }
    }
    ```


4. Sending application heartbeat
    ```
    PUT /eureka/apps/NEO4J/neo4j.dev.brinkus.com
    ```

5. De-register application instance
    ```
    DELETE /eureka/apps/NEO4J/neo4j.dev.brinkus.com
    ```