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

package com.brinkus.labs.neo4j.eureka.exception;

/**
 * Top level exception for the plugin related errors.
 */
public class EurekaPluginException extends Exception {

    /**
     * Create a new instance of {@link EurekaPluginException}.
     *
     * @param message
     *         the detail message.
     */
    public EurekaPluginException(final String message) {
        super(message);
    }

    /**
     * Create a new instance of {@link EurekaPluginException}.
     *
     * @param message
     *         the detail message.
     * @param cause
     *         the cause of the error.
     */
    public EurekaPluginException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
