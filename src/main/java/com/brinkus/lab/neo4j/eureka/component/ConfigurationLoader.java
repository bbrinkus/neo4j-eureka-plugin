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

package com.brinkus.lab.neo4j.eureka.component;

import com.brinkus.lab.neo4j.eureka.exception.ConfigurationLoadFailedException;
import com.brinkus.lab.neo4j.eureka.exception.EurekaPluginException;
import com.brinkus.lab.neo4j.eureka.type.config.Configuration;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;

import java.io.File;
import java.io.IOException;

/**
 * Configuration file reader.
 */
public class ConfigurationLoader {

    private final ObjectMapper mapper;

    /**
     * Create a new instance of {@link ConfigurationLoader}.
     */
    public ConfigurationLoader() {
        YAMLFactory yamlFactory = new YAMLFactory();
        yamlFactory.configure(YAMLGenerator.Feature.MINIMIZE_QUOTES, true);
        mapper = new ObjectMapper(yamlFactory);
    }

    /**
     * Read the configuration file.
     *
     * @param path
     *         the configuration file's path
     *
     * @return the configuration instance
     *
     * @throws ConfigurationLoadFailedException
     *         an error occur during the reading process
     */
    public Configuration loadConfiguration(final String path) throws EurekaPluginException {
        try {
            File source = new File(path);
            return mapper.readValue(source, Configuration.class);
        } catch (IOException e) {
            throw new ConfigurationLoadFailedException(e);
        }
    }

}
