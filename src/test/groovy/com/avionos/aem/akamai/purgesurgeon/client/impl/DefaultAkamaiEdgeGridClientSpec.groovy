package com.avionos.aem.akamai.purgesurgeon.client.impl

import com.avionos.aem.akamai.purgesurgeon.client.AkamaiEdgeGridClient
import com.github.tomakehurst.wiremock.common.Slf4jNotifier
import com.github.tomakehurst.wiremock.junit.WireMockRule
import com.icfolson.aem.prosper.specs.ProsperSpec
import groovy.json.JsonBuilder
import org.apache.http.HttpHeaders
import org.apache.http.NoHttpResponseException
import org.apache.http.client.HttpRequestRetryHandler
import org.apache.http.client.HttpResponseException
import org.apache.http.config.RegistryBuilder
import org.apache.http.conn.socket.ConnectionSocketFactory
import org.apache.http.conn.socket.PlainConnectionSocketFactory
import org.apache.http.conn.ssl.NoopHostnameVerifier
import org.apache.http.conn.ssl.SSLConnectionSocketFactory
import org.apache.http.conn.ssl.TrustSelfSignedStrategy
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.impl.conn.BasicHttpClientConnectionManager
import org.apache.http.protocol.HttpContext
import org.apache.http.ssl.SSLContextBuilder
import org.junit.Rule
import spock.lang.Shared
import spock.lang.Unroll

import javax.net.ssl.SSLContext

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson
import static com.github.tomakehurst.wiremock.client.WireMock.matching
import static com.github.tomakehurst.wiremock.client.WireMock.post
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options
import static org.apache.http.entity.ContentType.APPLICATION_JSON

@Unroll
class DefaultAkamaiEdgeGridClientSpec extends ProsperSpec {

    static class TestRetryHandler implements HttpRequestRetryHandler {
        @Override
        boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
            executionCount <= 3 || exception instanceof NoHttpResponseException
        }
    }

    @Shared
    AkamaiEdgeGridClient akamaiEdgeGridClient

    @Rule
    WireMockRule wireMockRule = new WireMockRule(options()
        .httpsPort(8001)
        .notifier(new Slf4jNotifier(true)))

    def setupSpec() {
        akamaiEdgeGridClient = slingContext.registerInjectActivateService(new DefaultAkamaiEdgeGridClient())

        // set httpclient using custom configuration for testing
        akamaiEdgeGridClient.httpClient = buildHttpClient()
    }

    def "invalidate urls returns success response"() {
        setup:
        def json = new JsonBuilder(["objects": urls]).toString()

        stubFor("/ccu/v3/invalidate/url/production", json, 200)

        when:
        akamaiEdgeGridClient.invalidateUrls(urls)

        then:
        notThrown(HttpResponseException)

        where:
        urls << [["/one"], ["/one", "/two"]]
    }

    def "delete urls returns success response"() {
        setup:
        def json = new JsonBuilder(["objects": urls]).toString()

        stubFor("/ccu/v3/delete/url/production", json, 200)

        when:
        akamaiEdgeGridClient.deleteUrls(urls)

        then:
        notThrown(HttpResponseException)

        where:
        urls << [["/one"], ["/one", "/two"]]
    }

    def "invalidate CP codes returns success response"() {
        setup:
        def json = new JsonBuilder(["objects": cpCodes]).toString()

        stubFor("/ccu/v3/invalidate/cpcode/production", json, 200)

        when:
        akamaiEdgeGridClient.invalidateCpCodes(cpCodes)

        then:
        notThrown(HttpResponseException)

        where:
        cpCodes << [[123], [123, 456]]
    }

    def "delete CP codes returns success response"() {
        setup:
        def json = new JsonBuilder(["objects": cpCodes]).toString()

        stubFor("/ccu/v3/delete/cpcode/production", json, 200)

        when:
        akamaiEdgeGridClient.deleteCpCodes(cpCodes)

        then:
        notThrown(HttpResponseException)

        where:
        cpCodes << [[123], [123, 456]]
    }

    def "invalidate cache tags returns success response"() {
        setup:
        def json = new JsonBuilder(["objects": cacheTags]).toString()

        stubFor("/ccu/v3/invalidate/tag/production", json, 200)

        when:
        akamaiEdgeGridClient.invalidateCacheTags(cacheTags)

        then:
        notThrown(HttpResponseException)

        where:
        cacheTags << [["tag1"], ["tag1", "tag2"]]
    }

    def "delete cache tags returns success response"() {
        setup:
        def json = new JsonBuilder(["objects": cacheTags]).toString()

        stubFor("/ccu/v3/delete/tag/production", json, 200)

        when:
        akamaiEdgeGridClient.deleteCacheTags(cacheTags)

        then:
        notThrown(HttpResponseException)

        where:
        cacheTags << [["tag1"], ["tag1", "tag2"]]
    }

    def "error response from akamai throws exception"() {
        setup:
        def urls = ["/one"]
        def json = new JsonBuilder(["objects": urls]).toString()

        wireMockRule.stubFor(post(urlEqualTo("/ccu/v3/invalidate/url/production"))
            .withHeader(HttpHeaders.ACCEPT, matching(APPLICATION_JSON.mimeType))
            .withHeader(HttpHeaders.CONTENT_TYPE, matching(APPLICATION_JSON.mimeType))
            .withRequestBody(equalToJson(json))
            .willReturn(aResponse().withStatus(400)))

        when:
        akamaiEdgeGridClient.invalidateUrls(urls)

        then:
        thrown(HttpResponseException)
    }

    private void stubFor(String url, String json, Integer statusCode) {
        wireMockRule.stubFor(post(urlEqualTo(url))
            .withHeader(HttpHeaders.ACCEPT, matching(APPLICATION_JSON.mimeType))
            .withHeader(HttpHeaders.CONTENT_TYPE, matching(APPLICATION_JSON.mimeType))
            .withRequestBody(equalToJson(json))
            .willReturn(aResponse().withStatus(statusCode)))
    }

    private CloseableHttpClient buildHttpClient() {
        def sslConnectionSocketFactory = new SSLConnectionSocketFactory(sslContext, new NoopHostnameVerifier())

        def registry = RegistryBuilder.<ConnectionSocketFactory> create()
            .register("http", PlainConnectionSocketFactory.getSocketFactory())
            .register("https", sslConnectionSocketFactory)
            .build()

        def connectionManager = new BasicHttpClientConnectionManager(registry)

        HttpClientBuilder.create()
            .setConnectionManager(connectionManager)
            .setRetryHandler(new TestRetryHandler())
            .build()
    }

    private SSLContext getSslContext() {
        SSLContextBuilder.create().loadTrustMaterial(new TrustSelfSignedStrategy()).build()
    }
}
