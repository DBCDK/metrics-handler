metrics-handler
===============

A Java library providing a CDI bean with convenience methods for updating
metrics programmatically via a MetricRegistry.
 
### Maven

Add the dependency to your Maven pom.xml

```xml
<dependency>
  <groupId>dk.dbc</groupId>
  <artifactId>dbc-commons-metrics-handler</artifactId>
  <version>1.0-SNAPSHOT</version>
</dependency>
```

### usage

In your Java code

```java
import dk.dbc.commons.metricshandler.CounterMetric;
import dk.dbc.commons.metricshandler.MetricsHandlerBean;

enum AppCounterMetrics implements CounterMetric {
    APP_COUNTER_1(Metadata.builder()
        .withName("first counter")
        .withType(MetricType.COUNTER)
        .build());

    private final Metadata metadata;

    AppCounterMetrics(Metadata metadata) {
        this.metadata = validateMetadata(metadata);
    }

    @Override
    public Metadata getMetadata() {
        return metadata;
    }
}


@Inject MetricsHandlerBean metricsHandler;

metricsHandler.increment(AppCounterMetrics.APP_COUNTER_1);
metricsHandler.increment(AppCounterMetrics.APP_COUNTER_1,
       new Tag("someTag", "someValue"));

```


### development

**Requirements**

To build this project JDK 1.8 or higher and Apache Maven is required.

### License

Copyright Dansk Bibliotekscenter a/s. Licensed under GPLv3.
See license text in LICENSE.txt