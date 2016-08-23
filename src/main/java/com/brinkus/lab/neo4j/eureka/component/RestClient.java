package com.brinkus.lab.neo4j.eureka.component;

import com.brinkus.lab.neo4j.eureka.exception.RestClientException;

public interface RestClient {

    int STATUS_OK = 200;

    int STATUS_NO_CONTENT = 204;

    String getHost();

    String get(final String uri) throws RestClientException;

    String get(final String uri, final int status) throws RestClientException;

    String post(final String uri, final String content) throws RestClientException;

    String post(final String uri, final String content, final int status) throws RestClientException;

    String put(final String uri) throws RestClientException;

    String put(final String uri, final int status) throws RestClientException;

    String delete(final String uri) throws RestClientException;

    String delete(final String uri, final int status) throws RestClientException;

}

