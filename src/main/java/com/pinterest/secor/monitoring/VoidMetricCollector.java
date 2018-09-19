package com.pinterest.secor.monitoring;

/**
 * /dev/null of the metric collectors
 */
public class VoidMetricCollector implements MetricCollector {

    @Override
    public void increment(String label, String topic) { }

    @Override
    public void increment(String label, int delta, String topic) { }

    @Override
    public void metric(String label, double value, String topic) { }

    @Override
    public void gauge(String label, double value, String topic) { }
}
