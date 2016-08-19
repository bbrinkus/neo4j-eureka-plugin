package com.brinkus.neo4j.eureka.exception;

public class RestClientException extends EurekaPluginException {

    public RestClientException(final String message) {
        super(message);
    }

    public RestClientException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
