package com.brinkus.lab.neo4j.eureka.unmanaged;

import com.brinkus.lab.neo4j.eureka.type.health.HealthCheck;
import com.brinkus.lab.neo4j.eureka.type.health.HealthStatusCode;
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
        assertThat(healthCheck.getCode(), is(HealthStatusCode.UP));
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
