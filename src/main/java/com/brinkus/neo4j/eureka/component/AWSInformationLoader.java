package com.brinkus.neo4j.eureka.component;

import com.brinkus.neo4j.eureka.type.aws.AWSInformation;
import com.brinkus.neo4j.eureka.type.aws.AWSMetaDataKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AWSInformationLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(AWSInformation.class);

    private static final String AWS_METADATA_PATH = "/latest/meta-data";

    private static final int SLEEP_TIME_MS = 100;

    private final RestClient restClient;

    public AWSInformationLoader() {
        this(new RestClientImpl.Builder()
                     .withHost("169.254.169.254")
                     .withPort(80)
                     .build());
    }

    AWSInformationLoader(final RestClient restClient) {
        this.restClient = restClient;
    }

    public AWSInformation loadAWSInformation() {
        AWSInformation awsInfo = new AWSInformation();

        for (AWSMetaDataKey key : AWSMetaDataKey.values()) {
            int numberOfTries = 3;
            while (numberOfTries-- > 0) {
                try {
                    String value = restClient.get(String.format("%s/%s", AWS_METADATA_PATH, key.getName()));
                    if (value != null) {
                        awsInfo.getMetadata().put(key.getName(), value);
                    }
                    break;
                } catch (Exception e) {
                    LOGGER.warn("Cannot get the value for the {} key", key);
                    if (numberOfTries >= 0) {
                        try {
                            Thread.sleep(SLEEP_TIME_MS);
                        } catch (InterruptedException e1) {
                            // ignore exception
                        }
                        continue;
                    }
                }
            }

            if (key == AWSMetaDataKey.INSTANCE_ID && !awsInfo.containsKey(AWSMetaDataKey.INSTANCE_ID)) {
                LOGGER.warn("Number of tries reached the configured threshold.");
                break;
            }
        }

        return awsInfo;
    }

}
