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

package com.brinkus.lab.neo4j.eureka.component;

import com.brinkus.lab.neo4j.eureka.exception.EurekaPluginFatalException;
import com.brinkus.lab.neo4j.eureka.exception.RestClientException;
import com.brinkus.lab.neo4j.eureka.type.LifecycleStatus;
import com.brinkus.lab.neo4j.eureka.type.config.Registration;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.netflix.appinfo.AmazonInfo;
import com.netflix.appinfo.InstanceInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handle the instance lifecycle in the discovery service.
 */
public class LifecycleService {

    /**
     * Builder to create a new {@link LifecycleService} instance.
     */
    public static class Builder {

        private Registration registration;

        private RestClient restClient;

        private AmazonInfo awsInfo;

        private ObjectMapper mapper;

        public Builder() {
            mapper = new ObjectMapper();
            mapper.enable(DeserializationFeature.UNWRAP_ROOT_VALUE);
            mapper.enable(SerializationFeature.WRAP_ROOT_VALUE);
        }

        public Builder withRegistration(final Registration registration) {
            this.registration = registration;
            return this;
        }

        public Builder withRestClient(final RestClient restClient) {
            this.restClient = restClient;
            return this;
        }

        public Builder withAwsInfo(final AmazonInfo amazonInfo) {
            this.awsInfo = amazonInfo;
            return this;
        }

        public Builder withMapper(final ObjectMapper mapper) {
            this.mapper = mapper;
            return this;
        }

        public LifecycleService build() {
            return new LifecycleService(registration, restClient, awsInfo, mapper);
        }
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(LifecycleService.class);

    private static final String APPLICATION_URI = "/eureka/apps/%s";

    private static final String INSTANCE_URI = "/eureka/apps/%s/%s";

    private final Registration registration;

    private final RestClient restClient;

    private final AmazonInfo amazonInfo;

    private final ObjectMapper mapper;

    private InstanceInfo instanceInfo;

    private LifecycleStatus status;

    /**
     * Create a new instance of {@link LifecycleService}.
     *
     * @param registration
     *         the discovery service registration information
     * @param restClient
     *         the rest client to handle HTTP communication
     * @param amazonInfo
     *         the Amazon info
     * @param mapper
     *         the json parser
     */
    LifecycleService(
            final Registration registration,
            final RestClient restClient,
            final AmazonInfo amazonInfo,
            final ObjectMapper mapper
    ) {
        this.registration = registration;
        this.restClient = restClient;
        this.amazonInfo = amazonInfo;
        this.mapper = mapper;
        this.status = LifecycleStatus.UNKNOWN;
    }

    /**
     * Register the instance in the discovery service.
     *
     * @throws RestClientException
     *         an error occurred during the HTTP communication
     */
    public void register() throws RestClientException {
        LOGGER.info("Registering application instance ({})", restClient.getHost());

        // create a new instance info before every registration
        instanceInfo = InstanceInfoFactory.getFactory()
                .createDefault(registration, amazonInfo, InstanceInfo.InstanceStatus.UP);

        String content;
        try {
            content = mapper.writeValueAsString(instanceInfo);
        } catch (JsonProcessingException e) {
            String message = "An error occurred during the InstanceInfo serialization. Shutting down the plug-in.";
            LOGGER.error(message, e);
            throw new EurekaPluginFatalException(e);
        }

        String uri = String.format(APPLICATION_URI, registration.getName());
        restClient.post(uri, content, RestClient.STATUS_NO_CONTENT);

        updateStatus(LifecycleStatus.REGISTERED);
    }

    /**
     * Sending keep alive message to the discovery service.
     *
     * @throws RestClientException
     *         an error occurred during the HTTP communication
     */
    public void keepAlive() throws RestClientException {
        LOGGER.info("Keeping application status alive ({})", restClient.getHost());

        String uri = String.format(INSTANCE_URI, registration.getName(), instanceInfo.getInstanceId());
        restClient.put(uri);

        updateStatus(LifecycleStatus.KEEP_ALIVE);
    }

    /**
     * Deregister the instance from the discovery service.
     *
     * @throws RestClientException
     *         an error occurred during the HTTP communication
     */
    public void deregister() throws RestClientException {
        LOGGER.info("De-registering application instance ({})", restClient.getHost());

        String uri = String.format(INSTANCE_URI, registration.getName(), instanceInfo.getInstanceId());
        restClient.delete(uri);

        updateStatus(LifecycleStatus.DEREGISTERED);
    }

    private void updateStatus(LifecycleStatus status) {
        if (this.status != status) {
            this.status = status;
        }
    }

}
