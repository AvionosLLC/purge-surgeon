package com.avionos.aem.akamai.purgesurgeon.replication

import com.day.cq.commons.Externalizer
import org.apache.sling.api.SlingHttpServletRequest
import org.apache.sling.api.resource.ResourceResolver

class MockExternalizer implements Externalizer {

    @Override
    String externalLink(ResourceResolver resolver, String domain, String path) {
        path
    }

    @Override
    String externalLink(ResourceResolver resolver, String domain, String scheme, String path) {
        path
    }

    @Override
    String publishLink(ResourceResolver resolver, String path) {
        path
    }

    @Override
    String publishLink(ResourceResolver resolver, String scheme, String path) {
        path
    }

    @Override
    String authorLink(ResourceResolver resolver, String path) {
        path
    }

    @Override
    String authorLink(ResourceResolver resolver, String scheme, String path) {
        path
    }

    @Override
    String relativeLink(SlingHttpServletRequest request, String path) {
        path
    }

    @Override
    String absoluteLink(SlingHttpServletRequest request, String scheme, String path) {
        path
    }

    @Override
    String absoluteLink(ResourceResolver resolver, String scheme, String path) {
        path
    }

    @Override
    String absoluteLink(String scheme, String path) {
        path
    }
}
