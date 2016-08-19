package com.brinkus.neo4j.eureka.component;

import com.brinkus.neo4j.eureka.exception.RequestFailedException;
import com.brinkus.neo4j.eureka.exception.ResponseCodeNotMatchingException;
import com.brinkus.neo4j.eureka.exception.ResponseProcessFailedException;
import com.brinkus.neo4j.eureka.exception.RestClientException;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class RestClientImpl implements RestClient {

    public static final class Builder {

        private String host;

        private int port;

        public Builder withHost(final String host) {
            this.host = host;
            return this;
        }

        public Builder withPort(final int port) {
            this.port = port;
            return this;
        }

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

            return new RestClientImpl(host, port, builder.build());
        }

    }

    private static final Logger LOGGER = LoggerFactory.getLogger(RestClientImpl.class);

    private final String host;

    private final int port;

    private final HttpClient httpClient;

    RestClientImpl(final String host, final int port, final HttpClient httpClient) {
        this.host = host;
        this.port = port;
        this.httpClient = httpClient;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    @Override
    public String get(final String uri) throws RestClientException {
        return get(uri, STATUS_OK);
    }

    @Override
    public String get(final String uri, final int status) throws RestClientException {
        HttpGet request = new HttpGet(uri);
        return sendRequests(request, status);
    }

    @Override
    public String post(final String uri, final String content) throws RestClientException {
        return post(uri, content, STATUS_OK);
    }

    @Override
    public String post(final String uri, final String content, final int status) throws RestClientException {
        HttpPost request = new HttpPost(uri);
        request.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");
        request.setEntity(new StringEntity(content, Consts.UTF_8));

        return sendRequests(request, status);
    }

    @Override
    public String put(final String uri) throws RestClientException {
        return put(uri, STATUS_OK);
    }

    @Override
    public String put(final String uri, final int status) throws RestClientException {
        HttpPut request = new HttpPut(uri);
        return sendRequests(request, status);
    }

    @Override
    public String delete(final String uri) throws RestClientException {
        return delete(uri, STATUS_OK);
    }

    @Override
    public String delete(final String uri, final int status) throws RestClientException {
        HttpDelete request = new HttpDelete(uri);
        return sendRequests(request, status);
    }

    private String sendRequests(HttpUriRequest request, int httpStatus) throws RestClientException {
        HttpResponse response;
        try {
            response = httpClient.execute(request);
        } catch (IOException e) {
            String message = "An error occurred during the registration HTTP communication process!";
            LOGGER.error(message, e);
            throw new RequestFailedException(message, e);
        }
        if (response.getStatusLine().getStatusCode() != httpStatus) {
            String message = String.format("The response status code %s is not matchined with the expected %s!", response.getStatusLine().getStatusCode(), host);
            LOGGER.warn(message);
            throw new ResponseCodeNotMatchingException(message);
        }

        try {
            return EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            String message = "An error occurred during the response process!";
            LOGGER.error(message, e);
            throw new ResponseProcessFailedException(message, e);
        }
    }

}
