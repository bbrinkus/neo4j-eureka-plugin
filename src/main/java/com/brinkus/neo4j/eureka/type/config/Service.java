package com.brinkus.neo4j.eureka.type.config;

/**
 * The host and port information of the discovery service.
 */
public class Service {

    private String host;

    private int port;

    public String getHost() {
        return host;
    }

    public void setHost(final String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(final int port) {
        this.port = port;
    }
}
