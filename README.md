# Compatibility Tests

## Project Overview

This project tests the Drill4J Java Agent for compatibility with different versions of JDK, web servers and frameworks.
Based on the testing results, we fill out the compatibility matrix.

## Running a separate test
To run a separate test against a specific JDK, follow these steps.

Build the test-agent:
```
./gradlew clean :test-agent:build :test-agent:runtimeJar
```

Copy the test-agent files to the directory [path].

Run the test module with the test-agent:
```
./gradlew clean :[test module]:test -Dtest-agent.binaries=[path]
```


## Compatibility Matrix

### Web Servers

| Name     | Versions | JDK | Notes | Tests |
|----------|----------|-----|-------|-------|
| Tomcat   |          |     |       |       |
| Jetty    |          |     |       |       |
| Netty    |          |     |       |       |
| Undertow |          |     |       |       |
| Wildfly  |          |     |       |       |

### Web Frameworks

| Name                                                                                 | Versions | JDK   | Notes                                 | Tests                                          |
|--------------------------------------------------------------------------------------|---------|-------|---------------------------------------|------------------------------------------------|
| [HttpServlet](https://jakarta.ee/specifications/servlet/)                            | 4.0.0   | 8-11  | Tested on Tomcat 9                    | [servlet-4-tomcat-9](./servlet-4-tomcat-9)     |
| [HttpServlet](https://jakarta.ee/specifications/servlet/)                            | 5.0.0   | 8-11  | Tested on Tomcat 10                   | [servlet-5-tomcat-10](./servlet-5-tomcat-10)   |
| [HttpServlet](https://jakarta.ee/specifications/servlet/)                            | 6.0.0   | 17    | Tested on Tomcat 11                   | [servlet-6-tomcat-11](./servlet-6-tomcat-11)   |
| [Spring MVC](https://docs.spring.io/spring-framework/reference/web/webmvc.html)      | 1.5.22  | 8-11  | Tested on Spring Boot Tomcat Embedded | [spring-1.5.22-tomcat](./spring-1.5.22-tomcat) |
| [Spring MVC](https://docs.spring.io/spring-framework/reference/web/webmvc.html)      | 2.7.18  | 11-17 | Tested on Spring Boot Tomcat Embedded | [spring-2.7.18-tomcat](./spring-2.7.18-tomcat) |
| [Spring MVC](https://docs.spring.io/spring-framework/reference/web/webmvc.html)      | 3.1.9   | 17-21 | Tested on Spring Boot Tomcat Embedded | [spring-3.1.9-tomcat](./spring-3.1.9-tomcat)   |
| [Spring WebFlux](https://docs.spring.io/spring-framework/reference/web/webflux.html) |         |       |                                       |                                                | 
| [Eclipse Jersey](https://eclipse-ee4j.github.io/jersey/)                             |          |       |                                       |                                                |
| [Apache CXF](https://cxf.apache.org/)                                                |         |       |                                       |                                                |
| [Resteasy](https://resteasy.dev/)                                                    |         |       |                                       |                                                |

### Asynchronous Communication

| Name              | Versions | JDK | Notes | Tests |
|-------------------|----------|-----|-------|-------|
| ExecutorService   |          |     |       |       |
| Reactor           |          |     |       |       |
| JavaRX            |          |     |       |       |
| Spring Scheduler  |          |     |       |       |
| Quartz            |          |     |       |       |

### HTTP Clients

| Name                   | Versions | JDK | Notes | Tests |
|------------------------|----------|-----|-------|-------|
| URLConnection          |          |     |       |       |
| OkHTTP Client          |          |     |       |       |
| Apache HttpClient      |          |     |       |       |
| Apache AsyncHttpClient |          |     |       |       |
| Spring RestTemplate    |          |     |       |       |
| Spring WebClient       |          |     |       |       |
| Feign Client           |          |     |       |       |

### WebSocket

| Name             | Versions | JDK | Notes | Tests |
|------------------|----------|-----|-------|-------|
| Spring WebSocket |          |     |       |       |
| Spring Webflux   |          |     |       |       |

### gRPC

| Name             | Versions | JDK | Notes | Tests |
|------------------|----------|-----|-------|-------|
| Spring gRPC      |          |     |       |       |

### Messaging

| Name             | Versions | JDK | Notes | Tests |
|------------------|----------|-----|-------|-------|
| Kafka Client     |          |     |       |       |
| RabbitMQ Client  |          |     |       |       |
| ActiveMQ Client  |          |     |       |       |
| Spring Messaging |          |     |       |       |
