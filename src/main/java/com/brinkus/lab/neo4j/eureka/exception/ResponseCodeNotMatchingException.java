package com.brinkus.lab.neo4j.eureka.exception;

public class ResponseCodeNotMatchingException extends RestClientException {

    public ResponseCodeNotMatchingException(final String message) {
        super(message);
    }

}
