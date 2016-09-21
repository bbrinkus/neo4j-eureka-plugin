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

package com.brinkus.labs.neo4j.eureka.component;

import com.brinkus.labs.neo4j.eureka.exception.EurekaPluginException;
import com.brinkus.labs.neo4j.eureka.exception.InvalidLifeCycleException;
import com.brinkus.labs.neo4j.eureka.type.LifecycleStatus;
import com.brinkus.labs.neo4j.eureka.type.config.Registration;
import com.netflix.appinfo.AmazonInfo;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.converters.JsonXStream;
import org.neo4j.logging.FormattedLog;
import org.neo4j.logging.Log;

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

        /**
         * Set the registration information.
         *
         * @param registration
         *         the registration information.
         *
         * @return the builder instance.
         */
        public Builder withRegistration(final Registration registration) {
            this.registration = registration;
            return this;
        }

        /**
         * Set the REST client.
         *
         * @param restClient
         *         the REST client instance.
         *
         * @return the builder instance.
         */
        public Builder withRestClient(final RestClient restClient) {
            this.restClient = restClient;
            return this;
        }

        /**
         * Set the AWS information instance.
         *
         * @param amazonInfo
         *         the AWS information instance.
         *
         * @return the builder instance.
         */
        public Builder withAwsInfo(final AmazonInfo amazonInfo) {
            this.awsInfo = amazonInfo;
            return this;
        }

        /**
         * Create a new instance of the {@link LifecycleService}.
         *
         * @return the lifecycle service instance.
         */
        public LifecycleService build() {
            return new LifecycleService(registration, restClient, awsInfo);
        }
    }

    private static final String APPLICATION_URI = "/eureka/apps/%s";

    private static final String INSTANCE_URI = "/eureka/apps/%s/%s";

    private final Log log = FormattedLog.toOutputStream(System.out);

    private final Registration registration;

    private final RestClient restClient;

    private final AmazonInfo amazonInfo;

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
     */
    LifecycleService(
            final Registration registration,
            final RestClient restClient,
            final AmazonInfo amazonInfo
    ) {
        this.registration = registration;
        this.restClient = restClient;
        this.amazonInfo = amazonInfo;
        this.status = LifecycleStatus.UNKNOWN;
    }

    /**
     * Register the instance in the discovery service.
     *
     * @throws EurekaPluginException
     *         an error occurred during the registration process
     */
    public void register() throws EurekaPluginException {
        log.info("Sending %s registration request from %s to %s",
                 registration.getName(),
                 registration.getHostname(),
                 restClient.getHost());

        // create a new instance info before every registration
        createInstanceInfo();

        String content;
        content = JsonXStream.getInstance().toXML(instanceInfo);

        String uri = String.format(APPLICATION_URI, registration.getName());
        restClient.post(uri, content, RestClient.STATUS_NO_CONTENT);

        updateStatus(LifecycleStatus.REGISTERED);
    }

    /**
     * Sending keep alive message to the discovery service.
     *
     * @throws EurekaPluginException
     *         an error occurred during the keep alive process
     */
    public void keepAlive() throws EurekaPluginException {
        log.debug("Sending %s status alive request from %s to %s",
                  registration.getName(),
                  registration.getHostname(),
                  restClient.getHost());
        if (instanceInfo == null) {
            throw new InvalidLifeCycleException("Instance info instance does not exist!");
        }

        String uri = String.format(INSTANCE_URI, registration.getName(), instanceInfo.getInstanceId());
        restClient.put(uri);

        updateStatus(LifecycleStatus.KEEP_ALIVE);
    }

    /**
     * Deregister the instance from the discovery service.
     *
     * @throws EurekaPluginException
     *         an error occurred during the de-registration process
     */
    public void deregister() throws EurekaPluginException {
        log.info("Sending %s de-registration request from %s to %s",
                 registration.getName(),
                 registration.getHostname(),
                 restClient.getHost());
        if (instanceInfo == null) {
            throw new InvalidLifeCycleException("Instance info instance does not exist!");
        }

        try {
            // sleep 1 sec before the de-registration
            // otherwise it can happen that it will stay in the DS queue due to the keep alive request
            // TODO investigate further
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            log.error("An error occurred during the de-registration delaying process");
        }
        String uri = String.format(INSTANCE_URI, registration.getName(), instanceInfo.getInstanceId());
        restClient.delete(uri);

        updateStatus(LifecycleStatus.DEREGISTERED);
        instanceInfo = null;
    }

    /**
     * Create a new {@link InstanceInfo} object.
     *
     * @return a new {@link InstanceInfo}
     */
    InstanceInfo createInstanceInfo() {
        instanceInfo = InstanceInfoFactory.getFactory()
                .create(registration, amazonInfo, InstanceInfo.InstanceStatus.UP);
        return instanceInfo;
    }

    private void updateStatus(final LifecycleStatus status) {
        if (this.status == status) {
            return;
        }
        log.info("Update status from %s to %s", this.status, status);
        this.status = status;
    }

}
