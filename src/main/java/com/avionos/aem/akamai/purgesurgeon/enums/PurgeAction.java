package com.avionos.aem.akamai.purgesurgeon.enums;

import com.avionos.aem.akamai.purgesurgeon.job.AkamaiPurgeJobConsumer;
import org.apache.sling.event.jobs.NotificationConstants;
import org.osgi.service.event.Event;

/**
 * Akamai purge action.
 */
public enum PurgeAction {
    INVALIDATE,
    DELETE;

    public static PurgeAction fromEvent(final Event event) {
        final String topic = (String) event.getProperty(NotificationConstants.NOTIFICATION_PROPERTY_JOB_TOPIC);

        return AkamaiPurgeJobConsumer.JOB_TOPIC_INVALIDATE.equals(topic) ? INVALIDATE : DELETE;
    }

    public String getOperation() {
        return this.name().toLowerCase();
    }
}
