package com.avionos.aem.akamai.purgesurgeon.replication;

import com.avionos.aem.akamai.purgesurgeon.externalizer.AkamaiUrlExternalizer;
import com.avionos.aem.akamai.purgesurgeon.job.AkamaiPurgeJobConsumer;
import com.day.cq.commons.Externalizer;
import com.day.cq.replication.Preprocessor;
import com.day.cq.replication.ReplicationAction;
import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.ReplicationException;
import com.day.cq.replication.ReplicationOptions;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.event.jobs.JobManager;
import org.apache.sling.event.jobs.ScheduledJobInfo;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Preprocessor for replication events to purge content from Akamai.
 */
@Component(immediate = true, service = Preprocessor.class)
@Designate(ocd = AkamaiPurgeReplicationPreprocessorConfiguration.class)
public final class AkamaiPurgeReplicationPreprocessor implements Preprocessor {

    private static final Logger LOG = LoggerFactory.getLogger(AkamaiPurgeReplicationPreprocessor.class);

    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

    private static final String SUBSERVICE_NAME = "Akamai";

    @Reference
    private JobManager jobManager;

    @Reference(cardinality = ReferenceCardinality.OPTIONAL)
    private AkamaiUrlExternalizer akamaiUrlExternalizer;

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Reference
    private Externalizer externalizer;

    private volatile boolean enabled;

    private volatile List<String> includedPaths;

    private volatile List<String> excludedPaths;

    private volatile int delay;

    @Override
    public void preprocess(final ReplicationAction replicationAction, final ReplicationOptions replicationOptions)
        throws ReplicationException {
        final ReplicationActionType replicationActionType = replicationAction.getType();
        final String path = replicationAction.getPath();

        if (enabled) {
            if (isIncluded(path)) {
                if (replicationActionType.equals(ReplicationActionType.ACTIVATE)) {
                    LOG.info("handling activate event for path : {}", path);

                    addJob(AkamaiPurgeJobConsumer.JOB_TOPIC_INVALIDATE, path);
                } else if (replicationActionType.equals(ReplicationActionType.DEACTIVATE)) {
                    LOG.info("handling deactivate event for path : {}", path);

                    addJob(AkamaiPurgeJobConsumer.JOB_TOPIC_DELETE, path);
                } else if (replicationActionType.equals(ReplicationActionType.DELETE)) {
                    LOG.info("handling delete event for path : {}", path);

                    addJob(AkamaiPurgeJobConsumer.JOB_TOPIC_DELETE, path);
                } else {
                    LOG.debug("replication action type : {} not handled for path : {}", replicationActionType.getName(),
                        path);
                }
            } else {
                LOG.debug("path not included {}, ignoring", path);
            }
        } else {
            LOG.debug("event handler disabled, ignoring {} event for path {}...", replicationActionType.getName(),
                path);
        }
    }

    @Activate
    @Modified
    protected void activate(final AkamaiPurgeReplicationPreprocessorConfiguration configuration) {
        enabled = configuration.enabled();
        includedPaths = getConfiguredPaths(configuration.includedPaths());
        excludedPaths = getConfiguredPaths(configuration.excludedPaths());
        delay = configuration.delay();
    }

    /**
     * Get the configured included or excluded paths.
     *
     * @param paths paths configuration property
     * @return list of non-empty paths
     */
    protected List<String> getConfiguredPaths(final String[] paths) {
        return Optional.ofNullable(paths)
            .map(Arrays :: stream)
            .orElseGet(Stream :: empty)
            .filter(StringUtils :: isNotBlank)
            .distinct()
            .collect(Collectors.toList());
    }

    /**
     * Check if the given page path is included according to the rules defined in the OSGi service configuration.
     *
     * @param path replicated page path
     * @return true if path is included, false if not
     */
    protected boolean isIncluded(final String path) {
        return includedPaths.stream().anyMatch(path :: startsWith) &&
            excludedPaths.stream().noneMatch(path :: startsWith);
    }

    /**
     * Add a job to the queue with the given topic and page path.
     *
     * @param topic job topic
     * @param path page path
     * @throws ReplicationException if unable to authenticate resource resolver
     */
    private void addJob(final String topic, final String path) throws ReplicationException {
        if (delay > 0) {
            final ScheduledJobInfo scheduledJobInfo = jobManager.createJob(topic)
                .properties(getJobProperties(path))
                .schedule()
                .at(getScheduledDate().getTime())
                .add();

            LOG.info("added job with topic : {} for path : {} at {}", topic, path, new SimpleDateFormat(DATE_FORMAT)
                .format(scheduledJobInfo.getNextScheduledExecution()));
        } else {
            jobManager.addJob(topic, getJobProperties(path));

            LOG.info("added job with topic : {} for path : {}", topic, path);
        }
    }

    /**
     * Get a map of job properties for the given page path.
     *
     * @param path page path
     * @return job properties
     * @throws ReplicationException if unable to authenticate resource resolver
     */
    private Map<String, Object> getJobProperties(final String path) throws ReplicationException {
        return Collections.singletonMap(AkamaiPurgeJobConsumer.PROPERTY_URLS, getUrls(path).toArray(new String[0]));
    }

    /**
     * Get the externalized URLs for the given resource path.
     *
     * @param path resource path
     * @return list of externalized URLs
     * @throws ReplicationException if unable to authenticate resource resolver
     */
    private List<String> getUrls(final String path) throws ReplicationException {
        try (final ResourceResolver resourceResolver = resourceResolverFactory.getServiceResourceResolver(null)) {
            final Resource resource = resourceResolver.getResource(path);

            return Optional.ofNullable(akamaiUrlExternalizer)
                .map(externalizer -> externalizer.getUrls(resource))
                .orElse(Collections.singletonList(externalizer.externalLink(resourceResolver, Externalizer.PUBLISH,
                    resourceResolver.map(resource.getPath()))));
        } catch (LoginException e) {
            // re-throw as runtime exception to propagate up to the event framework
            throw new ReplicationException("error authenticating resource resolver", e);
        }
    }

    /**
     * Get the scheduled job date based on the configured delay.
     *
     * @return date to execute purge job
     */
    private Calendar getScheduledDate() {
        final Calendar date = Calendar.getInstance();

        date.add(Calendar.SECOND, delay);

        return date;
    }
}
