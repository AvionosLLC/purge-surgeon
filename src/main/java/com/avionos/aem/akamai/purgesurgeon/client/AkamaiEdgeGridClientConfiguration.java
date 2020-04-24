package com.avionos.aem.akamai.purgesurgeon.client;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "Akamai Edge Grid Client Configuration")
public @interface AkamaiEdgeGridClientConfiguration {

    @AttributeDefinition(name = "Akamai Network")
    String network() default "production";

    @AttributeDefinition(name = "Akamai Hostname")
    String hostname() default "";

    @AttributeDefinition(name = "Akamai Access Token")
    String accessToken() default "";

    @AttributeDefinition(name = "Akamai Client Token")
    String clientToken() default "";

    @AttributeDefinition(name = "Akamai Client Secret")
    String clientSecret() default "";

    @AttributeDefinition(name = "Connection Request Timeout")
    int connectionRequestTimeout() default 1000;

    @AttributeDefinition(name = "Connect Timeout")
    int connectTimeout() default 3000;

    @AttributeDefinition(name = "Socket Timeout")
    int socketTimeout() default 30000;

    @AttributeDefinition(name = "Max Total Connections")
    int maxTotalConnections() default 25;

    @AttributeDefinition(name = "Max Connections Per Route")
    int maxConnectionsPerRoute() default 25;
}