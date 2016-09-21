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
 * The Neo4j application's used port and it's state.
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

    /**
     * Get the port number.
     *
     * @return the port number.
     */
    public int getPort() {
        return port;
    }

    /**
     * Set the port number.
     *
     * @param port
     *         the port number.
     */
    public void setPort(final int port) {
        this.port = port;
    }

    /**
     * Flag to indicate that port is enable or not.
     *
     * @return the flag value (default false)
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Set the port to enabled or disable state.
     *
     * @param enabled
     *         the port state
     */
    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

}
