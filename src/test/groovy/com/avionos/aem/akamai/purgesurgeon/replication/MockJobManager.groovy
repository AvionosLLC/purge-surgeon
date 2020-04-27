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
        return null
    }

    @Override
    Iterable<TopicStatistics> getTopicStatistics() {
        return null
    }

    @Override
    Queue getQueue(String s) {
        return null
    }

    @Override
    Iterable<Queue> getQueues() {
        return null
    }

    @Override
    Job addJob(String s, Map<String, Object> map) {
        return null
    }

    @Override
    Job getJobById(String s) {
        return null
    }

    @Override
    boolean removeJobById(String s) {
        return false
    }

    @Override
    Job getJob(String s, Map<String, Object> map) {
        return null
    }

    @Override
    Collection<Job> findJobs(QueryType queryType, String s, long l, Map<String, Object>... maps) {
        return null
    }

    @Override
    void stopJobById(String s) {

    }

    @Override
    Job retryJobById(String s) {
        return null
    }

    @Override
    JobBuilder createJob(String s) {
        return null
    }

    @Override
    Collection<ScheduledJobInfo> getScheduledJobs() {
        return null
    }

    @Override
    Collection<ScheduledJobInfo> getScheduledJobs(String s, long l, Map<String, Object>... maps) {
        return null
    }
}
