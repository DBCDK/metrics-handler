/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GPLv3
 * See license text in LICENSE.txt or at https://opensource.dbc.dk/licenses/gpl-3.0/
 */

package dk.dbc.commons.metricshandler;

import org.eclipse.microprofile.metrics.Metadata;
import org.eclipse.microprofile.metrics.MetricType;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CounterMetricTest {
    @Test
    void validMetadata() {
        final Metadata metadata = Metadata.builder()
                .withName("valid_counter_metadata")
                .withType(MetricType.COUNTER)
                .build();
        final CounterMetricImpl counterMetric = new CounterMetricImpl(metadata);
        assertThat(counterMetric.getMetadata(), is(metadata));
    }

    @Test
    void invalidMetadata() {
        final Metadata metadata = Metadata.builder()
                .withName("invalid_counter_metadata")
                .withType(MetricType.SIMPLE_TIMER)
                .build();
        assertThrows(IllegalArgumentException.class, () -> new CounterMetricImpl(metadata));
    }

    private static class CounterMetricImpl implements CounterMetric {
        private final Metadata metadata;

        public CounterMetricImpl(Metadata metadata) {
            this.metadata = validateMetadata(metadata);
        }

        @Override
        public Metadata getMetadata() {
            return metadata;
        }
    }
}