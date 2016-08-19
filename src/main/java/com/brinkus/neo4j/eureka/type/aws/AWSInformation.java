package com.brinkus.neo4j.eureka.type.aws;

import java.util.HashMap;
import java.util.Map;

public class AWSInformation {

    private Map<String, String> metadata = new HashMap<>();

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public String get(AWSMetaDataKey key) {
        return metadata.get(key.getName());
    }

    public boolean containsKey(AWSMetaDataKey key) {
        return metadata.containsKey(key.getName());
    }

}
