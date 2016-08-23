package com.brinkus.neo4j.eureka.component;

import com.brinkus.neo4j.eureka.type.config.Configuration;
import com.netflix.appinfo.AmazonInfo;
import com.netflix.appinfo.DataCenterInfo;
import com.netflix.appinfo.InstanceInfo;
import org.junit.Before;
import org.junit.Test;

import java.net.URL;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class InstanceInfoFactoryTest {

    private Configuration configuration;

    @Before
    public void before() throws Exception {
        ConfigurationLoader loader = new ConfigurationLoader();
        URL url = Thread.currentThread().getContextClassLoader().getResource("neo4j-eureka-it.yaml");
        configuration = loader.loadConfiguration(url.getPath());
    }

    @Test
    public void createDefaultWithoutAmazonInfo() {
        InstanceInfo info = InstanceInfoFactory.getFactory()
                .createDefault(configuration.getRegistration(), new AmazonInfo());

        assertThat(info.getId(), is("neo4j:neo4j:7474"));
        assertThat(info.getHostName(), is("localhost"));
        assertThat(info.getIPAddr(), is("127.0.0.1"));
        assertThat(info.getStatus(), is(InstanceInfo.InstanceStatus.STARTING));
        assertThat(info.getDataCenterInfo().getName(), is(DataCenterInfo.Name.MyOwn));
        verifyCommonProperties(info);
    }

    @Test
    public void createDefaultWithoutAmazonInfoWithStatus() {
        InstanceInfo info = InstanceInfoFactory.getFactory()
                .createDefault(configuration.getRegistration(), new AmazonInfo(), InstanceInfo.InstanceStatus.UP);

        assertThat(info.getId(), is("neo4j:neo4j:7474"));
        assertThat(info.getHostName(), is("localhost"));
        assertThat(info.getIPAddr(), is("127.0.0.1"));
        assertThat(info.getStatus(), is(InstanceInfo.InstanceStatus.UP));
        assertThat(info.getDataCenterInfo().getName(), is(DataCenterInfo.Name.MyOwn));
        verifyCommonProperties(info);
    }

    @Test
    public void createDefaultWithAmazonInfo() {
        InstanceInfo info = InstanceInfoFactory.getFactory()
                .createDefault(configuration.getRegistration(), getAmazonInfo());

        assertThat(info.getId(), is("i-0f84ec0b4c02e7878:neo4j:7474"));
        assertThat(info.getHostName(), is("ip-192-168-123-12.eu-central-1.compute.internal"));
        assertThat(info.getIPAddr(), is("192.168.123.12"));
        assertThat(info.getStatus(), is(InstanceInfo.InstanceStatus.STARTING));
        assertThat(info.getDataCenterInfo().getName(), is(DataCenterInfo.Name.Amazon));
        verifyCommonProperties(info);
    }

    @Test
    public void createDefaultWithAmazonInfooWithStatus() {
        InstanceInfo info = InstanceInfoFactory.getFactory()
                .createDefault(configuration.getRegistration(), getAmazonInfo(), InstanceInfo.InstanceStatus.UP);

        assertThat(info.getId(), is("i-0f84ec0b4c02e7878:neo4j:7474"));
        assertThat(info.getHostName(), is("ip-192-168-123-12.eu-central-1.compute.internal"));
        assertThat(info.getIPAddr(), is("192.168.123.12"));
        assertThat(info.getStatus(), is(InstanceInfo.InstanceStatus.UP));
        assertThat(info.getDataCenterInfo().getName(), is(DataCenterInfo.Name.Amazon));
        verifyCommonProperties(info);
    }

    private void verifyCommonProperties(final InstanceInfo info) {
        assertThat(info.getAppName(), is("NEO4J"));
        assertThat(info.getOverriddenStatus(), is(InstanceInfo.InstanceStatus.UNKNOWN));
        assertThat(info.getPort(), is(7474));
        assertThat(info.isPortEnabled(InstanceInfo.PortType.UNSECURE), is(true));
        assertThat(info.getSecurePort(), is(7473));
        assertThat(info.isPortEnabled(InstanceInfo.PortType.SECURE), is(false));
        assertThat(info.getLeaseInfo().getRenewalIntervalInSecs(), is(30));
        assertThat(info.getLeaseInfo().getDurationInSecs(), is(90));
        assertThat(info.getLeaseInfo().getEvictionTimestamp(), is(0L));
        assertThat(info.getLeaseInfo().getRegistrationTimestamp(), is(0L));
        assertThat(info.getLeaseInfo().getRenewalTimestamp(), is(0L));
        assertThat(info.getLeaseInfo().getServiceUpTimestamp(), is(0L));
        assertThat(info.getMetadata().size(), is(0));
        assertThat(info.getAppGroupName(), is("UNKNOWN"));
        assertThat(info.getHomePageUrl(), is("http://localhost:7474/"));
        assertThat(info.getStatusPageUrl(), is("http://localhost:7474/browser"));
        assertThat(info.getHealthCheckUrl(), is("http://localhost:7474/eureka/health"));
        assertThat(info.getVIPAddress(), is("neo4j"));
        assertThat(info.isCoordinatingDiscoveryServer(), is(false));
        assertThat(info.getLastUpdatedTimestamp(), notNullValue());
        assertThat(info.getLastDirtyTimestamp(), notNullValue());
        assertThat(info.getLastDirtyTimestamp(), is(info.getLastUpdatedTimestamp()));
    }

    private AmazonInfo getAmazonInfo() {
        AmazonInfo amazonInfo = new AmazonInfo();

        amazonInfo.getMetadata().put(AmazonInfo.MetaDataKey.accountId.getName(), "158623721582");
        amazonInfo.getMetadata().put(AmazonInfo.MetaDataKey.localHostname.getName(), "ip-192-168-123-12.eu-central-1.compute.internal");
        amazonInfo.getMetadata().put(AmazonInfo.MetaDataKey.instanceId.getName(), "i-0f84ec0b4c02e7878");
        amazonInfo.getMetadata().put(AmazonInfo.MetaDataKey.localIpv4.getName(), "192.168.123.12");
        amazonInfo.getMetadata().put(AmazonInfo.MetaDataKey.instanceType.getName(), "vpc-ffade3c3");
        amazonInfo.getMetadata().put(AmazonInfo.MetaDataKey.vpcId.getName(), "vpc-ffade3c3");
        amazonInfo.getMetadata().put(AmazonInfo.MetaDataKey.amiId.getName(), "ami-f2df387d");
        amazonInfo.getMetadata().put(AmazonInfo.MetaDataKey.mac.getName(), "06:6a:1e:1a:c0:27");
        amazonInfo.getMetadata().put(AmazonInfo.MetaDataKey.availabilityZone.getName(), "eu-central-1b");

        return amazonInfo;
    }

}
