package com.brinkus.lab.neo4j.eureka;

import com.brinkus.lab.neo4j.eureka.component.ConfigurationLoader;
import com.brinkus.lab.neo4j.eureka.component.LifecycleService;
import com.brinkus.lab.neo4j.eureka.component.LifecycleServiceRunnable;
import com.brinkus.lab.neo4j.eureka.component.RestClient;
import com.brinkus.lab.neo4j.eureka.component.ShutdownHook;
import com.brinkus.lab.neo4j.eureka.exception.EurekaPluginException;
import com.brinkus.lab.neo4j.eureka.exception.EurekaPluginFatalException;
import com.brinkus.lab.neo4j.eureka.type.PluginConfiguration;
import com.brinkus.lab.neo4j.eureka.type.config.Configuration;
import com.brinkus.lab.neo4j.eureka.type.config.Service;
import com.netflix.appinfo.AmazonInfo;
import org.neo4j.server.plugins.Description;
import org.neo4j.server.plugins.ServerPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Netflix Eureka service discovery plugin.
 */
@Description("Netflix Eureka Service Discovery plugin")
public class EurekaPlugin extends ServerPlugin {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerPlugin.class);

    public EurekaPlugin() {
        super();
        LOGGER.info("Create a new instance of EurekaPlugin.");
        run(new PluginConfiguration.Builder().build());
    }

    public EurekaPlugin(final String name) {
        super(name);
        LOGGER.info("Create a new instance of EurekaPlugin with name {}", name);
        run(new PluginConfiguration.Builder().build());
    }

    EurekaPlugin(final PluginConfiguration pluginConfiguration) {
        super();
        LOGGER.info("Create a new instance of EurekaPlugin with custom plugin configuration");
        run(pluginConfiguration);
    }

    private void run(final PluginConfiguration pluginConfiguration) {
        LOGGER.info("Start EurekaPlugin.");
        final Configuration configuration = loadConfiguration(pluginConfiguration.getConfigurationFilePath());

        List<RestClient> serviceClients = initializeServiceClients(configuration);
        initializeShutdownHook(configuration, serviceClients);
        startStatusHandlers(serviceClients, configuration, pluginConfiguration.getAmazonInfo());
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

    public Configuration loadConfiguration(final String configurationFilePath) {
        try {
            LOGGER.info("Reading configuration settings.");
            ConfigurationLoader configurationLoader = new ConfigurationLoader();
            return configurationLoader.loadConfiguration(configurationFilePath);
        } catch (EurekaPluginException e) {
            LOGGER.error("An error occurred the properties reading process!");
            throw new EurekaPluginFatalException(e);
        }
    }

    private void initializeShutdownHook(
            final Configuration configuration,
            final List<RestClient> serviceClients
    ) {
        for (RestClient client : serviceClients) {
            LOGGER.info(String.format("Registering new shutdown hook for %s", client.getHost()));
            final ShutdownHook hook = new ShutdownHook.Builder()
                    .withRegistration(configuration.getRegistration())
                    .withRestClient(client)
                    .build();

            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    hook.execute();
                }
            });
        }
    }

    private void startStatusHandlers(
            final List<RestClient> serviceClients,
            final Configuration configuration,
            final AmazonInfo amazonInfo
    ) {
        for (RestClient client : serviceClients) {
            LOGGER.info(String.format("Starting lifecycle service for %s", client.getHost()));
            final LifecycleService lifecycleService = new LifecycleService.Builder()
                    .withRegistration(configuration.getRegistration())
                    .withRestClient(client)
                    .withAwsInfo(amazonInfo)
                    .build();
            new Thread(new LifecycleServiceRunnable(lifecycleService)).start();
        }
    }

}
