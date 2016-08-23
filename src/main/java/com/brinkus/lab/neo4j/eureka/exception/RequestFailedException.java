package com.brinkus.lab.neo4j.eureka.exception;

public class RequestFailedException extends RestClientException {

    public RequestFailedException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
