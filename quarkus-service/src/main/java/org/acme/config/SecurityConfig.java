package org.acme.config;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SecurityConfig {

    @ConfigProperty(name = "app.internal.allowed-origins", defaultValue = "http://localhost:3000")
    String allowedOrigins;

    public boolean isInternalRequest(String origin) {
        return allowedOrigins.contains(origin);
    }
}
