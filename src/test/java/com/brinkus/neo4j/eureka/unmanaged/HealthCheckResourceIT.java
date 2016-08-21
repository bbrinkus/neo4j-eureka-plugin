package com.brinkus.neo4j.eureka.unmanaged;

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
