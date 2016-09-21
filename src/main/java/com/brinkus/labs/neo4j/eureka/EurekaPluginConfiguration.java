/*
 * Netflix Eureka Client Plugin for Neo4j
 * Copyright (C) 2016  Balazs Brinkus
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.brinkus.labs.neo4j.eureka;

import com.netflix.appinfo.AmazonInfo;

/**
 * Plugin configuration information.
 */
public class EurekaPluginConfiguration {

    private static final String EUREKA_CONFIGURATION_PATH = "conf/neo4j-eureka.yaml";

    /**
     * Builder to create a new {@link EurekaPluginConfiguration} instance.
     */
    public static final class Builder {

        private String configurationFilePath = EUREKA_CONFIGURATION_PATH;

        private AmazonInfo amazonInfo = AmazonInfo.Builder.newBuilder().autoBuild("eureka");

        /**
         * Set the configuration file's path.
         *
         * @param configurationFilePath
         *         the configuration file's path.
         *
         * @return the builder instance.
         */
        public Builder withConfigurationFilePath(final String configurationFilePath) {
            this.configurationFilePath = configurationFilePath;
            return this;
        }

        /**
         * Set the AWS information instance.
         *
         * @param amazonInfo
         *         the AWS information instance.
         *
         * @return the builder instance.
         */
        public Builder withAmazonInfo(final AmazonInfo amazonInfo) {
            this.amazonInfo = amazonInfo;
            return this;
        }

        /**
         * Create a new instance of the {@link EurekaPluginConfiguration}.
         *
         * @return the configuration instance.
         */
        public EurekaPluginConfiguration build() {
            return new EurekaPluginConfiguration(configurationFilePath, amazonInfo);
        }
    }

    private final String configurationFilePath;

    private final AmazonInfo amazonInfo;

    /**
     * Create a new instance of {@link EurekaPluginConfiguration}
     *
     * @param configurationFilePath
     *         the configuration file's path.
     * @param amazonInfo
     *         the AWS information instance.
     */
    EurekaPluginConfiguration(final String configurationFilePath, final AmazonInfo amazonInfo) {
        this.configurationFilePath = configurationFilePath;
        this.amazonInfo = amazonInfo;
    }

    /**
     * Get the configuration file's path.
     *
     * @return the configuration file's path.
     */
    public String getConfigurationFilePath() {
        return configurationFilePath;
    }

    /**
     * Get the AWS information instance.
     *
     * @return the AWS information instance.
     */
    public AmazonInfo getAmazonInfo() {
        return amazonInfo;
    }
}
