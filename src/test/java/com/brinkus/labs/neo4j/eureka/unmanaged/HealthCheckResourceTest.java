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

import com.brinkus.labs.neo4j.eureka.type.health.HealthCheck;
import com.brinkus.labs.neo4j.eureka.type.health.HealthStatusCode;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;

import javax.ws.rs.core.Response;
import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HealthCheckResourceTest {

    private class HealthCheckDeserializer extends StdDeserializer<HealthCheck> {

        public HealthCheckDeserializer(Class<?> vc) {
            super(vc);
        }

        @Override
        public HealthCheck deserialize(
                final JsonParser jsonParser,
                final DeserializationContext deserializationContext
        ) throws IOException {
            JsonNode node = jsonParser.getCodec().readTree(jsonParser);
            String code = node.get("code").asText();
            String description = node.get("description").asText();

            return new HealthCheck(HealthStatusCode.valueOf(code), description);
        }

    }

    private final ObjectMapper mapper;

    private GraphDatabaseService service;

    private Transaction transaction;

    private HealthCheckResource healthCheckResource;

    public HealthCheckResourceTest() {
        mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(HealthCheck.class, new HealthCheckDeserializer(HealthCheck.class));
        mapper.registerModule(module);
    }

    @Before
    public void before() {
        this.transaction = mock(Transaction.class);
        this.service = mock(GraphDatabaseService.class);
        when(this.service.beginTx()).thenReturn(transaction);
        this.healthCheckResource = new HealthCheckResource(service);
    }

    @Test
    public void serviceUp() throws Exception {
        Result result = mock(Result.class);
        when(result.resultAsString()).thenReturn("1 row");
        when(service.isAvailable(1000)).thenReturn(true);

        Response health = healthCheckResource.health();
        assertThat(health.getStatus(), is(200));
        HealthCheck healthCheck = mapper.readValue(((byte[]) health.getEntity()), HealthCheck.class);

        assertThat(healthCheck.getCode(), CoreMatchers.is(HealthStatusCode.UP));
        assertThat(healthCheck.getDescription(), is("Neo4j health check was success."));
    }

    @Test
    public void serviceDown() throws Exception {
        Result result = mock(Result.class);
        when(result.resultAsString()).thenReturn("0 row");
        when(service.isAvailable(1000)).thenReturn(false);

        Response health = healthCheckResource.health();
        assertThat(health.getStatus(), is(200));
        HealthCheck healthCheck = mapper.readValue(((byte[]) health.getEntity()), HealthCheck.class);

        assertThat(healthCheck.getCode(), is(HealthStatusCode.DOWN));
        assertThat(healthCheck.getDescription(), is("Neo4j health check result was invalid!"));
    }

    @Test
    public void serviceException() throws Exception {
        when(service.isAvailable(1000)).thenThrow(Exception.class);

        Response health = healthCheckResource.health();
        assertThat(health.getStatus(), is(200));
        HealthCheck healthCheck = mapper.readValue(((byte[]) health.getEntity()), HealthCheck.class);

        assertThat(healthCheck.getCode(), is(HealthStatusCode.OUT_OF_SERVICE));
        assertThat(healthCheck.getDescription(), is("Neo4j health check failed!"));
    }

}
