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
import org.neo4j.logging.FormattedLog;
import org.neo4j.logging.Log;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;

/**
 * Eureka instance information creator.
 */
public class InstanceInfoFactory {

    private final Log log = FormattedLog.toOutputStream(System.out);

    /**
     * Creates a new instance of {@link InstanceInfoFactory}
     */
    private InstanceInfoFactory() {
        // default
    }

    /**
     * Create a new instance of the {@link InstanceInfoFactory}.
     *
     * @return a new  {@link InstanceInfoFactory} instance.
     */
    public static InstanceInfoFactory getFactory() {
        return new InstanceInfoFactory();
    }

    /**
     * Create a new {@link InstanceInfo} object.
     *
     * @param registration
     *         the registration information.
     * @param amazonInfo
     *         the AWS information instance.
     *
     * @return the {@link InstanceInfo} object.
     */
    public InstanceInfo create(
            final Registration registration,
            final AmazonInfo amazonInfo
    ) {
        return create(registration, amazonInfo, InstanceInfo.InstanceStatus.STARTING);
    }

    /**
     * Create a new {@link InstanceInfo} object with the given status.
     *
     * @param registration
     *         the registration information.
     * @param amazonInfo
     *         the AWS information instance.
     * @param status
     *         the registered instance's status.
     *
     * @return the {@link InstanceInfo} object.
     */
    public InstanceInfo create(
            final Registration registration,
            final AmazonInfo amazonInfo,
            final InstanceInfo.InstanceStatus status
    ) {
        DataCenterInfo dataCenterInfo;

        String instanceId;
        String hostname;
        String ipAddress;

        if (amazonInfo.getMetadata().isEmpty()) {
            log.warn("Using own data center information");
            dataCenterInfo = new MyDataCenterInfo(DataCenterInfo.Name.MyOwn);

            instanceId = registration.getName();
            hostname = registration.getHostname();
            ipAddress = registration.getIpAddress();
        } else {
            log.warn("Using Amazon data center information");
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

            log.info("Eureka instance ip address: %s", ipAddress);
            log.info("Eureka instance hostname: %s", hostname);
        }

        final long timestamp = System.currentTimeMillis();

        // e.g.: i-0f84ec0b4c02e7878:neo4j:7474
        final String fullInstanceId = String.format("%s:%s:%d", instanceId, registration.getName(), registration.getPort().getPort()).toLowerCase();
        log.info("Eureka instance identifier: %s", fullInstanceId);

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
                .setMetadata(new HashMap<>())
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
                log.info("Overriding Amazon hostname", address.getHostName());
                return address.getHostName();
            }
        } catch (UnknownHostException e) {
            log.warn("An error occurred during the hostname query. Using the aws hostname.", e);
        }
        return null;
    }

}
