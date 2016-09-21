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

import com.brinkus.labs.neo4j.eureka.exception.RequestFailedException;
import com.brinkus.labs.neo4j.eureka.exception.ResponseCodeNotMatchingException;
import com.brinkus.labs.neo4j.eureka.exception.ResponseProcessFailedException;
import com.brinkus.labs.neo4j.eureka.exception.RestClientException;
import org.apache.http.Consts;
import org.apache.http.HttpException;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.DefaultRoutePlanner;
import org.apache.http.impl.conn.DefaultSchemePortResolver;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.neo4j.logging.FormattedLog;
import org.neo4j.logging.Log;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * HTTP REST client.
 */
public class RestClient {

    /**
     * Builder to create a new {@link RestClient} instance.
     */
    public static final class Builder {

        private String host;

        private int port;

        /**
         * Set the discovery service's host.
         *
         * @param host
         *         the discovery service's host.
         *
         * @return the builder instance.
         */
        public Builder withHost(final String host) {
            this.host = host;
            return this;
        }

        /**
         * Set the discovery service's port.
         *
         * @param port
         *         the discovery service's port.
         *
         * @return the builder instance
         */
        public Builder withPort(final int port) {
            this.port = port;
            return this;
        }

        /**
         * Create a new instance of the {@link RestClient}.
         *
         * @return the {@link RestClient} instance.
         */
        public RestClient build() {
            List<BasicHeader> defaultHeaders = Arrays.asList(new BasicHeader(HttpHeaders.ACCEPT, "application/json"));

            PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
            connectionManager.setDefaultMaxPerRoute(10);
            connectionManager.setMaxTotal(20);

            HttpRoutePlanner routePlanner = new DefaultRoutePlanner(DefaultSchemePortResolver.INSTANCE) {
                @Override
                public HttpRoute determineRoute(final HttpHost httpHost, final HttpRequest request, final HttpContext context) throws HttpException {
                    HttpHost target = httpHost != null ? httpHost : new HttpHost(host, port, "http");
                    return super.determineRoute(target, request, context);
                }
            };

            HttpClientBuilder builder = HttpClientBuilder
                    .create()
                    .setRoutePlanner(routePlanner)
                    .setDefaultHeaders(defaultHeaders)
                    .setConnectionManager(connectionManager);

            return new RestClient(host, port, builder.build());
        }

    }

    public static final int STATUS_OK = 200;

    public static final int STATUS_NO_CONTENT = 204;

    public static final String NO_CONTENT = "";

    private final Log log = FormattedLog.toOutputStream(System.out);

    private final String host;

    private final int port;

    private final HttpClient httpClient;

    /**
     * Create a new instance of {@link RestClient}.
     *
     * @param host
     *         the discovery service's host.
     * @param port
     *         the discovery service's port number.
     * @param httpClient
     *         the HTTP client instance.
     */
    RestClient(final String host, final int port, final HttpClient httpClient) {
        this.host = host;
        this.port = port;
        this.httpClient = httpClient;
    }

    /**
     * Get the discovery service host.
     *
     * @return the host of the discovery service.
     */
    public String getHost() {
        return host;
    }

    /**
     * Get the discovery service port.
     *
     * @return the port of the discovery service.
     */
    public int getPort() {
        return port;
    }

    /**
     * Execute a HTTP GET request to the given uri.
     *
     * @param uri
     *         the target uri.
     *
     * @return the response message.
     *
     * @throws RestClientException
     *         if an error occur during the request.
     */
    public String get(final String uri) throws RestClientException {
        return get(uri, STATUS_OK);
    }

    /**
     * Execute a HTTP GET request to the given uri with the expected status code.
     *
     * @param uri
     *         the target uri.
     * @param statusCode
     *         the expected response status code.
     *
     * @return the response message.
     *
     * @throws RestClientException
     *         if an error occur during the request.
     */
    public String get(final String uri, final int statusCode) throws RestClientException {
        HttpGet request = new HttpGet(uri);
        return sendRequests(request, statusCode);
    }

    /**
     * Execute a HTTP POST request to the given uri.
     *
     * @param uri
     *         the target uri.
     * @param content
     *         the request's content
     *
     * @return the response message.
     *
     * @throws RestClientException
     *         if an error occur during the request.
     */
    public String post(final String uri, final String content) throws RestClientException {
        return post(uri, content, STATUS_OK);
    }

    /**
     * Execute a HTTP POST request to the given uri with the expected status code.
     *
     * @param uri
     *         the target uri.
     * @param content
     *         the request's content
     * @param statusCode
     *         the expected response status code.
     *
     * @return the response message.
     *
     * @throws RestClientException
     *         if an error occur during the request.
     */
    public String post(final String uri, final String content, final int statusCode) throws RestClientException {
        HttpPost request = new HttpPost(uri);
        request.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");
        request.setEntity(new StringEntity(content, Consts.UTF_8));

        return sendRequests(request, statusCode);
    }

    /**
     * Execute a HTTP PUT request to the given uri.
     *
     * @param uri
     *         the target uri.
     *
     * @return the response message.
     *
     * @throws RestClientException
     *         if an error occur during the request.
     */
    public String put(final String uri) throws RestClientException {
        return put(uri, STATUS_OK);
    }

    /**
     * Execute a HTTP PUT request to the given uri with the expected status code.
     *
     * @param uri
     *         the target uri.
     * @param statusCode
     *         the expected response status code.
     *
     * @return the response message.
     *
     * @throws RestClientException
     *         if an error occur during the request.
     */
    public String put(final String uri, final int statusCode) throws RestClientException {
        HttpPut request = new HttpPut(uri);
        return sendRequests(request, statusCode);
    }

    /**
     * Execute a HTTP DELETE request to the given uri.
     *
     * @param uri
     *         the target uri.
     *
     * @return the response message.
     *
     * @throws RestClientException
     *         if an error occur during the request.
     */
    public String delete(final String uri) throws RestClientException {
        return delete(uri, STATUS_OK);
    }

    /**
     * Execute a HTTP DELETE request to the given uri with the expected status code.
     *
     * @param uri
     *         the target uri.
     * @param statusCode
     *         the expected response status code.
     *
     * @return the response message.
     *
     * @throws RestClientException
     *         if an error occur during the request.
     */
    public String delete(final String uri, final int statusCode) throws RestClientException {
        HttpDelete request = new HttpDelete(uri);
        return sendRequests(request, statusCode);
    }

    private String sendRequests(HttpUriRequest request, int statusCode) throws RestClientException {
        log.debug("Sending %s request to %s", request.getMethod(), request.getURI());
        HttpResponse response;
        try {
            response = httpClient.execute(request);
        } catch (IOException e) {
            String message = "An error occurred during the registration HTTP communication process!";
            log.error(message, e);
            throw new RequestFailedException(message, e);
        }
        if (response.getStatusLine().getStatusCode() != statusCode) {
            String message = String.format("The response status code %s is not matching with the expected %s!", response.getStatusLine().getStatusCode(), host);
            log.warn(message);
            throw new ResponseCodeNotMatchingException(message);
        }

        try {
            if (response.getStatusLine().getStatusCode() == STATUS_OK) {
                return EntityUtils.toString(response.getEntity());
            } else {
                return NO_CONTENT;
            }
        } catch (IOException e) {
            String message = "An error occurred during the response process!";
            log.error(message, e);
            throw new ResponseProcessFailedException(message, e);
        }
    }

}
