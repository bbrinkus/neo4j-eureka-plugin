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

import com.brinkus.labs.neo4j.eureka.EurekaPluginConfiguration;
import com.brinkus.labs.neo4j.eureka.ServiceDiscoveryRunner;
import com.brinkus.labs.neo4j.eureka.type.config.Configuration;
import com.brinkus.labs.neo4j.eureka.type.config.Service;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.test.server.HTTP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class LifecycleServiceIT {

    private static final Logger LOGGER = LoggerFactory.getLogger(LifecycleServiceIT.class);

    private static ServiceDiscoveryRunner discoveryRunner;

    private LifecycleService lifecycleService;

    @BeforeClass
    public static void beforeClass() throws Exception {
        discoveryRunner = new ServiceDiscoveryRunner();
        discoveryRunner.startService();

        while (!discoveryRunner.isServiceRunning()) {
            Thread.sleep(2000);
        }
    }

    @AfterClass
    public static void afterClass() {
        discoveryRunner.stopService();
    }

    @Before
    public void before() throws Exception {
        EurekaPluginConfiguration pluginConfiguration = new EurekaPluginConfiguration.Builder().build();

        ConfigurationLoader configurationLoader = new ConfigurationLoader();
        Configuration configuration = configurationLoader.loadConfiguration(pluginConfiguration.getConfigurationFilePath());

        Service service = configuration.getServices().get(0);
        RestClient restClient = new RestClient.Builder()
                .withHost(service.getHost().trim())
                .withPort(service.getPort())
                .build();

        lifecycleService = new LifecycleService.Builder()
                .withRegistration(configuration.getRegistration())
                .withRestClient(restClient)
                .withAwsInfo(pluginConfiguration.getAmazonInfo())
                .build();

    }

    @Test
    public void lifecycle() throws Exception {
        lifecycleService.register();
        waitUntilNotAvailable();
        lifecycleService.keepAlive();
        lifecycleService.deregister();
        waitUntilAvailable();
    }

    private void waitUntilNotAvailable() throws InterruptedException {
        int status = getApplicationStatusCode();
        while (status == 404) {
            Thread.sleep(1000);
            status = getApplicationStatusCode();
        }
        assertThat(status, is(200));
        LOGGER.info("Application instance was found");
    }

    private void waitUntilAvailable() throws InterruptedException {
        int status = getApplicationStatusCode();
        while (status == 200) {
            Thread.sleep(1000);
            status = getApplicationStatusCode();
        }
        assertThat(status, is(404));
        LOGGER.info("No application instance was found");
    }

    private int getApplicationStatusCode() {
        HTTP.Response response = HTTP.GET("http://localhost:18761/eureka/apps/neo4j/neo4j:neo4j:7474");
        return response.status();
    }

}
