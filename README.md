# RememberCardServer
## Configure
### Step 1
Create ***application.conf***
```
ktor {
  deployment {
    sslPort = 8080
    rootPath = "/"
  }
  application {
    modules = [com.uogames.dictionary.ApplicationKt.module]
  }
  security {
    ssl {
      keyStore = keystore.jks
      keyAlias = key0
      keyStorePassword = password
      privateKeyPassword = password
    }
  }
  hikariConfig = "dbconfig.properties"
  rootDir = "var/lib/files"
}
jwt {
  secret = "secret"
```
- **sslPort** - it is the port that you are going to use for connection. It possible to use just "port". Fore example **port = 8080**. If you are going to run just **port** without **sslPort** you don't need the **security** block.
- **rootPath** - it is a prefix path for our app. Fore example with **rootPath = "/test"** access is going to look like **http://localhost:8080/test/...**
- **keyStore** - it is a path to your keystore. Fore examle **keyStore = keystore.jks**.
- **keyAlias** - it is aa alias for your keystore.
- **keyStorePassword** - it is a password for your keystore.
- **privateKeyPassword** - it is a key alias password.
- **hikariConfig** - it is a path for database config.
- **rootDir** - it is a path where you are going to save user's files.
- **secret** - it use for verify jwt tokens.
### Step 2
Create ***dbconfig.properties***
```
driverClassName=org.postgresql.Driver
jdbcUrl=jdbc:postgresql://db:5432/test
dataSource.rootUrl=jdbc:postgresql://db:5432/postgres
dataSource.user=root
dataSource.password=root
dataSource.databaseName=test
```
- **jdbcUrl** - it is a url to our database.
- **dataSource.rootUrl** - it is a root database. It use to create your database if it doesn't exist.
## Run
### Run Windows
```java -jar fat.jar -config=application.conf```
### Run Docker
#### Step 1
Create ***Dockerfile***
```
FROM openjdk:17
8080:8080
RUN mkdir /app
COPY fat.jar /app/fat.jar
COPY *.jks /app/keystore.jks
COPY dbconfig.properties /app/dbconfig.properties
COPY application.conf /app/application.conf
ENTRYPOINT ["java","-jar","/app/fat.jar","-config=/app/application.conf"]
```
If you don't use **ssl** don't need **COPY *.jks /app/keystore.jks**
#### Step 2
Create ***docker-compose.yml***
```
services:
web:
  volumes:
    - parth:/var/lib/files
  image: ktor-test
  build: .
  ports:
    - "8080:8080"
  depends_on:
    db:
      condition: service_healthy
db:
  image: postgres:14.6
  volumes:
    - path:/var/lib/postgresql/data
  environment:
    POSTGRES_DB: test
    POSTGRES_USER: root
    POSTGRES_PASSWORD: root
    POSTGRES_HOST_AUTH_METHOD: trust
  ports:
    - "54334:5432"
  healthcheck:
    test: [ "CMD-SHELL", "pg_isready -d test" ]
    interval: 1s
```
#### Step 3
run in console
```
docker compose down --rmi local
docker build -t test-image .
docker compose up -d
```
