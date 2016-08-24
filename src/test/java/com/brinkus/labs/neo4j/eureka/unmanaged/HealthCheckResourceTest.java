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
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HealthCheckResourceTest {

    private GraphDatabaseService service;

    private Transaction transaction;

    private HealthCheckResource healthCheckResource;

    @Before
    public void before() {
        transaction = mock(Transaction.class);
        service = mock(GraphDatabaseService.class);
        when(service.beginTx()).thenReturn(transaction);
        healthCheckResource = new HealthCheckResource(service);
    }

    @Test
    public void serviceUp() {
        Result result = mock(Result.class);
        when(result.resultAsString()).thenReturn("1 row");
        when(service.execute(anyString())).thenReturn(result);

        HealthCheck healthCheck = healthCheckResource.healthCheck();
        assertThat(healthCheck.getCode(), CoreMatchers.is(HealthStatusCode.UP));
        assertThat(healthCheck.getDescription(), is("Neo4j health check was success."));
    }

    @Test
    public void serviceDown() {
        Result result = mock(Result.class);
        when(result.resultAsString()).thenReturn("0 row");
        when(service.execute(anyString())).thenReturn(result);

        HealthCheck healthCheck = healthCheckResource.healthCheck();
        assertThat(healthCheck.getCode(), is(HealthStatusCode.DOWN));
        assertThat(healthCheck.getDescription(), is("Neo4j health check result was invalid!"));
    }

    @Test
    public void serviceException() {
        when(service.execute(anyString())).thenThrow(Exception.class);

        HealthCheck healthCheck = healthCheckResource.healthCheck();
        assertThat(healthCheck.getCode(), is(HealthStatusCode.DOWN));
        assertThat(healthCheck.getDescription(), is("Neo4j health check failed!"));
    }


}
