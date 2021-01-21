package org.dockbox.selene.core.annotations.delegate;

import org.dockbox.selene.core.delegate.Phase;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The annotation used to mark a type as proxy executor. Proxy methods are still required to be annotated with
 * {@link Proxy.Target}. Any non-annotated method will be ignored.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Proxy {
    /**
     * The target class for the proxy. Can be a interface, abstract, or concrete class.
     *
     * @return The target class
     */
    Class<?> value();

    /**
     * The annotation used to mark a method as proxy executor. When {@link #method()} is unchanged, the name of the
     * annotated method will be used when looking up the target method in the target type. Any parameters not annotated
     * with {@link Instance} will be used during lookup, in the exact order they are defined.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @java.lang.annotation.Target(ElementType.METHOD)
    @interface Target {
        /**
         * Whether or not to overwrite the return value of the target method.
         */
        boolean overwrite() default true;

        /**
         * At which {@link Phase} the proxy executor should be performed.
         *
         * @return The phase
         *
         * @see Phase
         */
        Phase at() default Phase.OVERWRITE;

        /**
         * The priority of the proxy executor. The lower the number, the earlier it will be executed during its
         * dedicated {@link Phase}
         */
        int priority() default 10;

        /**
         * The name of the method to target, without parameter or class qualifiers. Only relevant if the name of the
         * annotated method differs from the target method.
         */
        String method() default "";
    }
}
