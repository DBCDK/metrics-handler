/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GPLv3
 * See license text in LICENSE.txt or at https://opensource.dbc.dk/licenses/gpl-3.0/
 */

package dk.dbc.commons.metricshandler;

import org.eclipse.microprofile.metrics.MetricRegistry;
import org.eclipse.microprofile.metrics.Tag;
import org.eclipse.microprofile.metrics.annotation.RegistryType;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.time.Duration;

/**
 * CDI bean wrapping calls to metric-registry
 */
@RequestScoped
public class MetricsHandlerBean {
    @Inject
    @RegistryType(type = MetricRegistry.Type.APPLICATION)
    MetricRegistry metricRegistry;

    /**
     * Increments counter metric by one
     * @param metric {@link CounterMetric} supplying metrics metadata
     * @param tags Optional tags to attach to the metric
     */
    public void increment(CounterMetric metric, Tag... tags) {
         metricRegistry.counter(metric.getMetadata(), tags).inc(1L);
    }

    /**
     * Increments counter metric by given amount
     * @param metric {@link CounterMetric} supplying metrics metadata
     * @param increment size of increment
     * @param tags Optional tags to attach to the metric
     */
    public void increment(CounterMetric metric, long increment, Tag... tags) {
         metricRegistry.counter(metric.getMetadata(), tags).inc(increment);
    }

    /**
     * Adds recorded duration to simple-timer
     * @param metric {@link SimpleTimerMetric} supplying metrics metadata
     * @param duration recorded duration
     * @param tags Optional tags to attach to the metric
     */
    public void update(SimpleTimerMetric metric, Duration duration, Tag... tags) {
         metricRegistry.simpleTimer(metric.getMetadata(), tags).update(duration);
    }
}
