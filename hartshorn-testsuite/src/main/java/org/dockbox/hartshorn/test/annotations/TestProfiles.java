package org.dockbox.hartshorn.test.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a test method or class as requiring specific profiles to be active in the test context.
 * These profiles will be activated when the test method or class is run.
 *
 * <p>Test profiles can be activated at both the method and class level. If a test class is annotated with
 * {@code @TestProfiles}, all test methods in that class will have the specified profiles active
 * during their execution.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
@Inherited
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface TestProfiles {

    /**
     * The profiles to activate in the test context when running the test.
     *
     * @return The profiles to activate in the test context when running the test
     */
    String[] value();
}
