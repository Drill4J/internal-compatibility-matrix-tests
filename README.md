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

| Name      | Versions   | JDK   | Tests                                            | Notes |
|-----------|------------|-------|--------------------------------------------------|-------|
| Tomcat    | 10.1.19    | 11    | [tomcat-10.1](./tests/web-servers/tomcat-10.1)   |       |
| Tomcat    | 11.0.0-M18 | 17    | [tomcat-11.0](./tests/web-servers/tomcat-11.0)                     |       |
| Jetty     | 10.0.20    | 11-17 | [jetty-10.0](./tests/web-servers/jetty-10.0)                       |       |
| Netty     | 4.1.107    | 8-17  | [netty-4.1](./tests/web-servers/netty-4.1)                         |       |
| Undertow  | 2.3.12     | 11-17 | [undertow-2.3](./tests/web-servers/undertow-2.3)                   |       |
| Glassfish | 7.0        | 11-17 | [servlet-5-glassfish-7](./tests/web-servers/servlet-5-glassfish-7) |       |
| Wildfly   |            |       |                                                  |       |

### Web Frameworks

| Name                                                                                 | Versions | JDK  | Tests                                                                                                                                                                                                                                                                                                                               | Notes |
|--------------------------------------------------------------------------------------|----------|------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-------|
| [HttpServlet](https://jakarta.ee/specifications/servlet/)                            | 4.0      | 8-17 | [servlet-4-tomcat-9](./tests/web-frameworks/servlet-4-tomcat-9)<br/>[servlet-4-jetty-9](./tests/web-frameworks/servlet-4-jetty-9)                                                                                                                                                                                                   |       |
| [HttpServlet](https://jakarta.ee/specifications/servlet/)                            | 5.0      | 8-17 | [servlet-5-tomcat-10](./tests/web-frameworks/servlet-5-tomcat-10)<br/>[servlet-5-jetty-11](./tests/web-frameworks/servlet-5-jetty-11)<br/>[servlet-5-undertow-2](./tests/web-frameworks/servlet-5-undertow-2)<br/>[servlet-5-glassfish-7](./tests/web-frameworks/servlet-5-glassfish-7)                                             |       |
| [HttpServlet](https://jakarta.ee/specifications/servlet/)                            | 6.0      | 17   | [servlet-6-tomcat-11](./tests/web-frameworks/servlet-6-tomcat-11)                                                                                                                                                                                                                                                                   |       |
| [Spring MVC](https://docs.spring.io/spring-framework/reference/web/webmvc.html)      | 4.3.25   | 8-17 | [spring-mvc-1.5-tomcat](./tests/web-frameworks/spring-mvc-1.5-tomcat)<br/>[spring-mvc-1.5-jetty](./tests/web-frameworks/spring-mvc-1.5-jetty)<br/>[spring-mvc-1.5-undertow](./tests/web-frameworks/spring-mvc-1.5-undertow)                                                                                                         |       |
| [Spring MVC](https://docs.spring.io/spring-framework/reference/web/webmvc.html)      | 5.3.31   | 8-17 | [spring-mvc-2.7-tomcat](./tests/web-frameworks/spring-mvc-2.7-tomcat)<br/>[spring-mvc-2.7-jetty](./tests/web-frameworks/spring-mvc-2.7-jetty)<br/>[spring-mvc-2.7-undertow](./tests/web-frameworks/spring-mvc-2.7-undertow)                                                                                                         |       |
| [Spring MVC](https://docs.spring.io/spring-framework/reference/web/webmvc.html)      | 6.0.17   | 17   | [spring-mvc-3.1-tomcat](./tests/web-frameworks/spring-mvc-3.1-tomcat)<br/>[spring-mvc-3.1-jetty](./tests/web-frameworks/spring-mvc-3.1-jetty)<br/>[spring-mvc-3.1-undertow](./tests/web-frameworks/spring-mvc-3.1-undertow)                                                                                                         |       |
| [Spring WebFlux](https://docs.spring.io/spring-framework/reference/web/webflux.html) | 5.3.31   | 8-17 | [spring-webflux-2.7-netty](./tests/web-frameworks/spring-webflux-2.7-netty)<br/>[spring-webflux-2.7-tomcat](./tests/web-frameworks/spring-webflux-2.7-tomcat)<br/>[spring-webflux-2.7-jetty](./tests/web-frameworks/spring-webflux-2.7-jetty)<br/>[spring-webflux-2.7-undertow](./tests/web-frameworks/spring-webflux-2.7-undertow) |       |
| [Spring WebFlux](https://docs.spring.io/spring-framework/reference/web/webflux.html) | 6.0.17   | 17   | [spring-webflux-3.1-netty](./tests/web-frameworks/spring-webflux-3.1-netty)<br/>[spring-webflux-3.1-tomcat](./tests/web-frameworks/spring-webflux-3.1-tomcat)<br/>[spring-webflux-3.1-jetty](./tests/web-frameworks/spring-webflux-3.1-jetty)<br/>[spring-webflux-3.1-undertow](./tests/web-frameworks/spring-webflux-3.1-undertow) |       |   
| [Eclipse Jersey](https://eclipse-ee4j.github.io/jersey/)                             | 2        | 8-17 | [jersey-2-jetty](./tests/web-frameworks/jersey-2-jetty)                                                                                                                                                                                                                                                                             |       |
| [Apache CXF](https://cxf.apache.org/)                                                | 3.4      | 8-17 | [cxf-3.4-jetty](./tests/web-frameworks/cxf-3.4-jetty)                                                                                                                                                                                                                                                                               |       |

### Asynchronous Communication

| Name                                                                                                     | Versions | JDK  | Tests                                              | Notes |
|----------------------------------------------------------------------------------------------------------|----------|------|----------------------------------------------------|-------|
| [ExecutorService](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ExecutorService.html)   | jdk      | 8-17 | [executor-service](./tests/async/executor-service) |       |
| [Reactor](https://projectreactor.io/)                                                                    | 3.5      | 8-17 | [reactor-3.5](./tests/async/reactor-3.5)           |       |
| [Reactor](https://projectreactor.io/)                                                                    | 3.6      | 8-17 | [reactor-3.6](./tests/async/reactor-3.6)           |       |
| [Spring Task Execution](https://docs.spring.io/spring-framework/reference/integration/scheduling.html)   | 6.1      | 17   | [spring-task-execution-3.1](./tests/async/spring-task-execution-3.1)   |      |

### HTTP Clients

| Name                | Versions | JDK  | Tests                                                                   | Notes |
|---------------------|----------|------|-------------------------------------------------------------------------|-------|
| URLConnection       | jdk      | 8-17 | [urlconnection](./tests/http-clients/urlconnection)                     |       |
| Apache HttpClient   | 4.5      | 8-17 | [apache-http-client-4.5](./tests/http-clients/apache-http-client-4.5)   |       |
| Apache HttpClient   | 5.3      | 8-17 | [apache-http-client-5.3](./tests/http-clients/apache-http-client-5.3)   |       |
| OkHttp Client       | 3.12     | 8-17 | [okhttp-client-3.12](./tests/http-clients/okhttp-client-3.12)           |       |
| OkHttp Client       | 3.14     | 8-17 | [okhttp-client-3.14](./tests/http-clients/okhttp-client-3.14)           |       |
| OkHttp Client       | 4.12     | 8-17 | [okhttp-client-4.12](./tests/http-clients/okhttp-client-4.12)           |       |
| Spring RestTemplate | 4.3      | 8-17 | [spring-resttemplate-4.3](./tests/http-clients/spring-resttemplate-4.3) |       |
| Spring RestTemplate | 5.3      | 8-17 | [spring-resttemplate-5.3](./tests/http-clients/spring-resttemplate-5.3) |       |
| Spring WebClient    | 5.3      | 8-17 | [spring-webclient-5.3](./tests/http-clients/spring-webclient-5.3)       |       |
| Spring WebClient    | 6.1      | 17   | [spring-webclient-6.1](./tests/http-clients/spring-webclient-6.1)       |       |
| Feign Client        | 13       | 8-17 | [feign-client-13](./tests/http-clients/feign-client-13)                 |       |

### WebSocket

| Name             | Versions | JDK | Tests | Notes |
|------------------|----------|-----|-------|-------|
| Spring WebSocket |          |     |       |       |
| Spring WebFlux   |          |     |       |       |

### Messaging

| Name             | Versions | JDK | Tests | Notes |
|------------------|----------|-----|-------|-------|
| Kafka Client     |          |     |       |       |
| Spring Messaging |          |     |       |       |
