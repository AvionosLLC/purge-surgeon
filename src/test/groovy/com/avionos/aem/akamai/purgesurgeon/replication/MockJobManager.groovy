package com.avionos.aem.akamai.purgesurgeon.replication

import org.apache.sling.event.jobs.Job
import org.apache.sling.event.jobs.JobBuilder
import org.apache.sling.event.jobs.JobManager
import org.apache.sling.event.jobs.Queue
import org.apache.sling.event.jobs.ScheduledJobInfo
import org.apache.sling.event.jobs.Statistics
import org.apache.sling.event.jobs.TopicStatistics

class MockJobManager implements JobManager {

    @Override
    Statistics getStatistics() {
        null
    }

    @Override
    Iterable<TopicStatistics> getTopicStatistics() {
        null
    }

    @Override
    Queue getQueue(String s) {
        null
    }

    @Override
    Iterable<Queue> getQueues() {
        null
    }

    @Override
    Job addJob(String s, Map<String, Object> map) {
        null
    }

    @Override
    Job getJobById(String s) {
        null
    }

    @Override
    boolean removeJobById(String s) {
        false
    }

    @Override
    Job getJob(String s, Map<String, Object> map) {
        null
    }

    @Override
    Collection<Job> findJobs(QueryType queryType, String s, long l, Map<String, Object>... maps) {
        null
    }

    @Override
    void stopJobById(String s) {

    }

    @Override
    Job retryJobById(String s) {
        null
    }

    @Override
    JobBuilder createJob(String s) {
        null
    }

    @Override
    Collection<ScheduledJobInfo> getScheduledJobs() {
        null
    }

    @Override
    Collection<ScheduledJobInfo> getScheduledJobs(String s, long l, Map<String, Object>... maps) {
        null
    }
}
