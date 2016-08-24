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

import com.brinkus.labs.neo4j.eureka.type.config.Registration;
import com.netflix.appinfo.AmazonInfo;
import com.netflix.appinfo.DataCenterInfo;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.appinfo.LeaseInfo;
import com.netflix.appinfo.MyDataCenterInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;

public class InstanceInfoFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(InstanceInfoFactory.class);

    public static InstanceInfoFactory getFactory() {
        return new InstanceInfoFactory();
    }

    /**
     * Creates a new instance of {@link InstanceInfoFactory}
     */
    InstanceInfoFactory() {
        // default
    }

    public InstanceInfo createDefault(
            final Registration registration,
            final AmazonInfo amazonInfo
    ) {
        return createDefault(registration, amazonInfo, InstanceInfo.InstanceStatus.STARTING);
    }

    public InstanceInfo createDefault(
            final Registration registration,
            final AmazonInfo amazonInfo,
            final InstanceInfo.InstanceStatus status
    ) {
        DataCenterInfo dataCenterInfo;

        String instanceId;
        String hostname;
        String ipAddress;

        if (amazonInfo.getMetadata().isEmpty()) {
            LOGGER.warn("Using own data center information");
            dataCenterInfo = new MyDataCenterInfo(DataCenterInfo.Name.MyOwn);

            instanceId = registration.getName();
            hostname = registration.getHostname();
            ipAddress = registration.getIpAddress();
        } else {
            LOGGER.warn("Using Amazon data center information");
            dataCenterInfo = amazonInfo;

            instanceId = amazonInfo.get(AmazonInfo.MetaDataKey.instanceId);
            hostname = amazonInfo.get(AmazonInfo.MetaDataKey.localHostname);
            ipAddress = amazonInfo.get(AmazonInfo.MetaDataKey.localIpv4);

            if (registration.useAwsDnsHostname()) {
                String dnsHostname = getDnsHostname(ipAddress);
                if (dnsHostname != null) {
                    hostname = dnsHostname;
                }
            }

            LOGGER.info("Eureka instance ip address: {}", ipAddress);
            LOGGER.info("Eureka instance hostname: {}", hostname);
        }

        final long timestamp = System.currentTimeMillis();

        // e.g.: i-0f84ec0b4c02e7878:neo4j:7474
        final String fullInstanceId = String.format("%s:%s:%d", instanceId, registration.getName(), registration.getPort().getPort()).toLowerCase();

        final LeaseInfo leaseInfo = LeaseInfo.Builder
                .newBuilder()
                .build();

        return InstanceInfo.Builder
                .newBuilder()
                .setInstanceId(fullInstanceId)
                .setHostName(hostname)
                .setAppName(registration.getName())
                .setIPAddr(ipAddress)
                .setStatus(status)
                .setOverriddenStatus(InstanceInfo.InstanceStatus.UNKNOWN)
                .setPort(registration.getPort().getPort())
                .enablePort(InstanceInfo.PortType.UNSECURE, registration.getPort().isEnabled())
                .setSecurePort(registration.getSecurePort().getPort())
                .enablePort(InstanceInfo.PortType.SECURE, registration.getSecurePort().isEnabled())
                .setDataCenterInfo(dataCenterInfo)
                .setLeaseInfo(leaseInfo)
                .setMetadata(new HashMap<String, String>())
                .setAppGroupName("UNKNOWN")
                .setHomePageUrlForDeser(registration.getHomePageUrl())
                .setStatusPageUrlForDeser(registration.getStatusPageUrl())
                .setHealthCheckUrlsForDeser(registration.getHealthCheckUrl(), null)
                .setVIPAddress(registration.getVipAddress())
                .setIsCoordinatingDiscoveryServer(false)
                .setLastUpdatedTimestamp(timestamp)
                .setLastDirtyTimestamp(timestamp)
                .build();
    }

    private String getDnsHostname(String ipAddress) {
        try {
            InetAddress address = InetAddress.getByName(ipAddress);
            if (address != null && address.getHostName() != null) {
                LOGGER.info("Overriding Amazon hostname", address.getHostName());
                return address.getHostName();
            }
        } catch (UnknownHostException e) {
            LOGGER.warn("An error occurred during the hostname query. Using the aws hostname.", e);
        }
        return null;
    }

}
