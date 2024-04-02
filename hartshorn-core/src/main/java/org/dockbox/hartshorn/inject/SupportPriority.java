package org.dockbox.hartshorn.inject;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.dockbox.hartshorn.util.introspect.annotations.Extends;

/**
 * Support specialization of {@link Priority}, used as a shorthand for {@code @Priority(Priority.SUPPORT_PRIORITY)}.
 *
 * @since 0.6.0
 *
 * @author Guus Lieben
 */
@Target({ ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Extends(Priority.class)
@Priority(Priority.SUPPORT_PRIORITY)
public @interface SupportPriority {
}
