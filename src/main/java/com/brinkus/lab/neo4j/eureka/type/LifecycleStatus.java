package com.brinkus.lab.neo4j.eureka.type;

/**
 * The instance status in the discovery service.
 */
public enum LifecycleStatus {

    /**
     * The instance is registered in the discovery service.
     */
    REGISTERED,

    /**
     * The instance is in the keep-alive state.
     */
    KEEP_ALIVE,

    /**
     * The instance is de-registered from the discovery service.
     */
    DEREGISTERED,

    /**
     * The instance state is unknown.
     */
    UNKNOWN

}
