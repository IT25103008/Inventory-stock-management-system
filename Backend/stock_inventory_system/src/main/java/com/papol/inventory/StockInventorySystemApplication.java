package com.papol.inventory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// WHY: This is the APPLICATION ENTRY POINT — the very first class that runs
//      when you start the project. Spring Boot uses this to bootstrap the
//      entire application: it auto-discovers all your @Entity, @Service,
//      @Repository, and @Controller classes and wires them together.
// WHAT: '@SpringBootApplication' is a shortcut that combines three annotations:
//       1. @Configuration — marks this class as a source of bean definitions
//       2. @EnableAutoConfiguration — tells Spring to auto-configure based on
//          dependencies (e.g., seeing JPA + MySQL deps → auto-configure Hibernate)
//       3. @ComponentScan — scans all packages under 'com.papol.inventory' to
//          find classes annotated with @Service, @Controller, @Repository, etc.
// NOTE: SpringApplication.run() starts the embedded Tomcat web server on port 8080
//       (configured in application.properties). You access the API at localhost:8080.

@SpringBootApplication
public class StockInventorySystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(StockInventorySystemApplication.class, args);
    }

}
