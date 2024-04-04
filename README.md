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

| Name      | Versions   | JDK   | Tests                                                | Notes |
|-----------|------------|-------|------------------------------------------------------|-------|
| Tomcat    | 10.1.19    | 11    | [clean-tomcat-10.1.19](./clean-tomcat-10.1.19)       |       |
| Tomcat    | 11.0.0-M18 | 17    | [clean-tomcat-11.0.0-M18](./clean-tomcat-11.0.0-M18) |       |
| Jetty     | 10.0.20    | 11-17 | [clean-jetty-10.0.20](./clean-jetty-10.0.20)         |       |
| Netty     | 4.1.107    | 8-17  | [clean-netty-4.1.107](./clean-netty-4.1.107)         |       |
| Undertow  | 2.3.12     | 11-17 | [clean-undertow-2.3.12](./clean-undertow-2.3.12)     |       |
| Glassfish | 7.0        | 11-17 | [servlet-5-glassfish-7](./servlet-5-glassfish-7)                                                     |       |
| Wildfly   |            |       |                                                      |       |

### Web Frameworks

| Name                                                                                 | Versions | JDK  | Tests                                                                                                                                                                                                  | Notes |
|--------------------------------------------------------------------------------------|----------|------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-------|
| [HttpServlet](https://jakarta.ee/specifications/servlet/)                            | 4.0      | 8-17 | [servlet-4-tomcat-9](./servlet-4-tomcat-9)<br/>[servlet-4-jetty-9](./servlet-4-jetty-9)                                                                                                                |       |
| [HttpServlet](https://jakarta.ee/specifications/servlet/)                            | 5.0      | 8-17 | [servlet-5-tomcat-10](./servlet-5-tomcat-10)<br/>[servlet-5-jetty-11](./servlet-5-jetty-11)<br/>[servlet-5-glassfish-7](./servlet-5-glassfish-7)                                                       |       |
| [HttpServlet](https://jakarta.ee/specifications/servlet/)                            | 6.0      | 17   | [servlet-6-tomcat-11](./servlet-6-tomcat-11)                                                                                                                                                           |       |
| [Spring MVC](https://docs.spring.io/spring-framework/reference/web/webmvc.html)      | 4.3.25   | 8-17 | [spring-1.5.22-tomcat](./spring-1.5.22-tomcat)<br/>[spring-1.5.22-jetty](./spring-1.5.22-jetty)<br/>[spring-1.5.22-undertow](./spring-1.5.22-undertow)                                                 |       |
| [Spring MVC](https://docs.spring.io/spring-framework/reference/web/webmvc.html)      | 5.3.31   | 8-17 | [spring-2.7.18-tomcat](./spring-2.7.18-tomcat)<br/>[spring-2.7.18-jetty](./spring-2.7.18-jetty)<br/>[spring-2.7.18-undertow](./spring-2.7.18-undertow)                                                 |       |
| [Spring MVC](https://docs.spring.io/spring-framework/reference/web/webmvc.html)      | 6.0.17   | 17   | [spring-3.1.9-tomcat](./spring-3.1.9-tomcat)<br/>[spring-3.1.9-jetty](./spring-3.1.9-jetty)<br/>[spring-3.1.9-undertow](./spring-3.1.9-undertow)                                                       |       |
| [Spring WebFlux](https://docs.spring.io/spring-framework/reference/web/webflux.html) | 5.3.31   | 8-17 | [spring-2.7.18-webflux-tomcat](./spring-2.7.18-webflux-tomcat)<br/>[spring-2.7.18-webflux-jetty](./spring-2.7.18-webflux-jetty)<br/>[spring-2.7.18-webflux-undertow](./spring-2.7.18-webflux-undertow) |       |
| [Spring WebFlux](https://docs.spring.io/spring-framework/reference/web/webflux.html) | 6.0.17   | 17   | [spring-3.1.9-webflux-tomcat](./spring-3.1.9-webflux-tomcat)<br/>[spring-3.1.9-webflux-jetty](./spring-3.1.9-webflux-jetty)<br/>[spring-3.1.9-webflux-undertow](./spring-3.1.9-webflux-undertow)       |       |   
| [Eclipse Jersey](https://eclipse-ee4j.github.io/jersey/)                             |          |      |                                                                                                                                                                                                        |       |
| [Apache CXF](https://cxf.apache.org/)                                                |          |      |                                                                                                                                                                                                        |       |
| [Resteasy](https://resteasy.dev/)                                                    |          |      |                                                                                                                                                                                                        |       |

### Asynchronous Communication

| Name              | Versions | JDK | Tests | Notes |
|-------------------|----------|-----|-------|-------|
| ExecutorService   |          |     |       |       |
| Reactor           |          |     |       |       |
| JavaRX            |          |     |       |       |
| Spring Scheduler  |          |     |       |       |
| Quartz            |          |     |       |       |

### HTTP Clients

| Name                   | Versions | JDK | Tests | Notes |
|------------------------|----------|-----|-------|-------|
| URLConnection          |          |     |       |       |
| OkHTTP Client          |          |     |       |       |
| Apache HttpClient      |          |     |       |       |
| Apache AsyncHttpClient |          |     |       |       |
| Spring RestTemplate    |          |     |       |       |
| Spring WebClient       |          |     |       |       |
| Feign Client           |          |     |       |       |

### WebSocket

| Name             | Versions | JDK | Tests | Notes |
|------------------|----------|-----|-------|-------|
| Spring WebSocket |          |     |       |       |
| Spring Webflux   |          |     |       |       |

### gRPC

| Name             | Versions | JDK | Tests | Notes |
|------------------|----------|-----|-------|-------|
| Spring gRPC      |          |     |       |       |

### Messaging

| Name             | Versions | JDK | Tests | Notes |
|------------------|----------|-----|-------|-------|
| Kafka Client     |          |     |       |       |
| RabbitMQ Client  |          |     |       |       |
| ActiveMQ Client  |          |     |       |       |
| Spring Messaging |          |     |       |       |
