package com.brinkus.lab.neo4j.eureka.component;

import com.brinkus.lab.neo4j.eureka.exception.ResponseCodeNotMatchingException;
import com.brinkus.lab.neo4j.eureka.type.config.Registration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ShutdownHookTest {

    private RestClient restClient;

    private ShutdownHook shutdownHook;

    @Before
    public void before() {
        Registration registration = new Registration();
        registration.setName("test");
        registration.setName("test.host.com");

        restClient = mock(RestClient.class);
        shutdownHook = new ShutdownHook.Builder()
                .withRestClient(restClient)
                .withRegistration(registration)
                .build();
    }

    @After
    public void after() {
        restClient = null;
        shutdownHook = null;
    }

    @Test(expected = NullPointerException.class)
    public void buildWithoutRegistration() {
        new ShutdownHook.Builder()
                .withRestClient(restClient)
                .build();
    }

    @Test(expected = NullPointerException.class)
    public void buildWithoutRestClient() {
        new ShutdownHook.Builder()
                .withRegistration(new Registration())
                .build();
    }

    @Test
    public void shutdownSuccess() throws Exception {
        when(restClient.delete(eq("/eureka/apps/test/test.host.com"))).thenReturn("");

        boolean result = shutdownHook.execute();
        assertThat(result, is(true));
    }

    @Test
    public void shutdownFailed() throws Exception {
        when(restClient.delete(eq("/eureka/apps/test/test.host.com"))).thenThrow(ResponseCodeNotMatchingException.class);

        boolean result = shutdownHook.execute();
        assertThat(result, is(true));
    }

}
