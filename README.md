# HAPI-FHIR Starter Project, Sensyne Health edition

This project was initially cloned from [the original HAPI FHIR JPA Server Starter repo](https://github.com/hapifhir/hapi-fhir-jpaserver-starter) and is a "custom" version of it.  
  
## Changes made:
- `BasicAuth` class has been created, containing all necessary functions for HTTP Basic Auth implementation;
- `BasicAuthorizationInterceptor` has been created as an `AuthInterceptor` implementation - it intercepts incoming requests to the HAPI FHIR server and serves as an auth layer, comparing credentials in the header with `hapi.fhir.server_client_id` and `hapi.fhir.server_client_secret`;
- `SecurityServletFilter` has been created as an `HttpFilter` implementation - it filters incoming requests to the HAPI FHIR Tester App and serves as an auth layer.

## Prerequisites
### either
- Oracle Java (JDK) 8 or newer;
- Apache Maven build tool (the newer, the better)
### or
- Docker

## Running locally
### either
```bash
mvn jetty:run
```
### or
```bash
docker pull sensynehealth.azurecr.io/dhos/dhos-hapi-fhir-api:dev
docker run -p 80:8080 sensynehealth.azurecr.io/dhos/dhos-hapi-fhir-api:dev
```
### or
```bash
docker-compose up
```


## Testing locally
The project needs to be built before executing the command below.
```bash
mvn failsafe:integration-test
```

## Configuration
HAPI looks in the environment variables for properties in the [application.yaml](https://github.com/draysontechnologies/dhos-hapi-fhir-api/blob/develop/src/main/resources/application.yaml) file **for defaults**, but it's also possible to override those properties by passing them as docker environment variables, e.g.:
```bash
docker run -p 80:8080 -e hapi.fhir.default_encoding=xml sensynehealth.azurecr.io/dhos/dhos-hapi-fhir-api:dev
```

You may also want to use another `application.yaml` file instead. It's possible by simply passing a path to it as an environment variable:

```bash
docker run -p 80:8080 -v $(pwd)/yourLocalFolder:/configs -e "--spring.config.location=file:///configs/another.application.yaml" sensynehealth.azurecr.io/dhos/dhos-hapi-fhir-api:dev
```

## Useful maven instructions
- `mvn clean` - cleans the `target` directory content
- `mvn package` - builds the project and stores into the `target` directory