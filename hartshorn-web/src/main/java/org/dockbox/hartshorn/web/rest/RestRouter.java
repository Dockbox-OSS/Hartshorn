package org.dockbox.hartshorn.web.rest;

import org.dockbox.hartshorn.inject.annotations.Component;
import org.dockbox.hartshorn.util.introspect.annotations.Extends;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Extends(Component.class)
public @interface RestRouter {

    /**
     * @see Component#permitProxying()
     * @return whether proxying is permitted
     */
    boolean permitProxying() default true;

    /**
     * @see Component#permitProcessing()
     * @return whether processing is permitted
     */
    boolean permitProcessing() default true;
}
