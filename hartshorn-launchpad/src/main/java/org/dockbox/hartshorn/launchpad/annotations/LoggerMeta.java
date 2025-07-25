package org.dockbox.hartshorn.launchpad.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Configuration annotation for logger types (e.g. {@link org.slf4j.Logger}. This allows for further customization
 * of the logger.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface LoggerMeta {

    /**
     * The name of the logger. When configured, this takes precedence over the automatically resolved logger name.
     *
     * @return The name of the logger, or an empty string if the name is not set.
     */
    String name() default "";
}
