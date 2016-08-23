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
