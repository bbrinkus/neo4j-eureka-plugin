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

package com.brinkus.labs.neo4j.eureka.component;

import com.brinkus.labs.neo4j.eureka.exception.RestClientException;
import com.brinkus.labs.neo4j.eureka.type.config.Registration;
import org.apache.commons.lang3.Validate;
import org.neo4j.logging.FormattedLog;
import org.neo4j.logging.Log;
import org.neo4j.procedure.Context;

/**
 * Shutdown hook that will send a delete request to the discovery service to de-register the current application.
 */
public class ShutdownHook {

    /**
     * Builder to create a new instance of {@link ShutdownHook}.
     */
    public static final class Builder {

        private Registration registration;

        private RestClient restClient;

        /**
         * Set the registration object.
         *
         * @param registration
         *         the {@link Registration} instance
         *
         * @return the builder object
         */
        public Builder withRegistration(final Registration registration) {
            this.registration = registration;
            return this;
        }

        /**
         * Set the rest client.
         *
         * @param restClient
         *         the {@link RestClient} instance
         *
         * @return the builder object
         */
        public Builder withRestClient(final RestClient restClient) {
            this.restClient = restClient;
            return this;
        }

        /**
         * Build a new instance of {@link ShutdownHook}.
         *
         * @return the new instance.
         */
        public ShutdownHook build() {
            validateRequiredFields();
            return new ShutdownHook(registration, restClient);
        }

        /**
         * Check that the required fields are there.
         */
        private void validateRequiredFields() {
            Validate.notNull(registration);
            Validate.notNull(restClient);
        }
    }

    private final Log log = FormattedLog.toOutputStream(System.out);

    private final Registration registration;

    private final RestClient restClient;

    /**
     * Create a new instance of {@link ShutdownHook}
     *
     * @param registration
     *         the discovery service registration information
     * @param restClient
     *         the rest client
     */
    ShutdownHook(Registration registration, RestClient restClient) {
        this.registration = registration;
        this.restClient = restClient;
    }

    /**
     * Execute the de-registration process.
     *
     * @return true if the process was success.
     */
    public boolean execute() {
        log.info("Shutting down service");
        String uri = getUri();
        try {
            restClient.delete(uri, RestClient.STATUS_OK);
            return true;
        } catch (RestClientException e) {
            // just log the exception because were are already shutting down
            log.warn("An error occurred during the request", e);
            return false;
        }
    }

    private String getUri() {
        return String.format("/eureka/apps/%s/%s", registration.getName(), registration.getHostname());
    }

}
