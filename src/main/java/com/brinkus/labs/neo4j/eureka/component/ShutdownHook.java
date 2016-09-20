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

import com.brinkus.labs.neo4j.eureka.exception.EurekaPluginException;
import org.apache.commons.lang3.Validate;
import org.neo4j.logging.FormattedLog;
import org.neo4j.logging.Log;

/**
 * Shutdown hook that will send a delete request to the discovery service to de-register the current application.
 */
public class ShutdownHook {

    /**
     * Builder to create a new instance of {@link ShutdownHook}.
     */
    public static final class Builder {

        private LifecycleService lifecycleService;

        /**
         * Set the lifecycle service.
         *
         * @param lifecycleService
         *         the {@link LifecycleService} instance
         *
         * @return the builder object.
         */
        public Builder withLifecycleService(final LifecycleService lifecycleService) {
            this.lifecycleService = lifecycleService;
            return this;
        }

        /**
         * Build a new instance of {@link ShutdownHook}.
         *
         * @return the new instance.
         */
        public ShutdownHook build() {
            validateRequiredFields();
            return new ShutdownHook(lifecycleService);
        }

        /**
         * Check that the required fields are there.
         */
        private void validateRequiredFields() {
            Validate.notNull(lifecycleService);
        }
    }

    private final Log log = FormattedLog.toOutputStream(System.out);

    private final LifecycleService lifecycleService;

    /**
     * Create a new instance of {@link ShutdownHook}
     *
     * @param lifecycleService
     *         the lifecycle service instance
     */
    ShutdownHook(final LifecycleService lifecycleService) {
        this.lifecycleService = lifecycleService;
    }

    /**
     * Execute the de-registration process.
     *
     * @return true if the process was success.
     */
    public boolean execute() {
        try {
            lifecycleService.deregister();
            return true;
        } catch (EurekaPluginException e) {
            // just log the exception because were are already shutting down
            log.warn("An error occurred during the de-registration process!", e);
            return false;
        }
    }

}
