# Compatibility Matrix Tests

## Project Overview

This project builds a compatibility matrix for Java agents across various environments, ensuring proper functioning in diverse server configurations.

Environments in which the tests are run:

1. Sync Jetty
2. Sync Undertow
3. Sync Tomcat
4. Spring Webflux + Netty
5. Spring Webflux + Jetty
6. Spring Webflux + Undertow
7. Spring Webflux + Tomcat

## Running the Tests
To run the tests, execute the following command:

```
./gradlew clean build
```
