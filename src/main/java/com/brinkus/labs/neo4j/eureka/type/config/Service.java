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
 * The host and port information of the discovery service.
 */
public class Service {

    /**
     * The service's fully qualified hostname or ip address.
     */
    private String host;

    /**
     * The service's port number.
     */
    private int port;

    /**
     * Get the service's fully qualified hostname or ip address.
     *
     * @return the hostname or ip address
     */
    public String getHost() {
        return host;
    }

    /**
     * Set the service's fully qualified hostname or ip address.
     *
     * @param host
     *         the hostname or ip address
     */
    public void setHost(final String host) {
        this.host = host;
    }

    /**
     * Get the service's port number.
     *
     * @return the service's port number.
     */
    public int getPort() {
        return port;
    }

    /**
     * Set the service's port number.
     *
     * @param port
     *         service's port number.
     */
    public void setPort(final int port) {
        this.port = port;
    }
}
