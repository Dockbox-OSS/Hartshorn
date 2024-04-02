package org.dockbox.hartshorn.inject;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.dockbox.hartshorn.util.introspect.annotations.Extends;

/**
 * Infrastructure specialization of {@link Priority}, used as a shorthand for {@code @Priority(Priority.INFRASTRUCTURE_PRIORITY)}.
 *
 * @since 0.6.0
 *
 * @author Guus Lieben
 */
@Target({ ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Extends(Priority.class)
@Priority(Priority.INFRASTRUCTURE_PRIORITY)
public @interface InfrastructurePriority {
}
