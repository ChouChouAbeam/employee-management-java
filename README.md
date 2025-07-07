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
## Using XSUAA with Hybrid Mode
### Config the `aplication.yaml` with profile cloud
```

spring:
  config.activate.on-profile: cloud

```

### Add dependencies for `pom.xml`
```

<dependency>
  <groupId>com.sap.cloud.security.xsuaa</groupId>
  <artifactId>spring-xsuaa</artifactId>
  <version>3.6.0</version>
</dependency>

```

### Run xsuaa service with Hybrid Mode
**Add xsuaa service and bind it in** [this tutorial](https://cap.cloud.sap/docs/node.js/authentication#xsuaa-setup)

Add App Router to the `app` folder of your project:
```

cds add approuter

```
Install npm packages for App Router:
```

npm install --prefix app/router

```
In your project folder run in port 5000:

```

cds bind --exec -- npm start --prefix app/router

```

### Run project with Hybrid Mode
```

SPRING_PROFILES_ACTIVE=cloud cds bind --exec -- mvn spring-boot:run

```
