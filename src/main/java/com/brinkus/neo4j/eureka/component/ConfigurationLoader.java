package com.brinkus.neo4j.eureka.component;

import com.brinkus.neo4j.eureka.exception.ConfigurationLoadFailedException;
import com.brinkus.neo4j.eureka.exception.EurekaPluginException;
import com.brinkus.neo4j.eureka.type.config.Configuration;
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
