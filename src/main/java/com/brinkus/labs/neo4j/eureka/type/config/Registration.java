/*
 * Netflix Eureka Client Plugin for Neo4j
 * Copyright (C) 2016  Balazs Brinkus
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.brinkus.labs.neo4j.eureka.type.config;

/**
 * The registration information for the discovery service.
 */
public class Registration {

    /**
     * The fully qualified hostname.
     */
    private String hostname;

    /**
     * The instance's ip address.
     */
    private String ipAddress;

    /**
     * Use the fully qualified hostname instead of the aws hostname.
     */
    private boolean awsDnsHostname;

    /**
     * The application's name of the instance.
     */
    private String name;

    /**
     * The instance's Virtual Internet Protocol address.
     */
    private String vipAddress;

    /**
     * The instance's unsecure port.
     */
    private RegistrationPort port;

    /**
     * The instance's secure port.
     */
    private RegistrationPort securePort;

    /**
     * The instance's status page url.
     */
    private String statusPageUrl;

    /**
     * The instance's health check url.
     */
    private String healthCheckUrl;

    /**
     * The instance's start page url.
     */
    private String homePageUrl;

    public String getHostname() {
        return hostname;
    }

    public void setHostname(final String hostname) {
        this.hostname = hostname;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public Registration setIpAddress(final String ipAddress) {
        this.ipAddress = ipAddress;
        return this;
    }

    public boolean isAwsDnsHostname() {
        return awsDnsHostname;
    }

    public boolean useAwsDnsHostname() {
        return awsDnsHostname;
    }

    public Registration setAwsDnsHostname(final boolean awsDnsHostname) {
        this.awsDnsHostname = awsDnsHostname;
        return this;
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
