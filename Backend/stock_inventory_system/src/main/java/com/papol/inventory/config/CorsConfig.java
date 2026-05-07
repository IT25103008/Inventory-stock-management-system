package com.papol.inventory.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

// WHY: CORS (Cross-Origin Resource Sharing) is a browser security mechanism.
//      When the frontend (running on port 3000 or 5500) sends a request to the
//      backend (running on port 8080), the browser blocks it by default because
//      they are on different "origins" (different ports = different origins).
//      This configuration tells the browser: "allow requests from any origin."
// WHAT: '@Configuration' marks this as a Spring configuration class. It implements
//       WebMvcConfigurer to customise Spring's MVC settings.
// NOTE: In a production system, you would restrict allowedOrigins to specific domains
//       instead of "*" (wildcard). For development and this project, "*" is acceptable.

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    // WHAT: Registers a CORS mapping for ALL routes ("/**").
    //       Allows all origins ("*") and all common HTTP methods.
    //       Without this, every fetch() call from JavaScript would fail with a
    //       "CORS policy" error in the browser console.
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET","POST","PUT","DELETE","OPTIONS");
    }
}