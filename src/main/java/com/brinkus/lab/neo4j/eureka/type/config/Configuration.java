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

package com.brinkus.lab.neo4j.eureka.type.config;

import java.util.List;

/**
 * The plugin configuration settings.
 */
public class Configuration {

    /**
     * The list of the discovery services.
     */
    private List<Service> services;

    /**
     * The registration information for the discovery service.
     */
    private Registration registration;

    public List<Service> getServices() {
        return services;
    }

    public void setServices(final List<Service> services) {
        this.services = services;
    }

    public Registration getRegistration() {
        return registration;
    }

    public void setRegistration(final Registration registration) {
        this.registration = registration;
    }

}
