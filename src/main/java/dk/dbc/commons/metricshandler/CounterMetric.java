/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GPLv3
 * See license text in LICENSE.txt or at https://opensource.dbc.dk/licenses/gpl-3.0/
 */

package dk.dbc.commons.metricshandler;

import org.eclipse.microprofile.metrics.Metadata;
import org.eclipse.microprofile.metrics.MetricType;

@FunctionalInterface
public interface CounterMetric {
    default Metadata validateMetadata(Metadata metadata) {
        if (metadata.getTypeRaw() != MetricType.COUNTER) {
            throw new IllegalArgumentException(
                    "Metric " + metadata.getName() + " is not of type " + MetricType.COUNTER);
        }
        return metadata;
    }
    Metadata getMetadata();
}
