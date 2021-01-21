package org.dockbox.selene.core.annotations.delegate;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The annotation used to mark a parameter as instance provider for methods annotated with {@link Proxy.Target}.
 * Parameters with this annotation are excluded from the target method lookup, and are automatically injected.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Instance {
}
