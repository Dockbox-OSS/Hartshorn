package org.dockbox.selene.core.annotations.delegate;

import org.dockbox.selene.core.delegate.DelegateTarget;

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
        boolean overwriteResult() default true;
        DelegateTarget target() default DelegateTarget.OVERWRITE;
    }
}
