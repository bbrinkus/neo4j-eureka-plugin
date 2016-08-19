package com.brinkus.neo4j.eureka.component;

import com.brinkus.neo4j.eureka.exception.ResponseCodeNotMatchingException;
import com.brinkus.neo4j.eureka.type.aws.AWSInformation;
import com.brinkus.neo4j.eureka.type.aws.AWSMetaDataKey;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AWSInformationLoaderTest {

    private RestClient restClient;

    private AWSInformationLoader awsInformationLoader;

    @Before
    public void before() throws Exception {
        restClient = mock(RestClient.class);
        awsInformationLoader = new AWSInformationLoader(restClient);
    }

    @Test
    public void loadInformation() throws Exception {
        when(restClient.get(eq("/latest/meta-data/instance-id"))).thenReturn("i-0bd9f6f296d9907d1");
        when(restClient.get(eq("/latest/meta-data/local-ipv4"))).thenReturn("192.168.12.10");
        when(restClient.get(eq("/latest/meta-data/local-hostname"))).thenReturn("ip-192-168-12-10.eu-central-1.compute.internal");
        // the computer has not public ip
        when(restClient.get(eq("/latest/meta-data/public-hostname"))).thenThrow(ResponseCodeNotMatchingException.class);
        when(restClient.get(eq("/latest/meta-data/public-ipv4"))).thenThrow(ResponseCodeNotMatchingException.class);

        AWSInformation awsInformation = awsInformationLoader.loadAWSInformation();
        assertThat(awsInformation.getMetadata().size(), is(3));
        assertThat(awsInformation.get(AWSMetaDataKey.INSTANCE_ID), is("i-0bd9f6f296d9907d1"));
        assertThat(awsInformation.get(AWSMetaDataKey.LOCAL_IPV_4), is("192.168.12.10"));
        assertThat(awsInformation.get(AWSMetaDataKey.LOCAL_HOSTNAME), is("ip-192-168-12-10.eu-central-1.compute.internal"));
        assertThat(awsInformation.containsKey(AWSMetaDataKey.PUBLIC_IPV_4), is(false));
        assertThat(awsInformation.containsKey(AWSMetaDataKey.PUBLIC_HOSTNAME), is(false));
    }

    @Test
    public void skipIfNoInstanceIdAvailable() throws Exception {
        when(restClient.get(eq("/latest/meta-data/instance-id"))).thenThrow(ResponseCodeNotMatchingException.class);
        when(restClient.get(eq("/latest/meta-data/local-ipv4"))).thenReturn("192.168.12.10");
        when(restClient.get(eq("/latest/meta-data/local-hostname"))).thenReturn("ip-192-168-12-10.eu-central-1.compute.internal");
        when(restClient.get(eq("/latest/meta-data/public-ipv4"))).thenReturn("192.168.12.10");
        when(restClient.get(eq("/latest/meta-data/public-hostname"))).thenReturn("ip-192-168-12-10.eu-central-1.compute.internal");

        AWSInformation awsInformation = awsInformationLoader.loadAWSInformation();
        assertThat(awsInformation.getMetadata().size(), is(0));
    }

}
