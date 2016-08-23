package com.brinkus.lab.neo4j.eureka.type.health;

/**
 * Health check information.
 */
public final class HealthCheck {

    /**
     * The status code of the server.
     */
    private final HealthStatusCode code;

    /**
     * The detailed description of the health status.
     */
    private final String description;

    /**
     * Create a new instance of {@link HealthCheck}.
     *
     * @param code
     *         The status code of the server
     * @param description
     *         the detailed description of the status
     */
    public HealthCheck(HealthStatusCode code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * Get the status code.
     *
     * @return the health status code
     */
    public HealthStatusCode getCode() {
        return this.code;
    }

    /**
     * Get the health detailed description.
     *
     * @return the description
     */
    public String getDescription() {
        return this.description;
    }

    @Override
    public String toString() {
        return this.code.toString();
    }

    @Override
    public int hashCode() {
        return code != null ? code.hashCode() : 0;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        return code == ((HealthCheck) o).code;
    }

}