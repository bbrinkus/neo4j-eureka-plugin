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
     * The instance's unsecured port.
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

    /**
     * Get the fully qualified hostname.
     *
     * @return fully qualified hostname.
     */
    public String getHostname() {
        return hostname;
    }

    /**
     * Set the fully qualified hostname.
     *
     * @param hostname
     *         fully qualified hostname.
     */
    public void setHostname(final String hostname) {
        this.hostname = hostname;
    }

    /**
     * Get the instance's ip address.
     *
     * @return the ip address.
     */
    public String getIpAddress() {
        return ipAddress;
    }

    /**
     * Set the instance's ip address.
     *
     * @param ipAddress
     *         the ip address.
     */
    public void setIpAddress(final String ipAddress) {
        this.ipAddress = ipAddress;
    }

    /**
     * Flag to indicate to use the fully qualified hostname instead of the aws hostname.
     *
     * @return the flag value (default false)
     */
    public boolean useAwsDnsHostname() {
        return awsDnsHostname;
    }

    /**
     * Set the use the aws hostname.
     *
     * @param awsDnsHostname
     *         the aws hostname usage state.
     */
    public void setAwsDnsHostname(final boolean awsDnsHostname) {
        this.awsDnsHostname = awsDnsHostname;
    }

    /**
     * Get the application's name of the instance.
     *
     * @return the application's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Set the application's name of the instance.
     *
     * @param name
     *         the application's name.
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Get the instance's Virtual Internet Protocol address.
     *
     * @return the VIP address.
     */
    public String getVipAddress() {
        return vipAddress;
    }

    /**
     * Set the instance's Virtual Internet Protocol address.
     *
     * @param vipAddress
     *         the VIP address.
     */
    public void setVipAddress(final String vipAddress) {
        this.vipAddress = vipAddress;
    }

    /**
     * Get the instance's unsecured port.
     *
     * @return the unsecured port.
     */
    public RegistrationPort getPort() {
        return port;
    }

    /**
     * Set the instance's unsecured port.
     *
     * @param port
     *         the unsecured port.
     */
    public void setPort(final RegistrationPort port) {
        this.port = port;
    }

    /**
     * Get the instance's secure port.
     *
     * @return the secure port.
     */
    public RegistrationPort getSecurePort() {
        return securePort;
    }

    /**
     * Set the instance's secure port.
     *
     * @param securePort
     *         the secure port.
     */
    public void setSecurePort(final RegistrationPort securePort) {
        this.securePort = securePort;
    }

    /**
     * Get the instance's status page url.
     *
     * @return the status page url.
     */
    public String getStatusPageUrl() {
        return statusPageUrl;
    }

    /**
     * Set the instance's status page url.
     *
     * @param statusPageUrl
     *         the status page url.
     */
    public void setStatusPageUrl(final String statusPageUrl) {
        this.statusPageUrl = statusPageUrl;
    }

    /**
     * Get the instance's health check url.
     *
     * @return the health check url.
     */
    public String getHealthCheckUrl() {
        return healthCheckUrl;
    }

    /**
     * Set the instance's health check url.
     *
     * @param healthCheckUrl
     *         the health check url.
     */
    public void setHealthCheckUrl(final String healthCheckUrl) {
        this.healthCheckUrl = healthCheckUrl;
    }

    /**
     * Get the instance's start page url.
     *
     * @return the start page url.
     */
    public String getHomePageUrl() {
        return homePageUrl;
    }

    /**
     * Set the instance's start page url.
     *
     * @param homePageUrl
     *         the start page url.
     */
    public void setHomePageUrl(final String homePageUrl) {
        this.homePageUrl = homePageUrl;
    }
}
