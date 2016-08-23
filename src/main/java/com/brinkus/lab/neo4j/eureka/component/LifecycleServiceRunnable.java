package com.brinkus.lab.neo4j.eureka.component;

import com.brinkus.lab.neo4j.eureka.exception.RestClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Runnable to handle the lifecycle service continuity.
 */
public class LifecycleServiceRunnable implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(LifecycleServiceRunnable.class);

    private static final int KEEP_ALIVE_TIMEOUT_SEC = 30;

    private final LifecycleService lifecycleService;

    private boolean interrupted;

    private int keepAliveTimeoutSec;

    /**
     * Create a new instance of {@link LifecycleServiceRunnable}
     *
     * @param lifecycleService
     *         the lifecycle service instance
     */
    public LifecycleServiceRunnable(final LifecycleService lifecycleService) {
        this.lifecycleService = lifecycleService;
        this.interrupted = false;
        this.keepAliveTimeoutSec = KEEP_ALIVE_TIMEOUT_SEC;
    }

    /**
     * Interrupt the current process.
     */
    public void interrupt() {
        LOGGER.info("Process was interrupted.");
        interrupted = true;
        // interrupt the thread sleeping state
        Thread.interrupted();
    }

    /**
     * Check the the process is interrupted.
     *
     * @return true if it was interrupted
     */
    public boolean isInterrupted() {
        return interrupted;
    }

    /**
     * Set the keep alive timeout in seconds
     *
     * @param keepAliveTimeoutSec
     *         the timeout in seconds
     */
    void setKeepAliveTimeoutSec(final int keepAliveTimeoutSec) {
        this.keepAliveTimeoutSec = keepAliveTimeoutSec;
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            try {
                // register the application instance
                lifecycleService.register();

                // wait 30 sec before the keep alive signal
                sleep(keepAliveTimeoutSec);
                while (!isInterrupted()) {
                    lifecycleService.keepAlive();
                    // wait 30 sec between the keep alive signals
                    sleep(keepAliveTimeoutSec);
                }
            } catch (RestClientException e) {
                LOGGER.error("An error occurred during the HTTP communication process. Re-start the process.", e);
                sleep(5);
            } finally {
                try {
                    // deregister the application instance
                    lifecycleService.deregister();
                } catch (RestClientException e) {
                    // just log the error. The new registration will override the process
                    LOGGER.error("An error occurred during the HTTP communication process", e);
                }
            }
        }
    }

    /**
     * Sleep the given seconds.
     *
     * @param seconds
     *         the sleeping time in seconds
     */
    private void sleep(int seconds) {
        try {
            Thread.sleep(seconds * 1000L);
        } catch (InterruptedException e) {
            LOGGER.error("The sleep process was interrupted.", e);
        }
    }

}
