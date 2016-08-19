package com.brinkus.neo4j.eureka.type.config;

/**
 * The used port and state for the registration.
 */
public class RegistrationPort {

    private int port;

    private boolean enabled;

    public int getPort() {
        return port;
    }

    public void setPort(final int port) {
        this.port = port;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

}
