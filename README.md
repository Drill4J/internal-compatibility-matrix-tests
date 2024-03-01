# Compatibility Matrix Tests

## Project Overview

This project is used for building a compatibility matrix. It runs the simplest Java agent in various environments, with the primary goal of determining compatibility between different server configurations. This could help developers ensure that their Java agents work correctly across a range of runtime environments.

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

This README file provides a brief description of the project, lists the environments where the tests are executed, and provides instructions on how to run the tests.