package com.brinkus.lab.neo4j.eureka.exception;

public class EurekaPluginFatalException extends RuntimeException {

    public EurekaPluginFatalException(Throwable e) {
        super("An unrecoverable error occurred. Shutting down plugin!", e);
    }
}
