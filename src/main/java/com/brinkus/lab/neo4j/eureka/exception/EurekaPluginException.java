package com.brinkus.lab.neo4j.eureka.exception;

public class EurekaPluginException extends Exception {

    public EurekaPluginException(final String message) {
        super(message);
    }

    public EurekaPluginException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
