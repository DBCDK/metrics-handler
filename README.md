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

Also available in this library is a special CDI interceptor capable of
incrementing a counter metric each time a monitored business method results in
an exception being thrown.

Include the interceptor by including the following in your beans.xml file:

```xml
<interceptors>
  <class>dk.dbc.commons.metricshandler.UnhandledExceptionInterceptor</class>
</interceptors>
```

Then annotate your business methods to be monitored with @ExceptionMonitored(QUALIFIED_NAME),
where QUALIFIED_NAME is a string identifying a class or enum constant
implementing the CounterMetric} interface.

```java
public class SomeCdiOrEjbBean {
    @ExceptionMonitored("fully.qualified.class.name.of.SomeCounterMetricsImpl")
    public void someBusinessMethod() {
        //...
    }
 
    @ExceptionMonitored("fully.qualified.name.for.SomeCounterMetricsImplEnum.ENUM_CONSTANT")
    public void someOtherBusinessMethod() {
        //...
    }
}
```
Be advised that the @ExceptionMonitored annotation will not work on a
message-drive-bean's onMessage() method since MDBs are not strictly speaking
CDI beans. In these cases you should instead extend UnhandledExceptionInterceptor
and override the 'String getValue(InvocationContext invocationContext)' method
to return a hard-coded value. The specialized interceptor can then be attached
using the @Interceptors annotation on the MDB class.

### development

**Requirements**

To build this project JDK 1.8 or higher and Apache Maven is required.

### License

Copyright Dansk Bibliotekscenter a/s. Licensed under GPLv3.
See license text in LICENSE.txt