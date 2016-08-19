package com.brinkus.neo4j.eureka.exception;

public class ConfigurationLoadFailedException extends EurekaPluginException {

    public ConfigurationLoadFailedException(final Throwable cause) {
        super("An error occurred during the configuration reading process.", cause);
    }
}
