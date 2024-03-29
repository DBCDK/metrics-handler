package dk.dbc.commons.metricshandler;

import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;

import java.lang.reflect.InvocationTargetException;

/**
 * Interceptor capable of incrementing a counter metric each time a business
 * method results in an exception being thrown.
 * <p>
 * Annotate your business methods to be monitored with {@literal @}ExceptionMonitored(QUALIFIED_NAME).
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
            throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException,
            InvocationTargetException {
        final String value = getValue(invocationContext);

        // First assume value represents a class name
        try {
            final Class<?> aClass = Class.forName(value);
            metricsHandler.increment((CounterMetric) aClass.getDeclaredConstructor().newInstance());
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
