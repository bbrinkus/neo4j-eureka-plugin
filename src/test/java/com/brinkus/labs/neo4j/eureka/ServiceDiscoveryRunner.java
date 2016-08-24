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

package com.brinkus.labs.neo4j.eureka;

import org.neo4j.test.server.HTTP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;

public class ServiceDiscoveryRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceDiscoveryRunner.class);

    private Process process;

    public void startService() throws IOException {
        LOGGER.info("Starting discovery service.");
        URL url = Thread.currentThread().getContextClassLoader().getResource("service-discovery.jar");
        String jarFilePath = url.getPath();
        process = Runtime.getRuntime().exec(String.format("java -jar %s", jarFilePath));
    }

    public void stopService() {
        LOGGER.info("Stopping discovery service.");
        process.destroy();
    }

    public boolean isServiceRunning() {
        try {
            HTTP.Response response = HTTP.GET("http://localhost:18761/info");
            boolean isRunning = (response.status() == 200);
            if (isRunning) {
                LOGGER.info("Discovery service is up and running.");
            } else {
                LOGGER.info("Discovery service is not available.");
            }
            return isRunning;
        } catch (Exception e) {
            return false;
        }
    }

}
