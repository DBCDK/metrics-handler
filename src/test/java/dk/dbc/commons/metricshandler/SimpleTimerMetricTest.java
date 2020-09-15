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

class SimpleTimerMetricTest {
    @Test
    void validMetadata() {
        final Metadata metadata = Metadata.builder()
                .withName("valid_simple_timer_metadata")
                .withType(MetricType.SIMPLE_TIMER)
                .build();
        final SimpleTimerMetricImpl simpleTimerMetric = new SimpleTimerMetricImpl(metadata);
        assertThat(simpleTimerMetric.getMetadata(), is(metadata));
    }

    @Test
    void invalidMetadata() {
        final Metadata metadata = Metadata.builder()
                .withName("invalid_simple_timer_metadata")
                .withType(MetricType.COUNTER)
                .build();
        assertThrows(IllegalArgumentException.class, () -> new SimpleTimerMetricImpl(metadata));
    }

    private static class SimpleTimerMetricImpl implements SimpleTimerMetric {
        private final Metadata metadata;

        public SimpleTimerMetricImpl(Metadata metadata) {
            this.metadata = validateMetadata(metadata);
        }

        @Override
        public Metadata getMetadata() {
            return metadata;
        }
    }
}