package com.brinkus.lab.neo4j.eureka.type;

public final class HealthCheck {

    private final String code;

    private final String description;

    public HealthCheck(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return this.code;
    }

    public String getDescription() {
        return this.description;
    }

    public String toString() {
        return this.code;
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

        final HealthCheck that = (HealthCheck) o;

        return code != null ? code.equals(that.code) : that.code == null;
    }

}