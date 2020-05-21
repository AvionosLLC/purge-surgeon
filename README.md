# Purge Surgeon

[Avionos](https://www.avionos.com)

## Overview

The Purge Surgeon is an OSGi bundle for the Adobe Experience Manager (AEM) platform that uses the [Akamai Fast Purge API](https://developer.akamai.com/api/core_features/fast_purge/v3.html) to purge content from Akamai when content is replicated.

The included replication preprocessor is triggered by replication requests for a configurable set of content paths.  When content is activated, deactivated, or deleted, the preprocessor creates a Sling job that sends a request using the Akamai Fast Purge API to invalidate or delete the externalized page/asset URL.

The Purge Surgeon API also includes methods for purging edge content by CP (content provider) code or cache tag.

## Compatibility

Bundle Version | AEM Version(s)
------------ | -------------
0.x.x | 6.5

## Installation

1. Add the bundle as a dependency to an existing AEM project:

```xml
<dependency>
    <groupId>com.avionos.aem.akamai</groupId>
    <artifactId>purge-surgeon</artifactId>
    <version>0.1.0</version>
    <scope>provided</scope>
</dependency>
```

2. Add a [service user mapping](https://docs.adobe.com/content/help/en/experience-manager-65/administering/security/security-service-users.html#ServiceUsersandMappings) for the `purge-surgeon` bundle.

3. Configure the Akamai services as outlined below.

## Configuration

### Akamai Purge Replication Preprocessor Configuration

Enable/disable the Akamai replication preprocessor and configure which resource paths should be purged and/or excluded from purging.

### Akamai Edge Grid Client Configuration

Configure the Akamai network, hostname, and credentials (access token, client token, client secret) in addition to HTTP client timeouts and connection parameters.

## URL Externalizers

Projects utilizing the Purge Surgeon may provide a service implementing the `com.avionos.aem.akamai.purgesurgeon.externalizer.AkamaiUrlExternalizer` interface to customize how URLs are externalized for replicated resource paths.  See the `getUrls()` method in `com.avionos.aem.akamai.purgesurgeon.replication.AkamaiPurgeReplicationPreprocessor` for the default externalization behavior if no custom URL externalizer service is implemented.

## Edge Grid Client API

The Purge Surgeon API can be used without enabling (or in addition to) replication preprocessing to purge content from Akamai edge servers by CP code or cache tag.

For example, using the [AEM Groovy Console](https://github.com/icfnext/aem-groovy-console), executing the following script would purge edge content for the CP codes "123" and "456":

```groovy
def akamaiEdgeGridClient = getService("com.avionos.aem.akamai.purgesurgeon.client.AkamaiEdgeGridClient")

akamaiEdgeGridClient.invalidateCpCodes([123, 456])
```

## Javadoc

https://javadoc.io/doc/com.avionos.aem.akamai/purge-surgeon/latest/index.html

## Versioning

Follows [Semantic Versioning](http://semver.org/) guidelines.