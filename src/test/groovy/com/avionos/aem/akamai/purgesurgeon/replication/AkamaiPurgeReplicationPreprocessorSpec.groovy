package com.avionos.aem.akamai.purgesurgeon.replication

import com.avionos.aem.akamai.purgesurgeon.externalizer.AkamaiUrlExternalizer
import com.day.cq.commons.Externalizer
import com.icfolson.aem.prosper.specs.ProsperSpec
import org.apache.sling.api.resource.Resource
import org.apache.sling.event.jobs.JobManager
import spock.lang.Shared
import spock.lang.Unroll

@Unroll
class AkamaiPurgeReplicationPreprocessorSpec extends ProsperSpec {

    class TestAkamaiUrlExternalizer implements AkamaiUrlExternalizer {

        @Override
        List<String> getUrls(Resource resource) {
            [resource.path]
        }
    }

    @Shared
    AkamaiPurgeReplicationPreprocessor preprocessor

    def setupSpec() {
        slingContext.registerService(AkamaiUrlExternalizer, new TestAkamaiUrlExternalizer())
        slingContext.registerService(JobManager, new MockJobManager())
        slingContext.registerService(Externalizer, new MockExternalizer())

        preprocessor = slingContext.registerInjectActivateService(new AkamaiPurgeReplicationPreprocessor())
    }

    def "get configured paths"() {
        expect:
        preprocessor.getConfiguredPaths(paths) == configuredPaths

        where:
        paths                          | configuredPaths
        null                           | []
        [] as String[]                 | []
        [""] as String[]               | []
        ["/a/b", " "] as String[]      | ["/a/b"]
        ["/a/b"] as String[]           | ["/a/b"]
        ["/a/b", "/a/b"] as String[]   | ["/a/b"]
        ["/a/b", "/a/b/c"] as String[] | ["/a/b", "/a/b/c"]
    }

    def "is included"() {
        expect:
        preprocessor.isIncluded(path) == isIncluded

        where:
        path                    | isIncluded
        "/content/avionos"      | true
        "/content/avionos/test" | true
        "/content/test"         | true
        "/content/test/avionos" | true
        "/content/other"        | false
        "/content/dam"          | false
        "/content/dam/avionos"  | false
    }
}
