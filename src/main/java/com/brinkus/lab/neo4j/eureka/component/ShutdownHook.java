package com.brinkus.lab.neo4j.eureka.component;

import com.brinkus.lab.neo4j.eureka.exception.RestClientException;
import com.brinkus.lab.neo4j.eureka.type.config.Registration;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Shutdown hook that will send a delete request to the discovery service to de-register the current application.
 */
public class ShutdownHook {

    /**
     * Builder to create a new instance of {@link ShutdownHook}.
     */
    public static final class Builder {

        private Registration registration;

        private RestClient restClient;

        /**
         * Set the registration object.
         *
         * @param registration
         *         the {@link Registration} instance
         *
         * @return the builder object
         */
        public Builder withRegistration(final Registration registration) {
            this.registration = registration;
            return this;
        }

        /**
         * Set the rest client.
         *
         * @param restClient
         *         the {@link RestClient} instance
         *
         * @return the builder object
         */
        public Builder withRestClient(final RestClient restClient) {
            this.restClient = restClient;
            return this;
        }

        /**
         * Build a new instance of {@link ShutdownHook}.
         *
         * @return the new instance.
         */
        public ShutdownHook build() {
            validateRequiredFields();
            return new ShutdownHook(registration, restClient);
        }

        /**
         * Check that the required fields are there.
         */
        private void validateRequiredFields() {
            Validate.notNull(registration);
            Validate.notNull(restClient);
        }
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(ShutdownHook.class);

    private final Registration registration;

    private final RestClient restClient;

    /**
     * Create a new instance of {@link ShutdownHook}
     *
     * @param registration
     *         the discovery service registration information
     * @param restClient
     *         the rest client
     */
    ShutdownHook(Registration registration, RestClient restClient) {
        this.registration = registration;
        this.restClient = restClient;
    }

    /**
     * Execute the de-registration process.
     *
     * @return true if the process was success.
     */
    public boolean execute() {
        LOGGER.info("Shutting down service");
        String uri = getUri();
        try {
            restClient.delete(uri, RestClient.STATUS_OK);
            return true;
        } catch (RestClientException e) {
            // just log the exception because were are already shutting down
            LOGGER.warn("An error occurred during the request", e);
            return false;
        }
    }

    private String getUri() {
        return String.format("/eureka/apps/%s/%s", registration.getName(), registration.getHostname());
    }

}
