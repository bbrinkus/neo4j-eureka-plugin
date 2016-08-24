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

package com.brinkus.labs.neo4j.eureka.unmanaged;

import org.junit.Rule;
import org.junit.Test;
import org.neo4j.harness.ServerControls;
import org.neo4j.harness.TestServerBuilders;
import org.neo4j.test.SuppressOutput;
import org.neo4j.test.server.HTTP;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class HealthCheckResourceIT {

    @Rule
    public SuppressOutput suppressOutput = SuppressOutput.suppressAll();

    @Test
    public void healthUp() throws Exception {
        try (ServerControls server = TestServerBuilders.newInProcessBuilder()
                .withExtension("/eureka", HealthCheckResource.class)
                .withFixture("CREATE (TheMatrix:Movie {title:'The Matrix', released:1999, tagline:'Welcome to the Real World'})")
                .newServer()) {

            HTTP.Response response = HTTP.GET(server.httpURI().resolve("eureka/health").toString());

            assertThat(response.status(), is(200));
            assertThat(response.stringFromContent("code"), is("UP"));
            assertThat(response.stringFromContent("description"), is("Neo4j health check was success."));
        }
    }

    @Test
    public void healthDown() throws Exception {
        try (ServerControls server = TestServerBuilders.newInProcessBuilder()
                .withExtension("/eureka", HealthCheckResource.class)
                .newServer()) {

            HTTP.Response response = HTTP.GET(server.httpURI().resolve("eureka/health").toString());

            assertThat(response.status(), is(200));
            assertThat(response.stringFromContent("code"), is("DOWN"));
            assertThat(response.stringFromContent("description"), is("Neo4j health check result was invalid!"));
        }
    }

}
