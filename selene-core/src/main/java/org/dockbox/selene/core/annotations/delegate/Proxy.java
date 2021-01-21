package org.dockbox.selene.core.annotations.delegate;

import org.dockbox.selene.core.delegate.Phase;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Proxy {
    Class<?> value();

    @Retention(RetentionPolicy.RUNTIME)
    @java.lang.annotation.Target(ElementType.METHOD)
    @interface Target {
        boolean overwrite() default true;
        Phase at() default Phase.OVERWRITE;
        int priority() default 10;
        String method() default "";
    }
}
