package com.brinkus.lab.neo4j.eureka.type.health;

/**
 * The health status code.
 */
public enum HealthStatusCode {

    /**
     * The server is up and running.
     */
    UP("UP"),

    /**
     * The server is down.
     */
    DOWN("DOWN");

    /**
     * The value of the enum.
     */
    private String value;

    /**
     * Create a new instance of {@link HealthStatusCode}
     *
     * @param value
     *         the text value of the enum
     */
    HealthStatusCode(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

}
