package com.brinkus.neo4j.eureka.type.config;

/**
 * The registration information.
 */
public class Registration {

    private String hostname;

    private String name;

    private String vipAddress;

    private RegistrationPort port;

    private RegistrationPort securePort;

    private String statusPageUrl;

    private String healthCheckUrl;

    private String homePageUrl;

    public String getHostname() {
        return hostname;
    }

    public void setHostname(final String hostname) {
        this.hostname = hostname;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getVipAddress() {
        return vipAddress;
    }

    public void setVipAddress(final String vipAddress) {
        this.vipAddress = vipAddress;
    }

    public RegistrationPort getPort() {
        return port;
    }

    public void setPort(final RegistrationPort port) {
        this.port = port;
    }

    public RegistrationPort getSecurePort() {
        return securePort;
    }

    public void setSecurePort(final RegistrationPort securePort) {
        this.securePort = securePort;
    }

    public String getStatusPageUrl() {
        return statusPageUrl;
    }

    public void setStatusPageUrl(final String statusPageUrl) {
        this.statusPageUrl = statusPageUrl;
    }

    public String getHealthCheckUrl() {
        return healthCheckUrl;
    }

    public void setHealthCheckUrl(final String healthCheckUrl) {
        this.healthCheckUrl = healthCheckUrl;
    }

    public String getHomePageUrl() {
        return homePageUrl;
    }

    public void setHomePageUrl(final String homePageUrl) {
        this.homePageUrl = homePageUrl;
    }
}
