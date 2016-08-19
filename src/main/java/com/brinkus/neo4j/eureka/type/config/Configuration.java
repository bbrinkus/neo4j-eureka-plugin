package com.brinkus.neo4j.eureka.type.config;

import java.util.List;

/**
 * The plugin configuration settings.
 */
public class Configuration {

    private List<Service> services;

    private Registration registration;

    public List<Service> getServices() {
        return services;
    }

    public void setServices(final List<Service> services) {
        this.services = services;
    }

    public Registration getRegistration() {
        return registration;
    }

    public void setRegistration(final Registration registration) {
        this.registration = registration;
    }

}
