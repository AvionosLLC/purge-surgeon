package com.avionos.aem.akamai.purgesurgeon.client.impl;

import com.akamai.edgegrid.signer.ClientCredential;
import com.akamai.edgegrid.signer.apachehttpclient.ApacheHttpClientEdgeGridInterceptor;
import com.akamai.edgegrid.signer.apachehttpclient.ApacheHttpClientEdgeGridRoutePlanner;
import com.avionos.aem.akamai.purgesurgeon.client.AkamaiEdgeGridClient;
import com.avionos.aem.akamai.purgesurgeon.client.AkamaiEdgeGridClientConfiguration;
import com.avionos.aem.akamai.purgesurgeon.enums.PurgeAction;
import com.avionos.aem.akamai.purgesurgeon.enums.PurgeType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.propertytypes.ServiceDescription;
import org.osgi.service.component.propertytypes.ServiceVendor;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component(immediate = true, service = AkamaiEdgeGridClient.class)
@Designate(ocd = AkamaiEdgeGridClientConfiguration.class)
@ServiceDescription("Akamai Edge Grid Client for Fast Purge API v3")
@ServiceVendor("Avionos")
public final class DefaultAkamaiEdgeGridClient implements AkamaiEdgeGridClient {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final Logger LOG = LoggerFactory.getLogger(DefaultAkamaiEdgeGridClient.class);

    private volatile CloseableHttpClient httpClient;

    private volatile String network;

    private volatile String hostname;

    @Override
    public void invalidateUrls(final List<String> urls) throws IOException, URISyntaxException {
        purge(getJson(urls), PurgeAction.INVALIDATE, PurgeType.URL);
    }

    @Override
    public void invalidateCpCodes(final List<Integer> cpCodes) throws IOException, URISyntaxException {
        purge(getJson(cpCodes), PurgeAction.INVALIDATE, PurgeType.CP_CODE);
    }

    @Override
    public void invalidateCacheTags(final List<String> cacheTags) throws IOException, URISyntaxException {
        purge(getJson(cacheTags), PurgeAction.INVALIDATE, PurgeType.CACHE_TAG);
    }

    @Override
    public void deleteUrls(final List<String> urls) throws IOException, URISyntaxException {
        purge(getJson(urls), PurgeAction.DELETE, PurgeType.URL);
    }

    @Override
    public void deleteCpCodes(final List<Integer> cpCodes) throws IOException, URISyntaxException {
        purge(getJson(cpCodes), PurgeAction.DELETE, PurgeType.CP_CODE);
    }

    @Override
    public void deleteCacheTags(final List<String> cacheTags) throws IOException, URISyntaxException {
        purge(getJson(cacheTags), PurgeAction.DELETE, PurgeType.CACHE_TAG);
    }

    @Activate
    @Modified
    protected void activate(final AkamaiEdgeGridClientConfiguration configuration) {
        final ClientCredential credential = ClientCredential.builder()
            .accessToken(configuration.accessToken())
            .clientSecret(configuration.clientSecret())
            .clientToken(configuration.clientToken())
            .host(configuration.hostname())
            .build();

        final RequestConfig requestConfig = RequestConfig.custom()
            .setConnectTimeout(configuration.connectTimeout())
            .setConnectionRequestTimeout(configuration.connectionRequestTimeout())
            .setSocketTimeout(configuration.socketTimeout())
            .build();

        final PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();

        connectionManager.setMaxTotal(configuration.maxTotalConnections());
        connectionManager.setDefaultMaxPerRoute(configuration.maxConnectionsPerRoute());

        httpClient = HttpClientBuilder.create()
            .setDefaultRequestConfig(requestConfig)
            .setConnectionManager(connectionManager)
            .addInterceptorFirst(new ApacheHttpClientEdgeGridInterceptor(credential))
            .setRoutePlanner(new ApacheHttpClientEdgeGridRoutePlanner(credential))
            .build();

        network = configuration.network();
        hostname = configuration.hostname();
    }

    @Deactivate
    protected void deactivate() throws IOException {
        httpClient.close();
    }

    private void purge(final String json, final PurgeAction purgeAction, final PurgeType purgeType)
        throws IOException, URISyntaxException {
        final HttpEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);

        final URI uri = new URIBuilder()
            .setScheme("https")
            .setHost(hostname)
            .setPath(new StringBuilder()
                .append("/ccu/v3/")
                .append(purgeAction.getOperation())
                .append("/")
                .append(purgeType.getType())
                .append("/")
                .append(network)
                .toString())
            .build();

        LOG.info("sending {} request to URI : {} with JSON entity : {}", purgeAction, uri, json);

        final String mimeType = ContentType.APPLICATION_JSON.getMimeType();

        final HttpUriRequest request = RequestBuilder.post(uri)
            .setEntity(entity)
            .addHeader(HttpHeaders.ACCEPT, mimeType)
            .addHeader(HttpHeaders.CONTENT_TYPE, mimeType)
            .build();

        try (final CloseableHttpResponse response = httpClient.execute(request)) {
            final StatusLine statusLine = response.getStatusLine();
            final String responseBody = EntityUtils.toString(response.getEntity());

            LOG.info("akamai response body : {}", responseBody);

            if (statusLine.getStatusCode() >= 300) {
                throw new HttpResponseException(statusLine.getStatusCode(), statusLine.getReasonPhrase());
            }
        }
    }

    private String getJson(final Object values) throws IOException {
        return getJsonString(Collections.singletonMap("objects", values));
    }

    private String getJsonString(final Map<String, Object> payload) throws IOException {
        return MAPPER.writeValueAsString(payload);
    }
}
