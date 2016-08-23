package com.brinkus.neo4j.eureka;

import org.neo4j.test.server.HTTP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;

public class ServiceDiscoveryRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceDiscoveryRunner.class);

    private Process process;

    public void startService() throws IOException {
        LOGGER.info("Starting discovery service.");
        URL url = Thread.currentThread().getContextClassLoader().getResource("service-discovery.jar");
        String jarFilePath = url.getPath();
        process = Runtime.getRuntime().exec(String.format("java -jar %s", jarFilePath));
    }

    public void stopService() {
        LOGGER.info("Stopping discovery service.");
        process.destroy();
    }

    public boolean isServiceRunning() {
        try {
            HTTP.Response response = HTTP.GET("http://localhost:18761/info");
            boolean isRunning = (response.status() == 200);
            if (isRunning) {
                LOGGER.info("Discovery service is up and running.");
            } else {
                LOGGER.info("Discovery service is not available.");
            }
            return isRunning;
        } catch (Exception e) {
            return false;
        }
    }

}
