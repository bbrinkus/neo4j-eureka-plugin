package com.brinkus.lab.neo4j.eureka.component;

import com.brinkus.lab.neo4j.eureka.exception.RestClientException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class LifecycleServiceRunnableTest {

    private LifecycleService lifecycleService;

    private LifecycleServiceRunnable lifecycleServiceRunnable;

    private boolean registered;

    private boolean keepAlived;

    @Before
    public void before() throws Exception {
        lifecycleService = mock(LifecycleService.class);
        lifecycleServiceRunnable = new LifecycleServiceRunnable(lifecycleService);
        registered = false;
        keepAlived = false;
    }

    @After
    public void after() throws Exception {
        lifecycleService = null;
        lifecycleServiceRunnable = null;
    }

    @Test
    public void testInterruption() throws Exception {
        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(final InvocationOnMock invocation) throws Throwable {
                registered = true;
                return null;
            }
        }).when(lifecycleService).register();

        long startTime = System.currentTimeMillis();
        new Thread(lifecycleServiceRunnable).start();
        Thread.sleep(2000L);
        lifecycleServiceRunnable.interrupt();
        assertThat(registered, is(true));
        long currentTime = System.currentTimeMillis();
        assertThat(currentTime - startTime, lessThan(3000L));

        verify(lifecycleService, times(1)).register();
    }

    @Test
    public void testRestart() throws Exception {
        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(final InvocationOnMock invocation) throws Throwable {
                registered = true;
                return null;
            }
        }).when(lifecycleService).register();

        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(final InvocationOnMock invocation) throws Throwable {
                if (keepAlived) {
                    throw new RestClientException("Test exception");
                }
                keepAlived = !keepAlived;
                return null;
            }
        }).when(lifecycleService).keepAlive();

        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(final InvocationOnMock invocation) throws Throwable {
                lifecycleServiceRunnable.interrupt();
                return null;
            }
        }).when(lifecycleService).deregister();

        lifecycleServiceRunnable.setKeepAliveTimeoutSec(2);

        long startTime = System.currentTimeMillis();
        new Thread(lifecycleServiceRunnable).start();

        while (!lifecycleServiceRunnable.isInterrupted()) {
            Thread.sleep(100);
        }

        long currentTime = System.currentTimeMillis();
        assertThat(currentTime - startTime, lessThan(10000L));
        verify(lifecycleService, times(1)).register();
        verify(lifecycleService, times(2)).keepAlive();
        verify(lifecycleService, times(1)).deregister();
    }

}
