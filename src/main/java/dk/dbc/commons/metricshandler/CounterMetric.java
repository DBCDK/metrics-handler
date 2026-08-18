/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GPLv3
 * See license text in LICENSE.txt or at https://opensource.dbc.dk/licenses/gpl-3.0/
 */

package dk.dbc.commons.metricshandler;

import org.eclipse.microprofile.metrics.Metadata;

@FunctionalInterface
public interface CounterMetric {
    Metadata getMetadata();
}
