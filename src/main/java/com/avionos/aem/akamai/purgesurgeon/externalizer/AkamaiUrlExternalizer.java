package com.avionos.aem.akamai.purgesurgeon.externalizer;

import org.apache.sling.api.resource.Resource;

import java.util.List;

/**
 * Externalizer to convert Sling resource paths to their corresponding Akamai (i.e. public) URLs.
 */
public interface AkamaiUrlExternalizer {

    /**
     * Get a list of Akamai URLs for the given resource.
     *
     * @param resource resource to purge from Akamai cache
     * @return list of externalized URLs for the given resource
     */
    List<String> getUrls(Resource resource);
}
