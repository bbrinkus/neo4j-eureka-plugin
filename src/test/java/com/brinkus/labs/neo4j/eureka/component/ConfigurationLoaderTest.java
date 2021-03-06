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

package com.brinkus.labs.neo4j.eureka.component;

import com.brinkus.labs.neo4j.eureka.exception.ConfigurationLoadFailedException;
import com.brinkus.labs.neo4j.eureka.type.config.Configuration;
import com.brinkus.labs.neo4j.eureka.type.config.Registration;
import com.brinkus.labs.neo4j.eureka.type.config.Service;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.net.URL;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class ConfigurationLoaderTest {

    private ConfigurationLoader loader;

    @Before
    public void before() throws Exception {
        loader = new ConfigurationLoader();
    }

    @After
    public void after() throws Exception {
        loader = null;
    }

    @Test(expected = FileNotFoundException.class)
    public void loadConfigurationWithInvalidFile() throws Throwable {
        try {
            loader.loadConfiguration("eureka.yaml");
        } catch (ConfigurationLoadFailedException e) {
            throw e.getCause();
        }
    }

    @Test
    public void loadConfiguration() throws Throwable {
        URL url = Thread.currentThread().getContextClassLoader().getResource("neo4j-eureka-services.yaml");
        Configuration configuration = loader.loadConfiguration(url.getPath());

        List<Service> services = configuration.getServices();
        assertThat(services.size(), is(2));
        assertThat(services.get(0).getHost(), is("discovery1.dev.brinkus.com"));
        assertThat(services.get(0).getPort(), is(8761));
        assertThat(services.get(1).getHost(), is("discovery2.dev.brinkus.com"));
        assertThat(services.get(1).getPort(), is(8762));

        Registration registration = configuration.getRegistration();
        assertThat(registration, notNullValue());
        assertThat(registration.getName(), is("neo4j"));
        assertThat(registration.getHostname(), is("neo4j.dev.brinkus.com"));
        assertThat(registration.getIpAddress(), is("127.0.0.1"));
        assertThat(registration.useAwsDnsHostname(), is(true));
        assertThat(registration.getVipAddress(), is("neo4j"));
        assertThat(registration.getPort().getPort(), is(7474));
        assertThat(registration.getPort().isEnabled(), is(true));
        assertThat(registration.getSecurePort().getPort(), is(7473));
        assertThat(registration.getSecurePort().isEnabled(), is(false));
        assertThat(registration.getStatusPageUrl(), is("http://neo4j.dev.brinkus.com:7474/browser"));
        assertThat(registration.getHealthCheckUrl(), is("http://neo4j.dev.brinkus.com:7474/eureka/health"));
        assertThat(registration.getHomePageUrl(), is("http://neo4j.dev.brinkus.com:7474/"));
    }
}
