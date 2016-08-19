package com.brinkus.neo4j.eureka.type.aws;

import java.net.MalformedURLException;
import java.net.URL;

public enum AWSMetaDataKey {

    INSTANCE_ID("instance-id"),

    LOCAL_IPV_4("local-ipv4"),

    LOCAL_HOSTNAME("local-hostname"),

    PUBLIC_HOSTNAME("public-hostname"),

    PUBLIC_IPV_4("public-ipv4");

    private static final String AWS_METADATA_URL = "http://169.254.169.254/latest/meta-data/";

    protected String name;

    AWSMetaDataKey(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public URL getURL() throws MalformedURLException {
        return new URL(AWS_METADATA_URL + name);
    }

    @Override
    public String toString() {
        return getName();
    }
}