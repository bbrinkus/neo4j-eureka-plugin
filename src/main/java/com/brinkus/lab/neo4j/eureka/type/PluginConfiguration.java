package com.brinkus.lab.neo4j.eureka.type;

import com.netflix.appinfo.AmazonInfo;

public class PluginConfiguration {

    private static final String EUREKA_CONFIGURATION_PATH = "conf/neo4j-eureka.yaml";

    public static final class Builder {

        private String configurationFilePath = EUREKA_CONFIGURATION_PATH;

        private AmazonInfo amazonInfo = AmazonInfo.Builder.newBuilder().autoBuild("eureka");

        public Builder withConfigurationFilePath(final String configurationFilePath) {
            this.configurationFilePath = configurationFilePath;
            return this;
        }

        public Builder withAmazonInfo(final AmazonInfo amazonInfo) {
            this.amazonInfo = amazonInfo;
            return this;
        }

        public PluginConfiguration build() {
            return new PluginConfiguration(configurationFilePath, amazonInfo);
        }
    }

    private final String configurationFilePath;

    private final AmazonInfo amazonInfo;

    PluginConfiguration(final String configurationFilePath, final AmazonInfo amazonInfo) {
        this.configurationFilePath = configurationFilePath;
        this.amazonInfo = amazonInfo;
    }

    public String getConfigurationFilePath() {
        return configurationFilePath;
    }

    public AmazonInfo getAmazonInfo() {
        return amazonInfo;
    }
}
