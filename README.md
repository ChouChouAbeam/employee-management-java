# CAP java 
## Create project automatic with **Create project Build from Template**
<img width="892" alt="image" src="https://github.com/user-attachments/assets/a3435b21-7cbd-4969-a0ad-5b4df7f6ed01" />

**Run sample project with**

```

mvn spring-boot:run

```
## Using hana database with Hybrid Mode
### Config the `aplication.yaml` with profile cloud
```

spring:
  config.activate.on-profile: cloud
cds:
  data:
    db:
      vcapp: true

```
### Add the `default-env.json` for database information
```

{
  "VCAP_SERVICES": {
    "hana": [
      {
        "name": "db",
        "label": "hana",
        "tags": ["hana", "database", "relational"],
        "credentials": {
          "url": "jdbc:sap://",
          "user": "",
          "password": "",
          "schema": "",
          "driver": "com.sap.db.jdbc.Driver",
          "host": "",
          "port": "443"
        }
      }
    ]
  }
}

```

### Add dependencies for `pom.xml`
```

<dependency>
  <groupId>com.sap.cds</groupId>
  <artifactId>cds-feature-hana</artifactId>
  <scope>runtime</scope>
</dependency>

```

### Run `cds add hana`
Adding hana database binding and deploy to hana
```

cds add hana
cds deploy -2 hana

```

### Run project with Hybrid Mode
```

mvn spring-boot:run -Dspring-boot.run.profiles=cloud

```
