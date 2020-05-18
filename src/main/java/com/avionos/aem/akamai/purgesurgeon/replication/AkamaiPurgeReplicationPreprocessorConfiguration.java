package com.avionos.aem.akamai.purgesurgeon.replication;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "Akamai Purge Replication Preprocessor Configuration")
public @interface AkamaiPurgeReplicationPreprocessorConfiguration {

    @AttributeDefinition(name = "Enabled?", description = "Check to enable Akamai purge replication preprocessor.")
    boolean enabled() default false;

    @AttributeDefinition(name = "Included Paths", description = "List of paths that should be purged.")
    String[] includedPaths() default { "/content" };

    @AttributeDefinition(name = "Excluded Paths", description = "List of paths to exclude from purges.")
    String[] excludedPaths();

    @AttributeDefinition(name = "Delay", description = "Delay in seconds before queueing purge requests.")
    int delay() default 30;
}