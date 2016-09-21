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

package com.brinkus.labs.neo4j.eureka;

import com.brinkus.labs.neo4j.eureka.component.ConfigurationLoader;
import com.brinkus.labs.neo4j.eureka.component.LifecycleService;
import com.brinkus.labs.neo4j.eureka.component.LifecycleServiceRunnable;
import com.brinkus.labs.neo4j.eureka.component.RestClient;
import com.brinkus.labs.neo4j.eureka.component.ShutdownHook;
import com.brinkus.labs.neo4j.eureka.exception.EurekaPluginException;
import com.brinkus.labs.neo4j.eureka.exception.EurekaPluginFatalException;
import com.brinkus.labs.neo4j.eureka.type.config.Configuration;
import com.brinkus.labs.neo4j.eureka.type.config.Service;
import org.neo4j.logging.FormattedLog;
import org.neo4j.logging.Log;
import org.neo4j.server.plugins.Description;
import org.neo4j.server.plugins.ServerPlugin;

import java.util.ArrayList;
import java.util.List;

/**
 * Netflix Eureka service discovery plugin.
 */
@Description("Netflix Eureka Service Discovery plugin")
public class EurekaPlugin extends ServerPlugin {

    private final Log log = FormattedLog.toOutputStream(System.out);

    /**
     * Create a new instance of {@link EurekaPlugin}.
     */
    public EurekaPlugin() {
        super();
        log.info("Create a new instance of EurekaPlugin.");
        run(new EurekaPluginConfiguration.Builder().build());
    }

    /**
     * Create a new instance of {@link EurekaPlugin}.
     *
     * @param name
     *         the name of this extension.
     */
    public EurekaPlugin(final String name) {
        super(name);
        log.info("Create a new instance of EurekaPlugin with name %s", name);
        run(new EurekaPluginConfiguration.Builder().build());
    }

    /**
     * Create a new instance of {@link EurekaPlugin}.
     *
     * @param pluginConfiguration
     *         the plugin's configuration information.
     */
    EurekaPlugin(final EurekaPluginConfiguration pluginConfiguration) {
        super();
        log.info("Create a new instance of EurekaPlugin with custom plugin configuration");
        run(pluginConfiguration);
    }

    private void run(final EurekaPluginConfiguration pluginConfiguration) {
        log.info("Start EurekaPlugin.");

        final Configuration configuration = loadConfiguration(pluginConfiguration.getConfigurationFilePath());
        final List<RestClient> serviceClients = initializeServiceClients(configuration);

        for (RestClient client : serviceClients) {
            final LifecycleService lifecycleService = new LifecycleService.Builder()
                    .withRegistration(configuration.getRegistration())
                    .withRestClient(client)
                    .withAwsInfo(pluginConfiguration.getAmazonInfo())
                    .build();

            log.info(String.format("Registering new shutdown hook for %s from %s to %s",
                                   configuration.getRegistration().getName(),
                                   configuration.getRegistration().getHostname(),
                                   client.getHost()));
            new ShutdownHook.Builder()
                    .withLifecycleService(lifecycleService)
                    .build()
                    .register();

            log.info(String.format("Starting lifecycle service for %s from %s to %s",
                                   configuration.getRegistration().getName(),
                                   configuration.getRegistration().getHostname(),
                                   client.getHost()));
            new Thread(new LifecycleServiceRunnable(lifecycleService)).start();
        }
    }

    private Configuration loadConfiguration(final String configurationFilePath) {
        try {
            log.info("Reading configuration settings.");
            ConfigurationLoader configurationLoader = new ConfigurationLoader();
            return configurationLoader.loadConfiguration(configurationFilePath);
        } catch (EurekaPluginException e) {
            log.error("An error occurred the properties reading process!");
            throw new EurekaPluginFatalException(e);
        }
    }

    private List<RestClient> initializeServiceClients(final Configuration configuration) {
        List<RestClient> clients = new ArrayList<>();
        for (Service service : configuration.getServices()) {
            RestClient restClient = new RestClient.Builder()
                    .withHost(service.getHost().trim())
                    .withPort(service.getPort())
                    .build();
            clients.add(restClient);
        }
        return clients;
    }

}
