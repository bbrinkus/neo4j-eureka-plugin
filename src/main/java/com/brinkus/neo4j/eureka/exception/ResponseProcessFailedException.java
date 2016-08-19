package com.brinkus.neo4j.eureka.exception;

public class ResponseProcessFailedException extends RestClientException {

    public ResponseProcessFailedException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
