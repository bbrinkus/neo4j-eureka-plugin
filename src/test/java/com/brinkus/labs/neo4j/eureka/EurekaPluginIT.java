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

import com.brinkus.labs.neo4j.eureka.unmanaged.HealthCheckResource;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.harness.ServerControls;
import org.neo4j.harness.TestServerBuilders;
import org.neo4j.server.rest.domain.JsonParseException;
import org.neo4j.test.server.HTTP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class EurekaPluginIT {

    private static final Logger LOGGER = LoggerFactory.getLogger(EurekaPluginIT.class);

    private static ServiceDiscoveryRunner discoveryRunner;

    @BeforeClass
    public static void beforeClass() throws Exception {
        discoveryRunner = new ServiceDiscoveryRunner();
        discoveryRunner.startService();

        while (!discoveryRunner.isServiceRunning()) {
            Thread.sleep(2000);
        }
    }

    @AfterClass
    public static void afterClass() {
        discoveryRunner.stopService();
    }

    @Before
    public void before() throws Exception {
        checkApplicationIsNotAvailable();
    }

    @Test
    public void registrationSuccess() throws Exception {
        try (ServerControls server = TestServerBuilders.newInProcessBuilder()
                .withExtension("/eureka", HealthCheckResource.class)
                .withFixture("CREATE (TheMatrix:Movie {title:'The Matrix', released:1999, tagline:'Welcome to the Real World'})")
                .newServer()) {

            checkHealthCheck(server.httpURI().resolve("eureka/health").toString());

            HTTP.Response response = getApplicationInstance();
            int status = response.status();
            while (status != 200) {
                Thread.sleep(1000);
                response = getApplicationInstance();
                status = response.status();
            }
            assertThat(status, is(200));
            assertThat(response.rawContent(), containsString("\"instanceId\":\"neo4j:neo4j:7474\""));
            assertThat(response.rawContent(), containsString("\"status\":\"UP\""));
            LOGGER.info("Application instance was found");
        }
    }

    private void checkHealthCheck(final String uri) throws JsonParseException {
        HTTP.Response response = HTTP.GET(uri);
        assertThat(response.status(), is(200));
        assertThat(response.stringFromContent("code"), is("UP"));
        LOGGER.info("Health check response is valid");
    }

    private void checkApplicationIsNotAvailable() throws InterruptedException {
        // check that the discovery service has no registered application
        HTTP.Response response = HTTP.GET("http://localhost:18761/eureka/apps/neo4j");
        int status = response.status();
        assertThat(status, is(404));
        LOGGER.info("No application instance was found");
    }

    private HTTP.Response getApplicationInstance() {
        return HTTP.GET("http://localhost:18761/eureka/apps/neo4j");
    }
}
