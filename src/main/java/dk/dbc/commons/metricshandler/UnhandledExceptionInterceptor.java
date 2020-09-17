/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GNU GPLv3
 * See license text in LICENSE.txt
 */

package dk.dbc.commons.metricshandler;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

/**
 * Interceptor capable of incrementing a counter metric each time a business
 * method results in an exception being thrown.
 * <p>
 * Include the interceptor by adding:
 * </p>
 * <pre>
 * {@literal <}interceptors>
 *   {@literal <}class>
 *       dk.dbc.commons.metricshandler.UnhandledExceptionInterceptor
 *   {@literal <}/class>
 * {@literal <}/interceptors>
 * </pre>
 * <p>
 * to your beans.xml file.
 * </p>
 * <p>
 * Then annotate your business methods to be monitored with {@literal @}ExceptionMonitored(QUALIFIED_NAME).
 * QUALIFIED_NAME must identify a class or enum constant implementing the {@link CounterMetric} interface.
 * </p>
 * <p>
 * Synopsis:
 * </p>
 * <pre>
 * public class SomeCdiOrEjbBean {
 *    {@literal @}ExceptionMonitored("fully.qualified.class.name.of.SomeCounterMetricsImpl")
 *     public void someBusinessMethod() {
 *        //...
 *     }
 *
 *    {@literal @}ExceptionMonitored("fully.qualified.name.for.SomeCounterMetricsImplEnum.ENUM_CONSTANT")
 *     public void someOtherBusinessMethod() {
 *        //...
 *     }
 * }
 * </pre>
 */
@ExceptionMonitored
@Interceptor
public class UnhandledExceptionInterceptor {
    @Inject MetricsHandlerBean metricsHandler;

    @AroundInvoke
    public Object count(InvocationContext invocationContext) throws Exception {
        try {
            return invocationContext.proceed();
        } catch (Exception e) {
            incrementMetric(invocationContext);
            throw e;
        }
    }

    private void incrementMetric(InvocationContext invocationContext)
            throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        final String value = getValue(invocationContext);

        // First assume value represents a class name
        try {
            final Class<?> aClass = Class.forName(value);
            metricsHandler.increment((CounterMetric)aClass.newInstance());
            return;
        } catch (ClassNotFoundException ignored) {}

        // value did not represent a known class, so now we assume
        // it represents an enum constant.
        final int lastDotPos = value.lastIndexOf('.');
        // Split into enum type and enum constant
        final Class<?> aClass = Class.forName(value.substring(0, lastDotPos));
        final String enumConstantName = value.substring(lastDotPos + 1);
        // Get enum constant instance and update metric
        for (Enum enumConstant : (Enum[]) aClass.getEnumConstants()) {
            if (enumConstant.name().equals(enumConstantName)) {
                metricsHandler.increment((CounterMetric)enumConstant);
                break;
            }
        }
    }

    /**
     * Extracts value parameter of the @{@link ExceptionMonitored} annotation
     * @param invocationContext invocation context for this interception
     * @return annotation value
     */
    protected String getValue(InvocationContext invocationContext) {
        final ExceptionMonitored annotation = invocationContext.getMethod().getAnnotation(ExceptionMonitored.class);
        return annotation.value();
    }
}
