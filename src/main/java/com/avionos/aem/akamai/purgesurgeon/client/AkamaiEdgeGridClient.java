package com.avionos.aem.akamai.purgesurgeon.client;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

/**
 * Edge Grid client for sending invalidate/delete requests to Akamai for URLs, CP codes, or cache tags.
 */
public interface AkamaiEdgeGridClient {

    /**
     * Invalidate the given list of URLs.
     *
     * @param urls externalized URLs
     * @throws IOException if error occurs in Akamai request
     * @throws URISyntaxException if Akamai URL is misconfigured
     */
    void invalidateUrls(List<String> urls) throws IOException, URISyntaxException;

    /**
     * Invalidate the given list of CP codes.
     *
     * @param cpCodes CP codes
     * @throws IOException if error occurs in Akamai request
     * @throws URISyntaxException if Akamai URL is misconfigured
     */
    void invalidateCpCodes(List<Integer> cpCodes) throws IOException, URISyntaxException;

    /**
     * Invalidate the given list of cache tags.
     *
     * @param cacheTags cache tags
     * @throws IOException if error occurs in Akamai request
     * @throws URISyntaxException if Akamai URL is misconfigured
     */
    void invalidateCacheTags(List<String> cacheTags) throws IOException, URISyntaxException;

    /**
     * Delete the given list of URLs.
     *
     * @param urls externalized URLs
     * @throws IOException if error occurs in Akamai request
     * @throws URISyntaxException if Akamai URL is misconfigured
     */
    void deleteUrls(List<String> urls) throws IOException, URISyntaxException;

    /**
     * Delete the given list of CP codes.
     *
     * @param cpCodes CP codes
     * @throws IOException if error occurs in Akamai request
     * @throws URISyntaxException if Akamai URL is misconfigured
     */
    void deleteCpCodes(List<Integer> cpCodes) throws IOException, URISyntaxException;

    /**
     * Delete the given list of cache tags.
     *
     * @param cacheTags cache tags
     * @throws IOException if error occurs in Akamai request
     * @throws URISyntaxException if Akamai URL is misconfigured
     */
    void deleteCacheTags(List<String> cacheTags) throws IOException, URISyntaxException;
}
