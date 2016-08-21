package com.brinkus.neo4j.eureka.type.config;

/**
 * The the Neo4j application's used port and it's state.
 */
public class RegistrationPort {

    /**
     * The port number.
     */
    private int port;

    /**
     * True if the port is enabled.
     */
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
