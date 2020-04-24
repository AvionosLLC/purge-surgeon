# Purge Surgeon

[Avionos](https://www.avionos.com)

## Overview

The Purge Surgeon is an OSGi bundle for the Adobe Experience Manager (AEM) platform that uses the [Akamai Fast Purge API](https://developer.akamai.com/api/core_features/fast_purge/v3.html) to purge content from Akamai when content is replicated.

The included event handler listens to replication events for a configurable set of content paths.  When content is activated, deactivated, or deleted, the event handler creates a Sling job that sends a request using the Akamai Fast Purge API to invalidate or delete the externalized page/asset URL.

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

2. Add a [service user mapping](https://helpx.adobe.com/experience-manager/6-4/sites/administering/using/security-service-users.html#ServiceUsersandMappings) for the `purge-surgeon` bundle.

3. Configure the Akamai services as outlined below.

## Configuration

### Akamai Purge Replication Event Handler Configuration

Enable/disable the Akamai replication event handler and configure which resource paths should be purged.

### Akamai Edge Grid Client Configuration

Configure the Akamai network, hostname, and credentials (access token, client token, client secret).

## Job Cancellation Event Handlers

If the Akamai purge request fails, the underlying Sling job will be cancelled and an event will be generated containing the job topic and affected page/asset path.  

Applications using this bundle may register services implementing the `com.avionos.aem.akamai.purgesurgeon.job.delegate.AkamaiPurgeJobCancelledEventHandlerDelegate` interface to provide additional error/failure handling behavior (e.g. email notification).  These services will be automatically bound to the default 
Akamai purge job cancellation event handler by the framework.

## URL Externalizers

TODO

## Versioning

Follows [Semantic Versioning](http://semver.org/) guidelines.