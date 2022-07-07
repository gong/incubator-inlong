/*
 *   Licensed to the Apache Software Foundation (ASF) under one
 *   or more contributor license agreements.  See the NOTICE file
 *   distributed with this work for additional information
 *   regarding copyright ownership.  The ASF licenses this file
 *   to you under the Apache License, Version 2.0 (the
 *   "License"); you may not use this file except in compliance
 *   with the License.  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package org.apache.inlong.sort.metric;

import org.apache.flink.metrics.Counter;
import org.apache.flink.metrics.Gauge;
import org.apache.flink.metrics.Histogram;
import org.apache.flink.metrics.Meter;
import org.apache.flink.metrics.Metric;
import org.apache.flink.metrics.MetricConfig;
import org.apache.flink.metrics.MetricGroup;
import org.apache.flink.metrics.reporter.AbstractReporter;
import org.apache.flink.metrics.reporter.Scheduled;

import java.util.Map;

/**
 * report metric data to inlong manager service
 */
public class SortReporter extends AbstractReporter implements Scheduled {

    public static final String ARG_HOST = "host";
    public static final String ARG_PORT = "port";

    /**
     * Filter the given string and generate a resulting string from it.
     *
     * <p>For example, one implementation could filter out invalid characters from the input string.
     *
     * @param input Input string
     * @return Filtered result string
     */
    @Override
    public String filterCharacters(String input) {
        return input;
    }

    /**
     * Configures this reporter.
     *
     * <p>If the reporter was instantiated generically and hence parameter-less, this method is the
     * place where the reporter sets it's basic fields based on configuration values. Otherwise,
     * this method will typically be a no-op since resources can be acquired in the constructor.
     *
     * <p>This method is always called first on a newly instantiated reporter.
     *
     * @param config A properties object that contains all parameters set for this reporter.
     */
    @Override
    public void open(MetricConfig config) {
        String host = config.getString(ARG_HOST, null);
        int port = config.getInteger(ARG_PORT, -1);

        if (host == null || host.length() == 0 || port < 1) {
            throw new IllegalArgumentException(
                    "Invalid host/port configuration. Host: " + host + " Port: " + port);
        }
        log.info("Configured SortReporter with {host:{}, port:{}}", host, port);
    }

    /**
     * Closes this reporter. Should be used to close channels, streams and release resources.
     */
    @Override
    public void close() {
    }

    @Override
    public void notifyOfAddedMetric(Metric metric, String metricName, MetricGroup group) {
        final String name = group.getMetricIdentifier(metricName, this);

        synchronized (this) {
            if (metric instanceof Counter) {
                counters.put((Counter) metric, name);
            } else if (metric instanceof Gauge) {
                gauges.put((Gauge<?>) metric, name);
            } else if (metric instanceof Histogram) {
                histograms.put((Histogram) metric, name);
            } else if (metric instanceof Meter) {
                meters.put((Meter) metric, name);
            } else {
                log.warn(
                        "Cannot add unknown metric type {}. This indicates that the reporter "
                                + "does not support this metric type.",
                        metric.getClass().getName());
            }
        }
    }

    /**
     * Report the current measurements. This method is called periodically by the metrics registry
     * that uses the reporter.
     */
    @Override
    public void report() {
        /* for (Map.Entry<Gauge<?>, String> entry : gauges.entrySet()) {
            log.info(entry.getValue() + " =(Gauge) " + entry.getKey().getValue());
        }*/

        for (Map.Entry<Counter, String> entry : counters.entrySet()) {
            if (entry.getValue().contains("numRecordsOut") || entry.getValue().contains("numRecordsIn")
                    || entry.getValue().contains("numBytesOut") || entry.getValue().contains("gongxin")) {
                log.info(entry.getValue() + " =(Counter) " + entry.getKey().getCount());
            }
        }

        /*for (Map.Entry<Histogram, String> entry : histograms.entrySet()) {
            log.info(entry.getValue() + " =(Histogram) " + entry.getKey().getCount());
        }*/

        for (Map.Entry<Meter, String> entry : meters.entrySet()) {
            if (entry.getValue().contains("numRecordsOutPerSecond") || entry.getValue().contains(
                    "numRecordsIntPerSecond") || entry.getValue().contains("numBytesOutPerSecond")
                    || entry.getValue().contains("gongxin")) {
                log.info(entry.getValue() + " =(Meter) " + entry.getKey().getCount());
            }
        }
    }
}
