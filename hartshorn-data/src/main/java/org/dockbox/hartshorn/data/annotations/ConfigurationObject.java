package org.dockbox.hartshorn.data.annotations;

import org.dockbox.hartshorn.component.Component;
import org.dockbox.hartshorn.util.reflect.Extends;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Extends(Component.class)
public @interface ConfigurationObject {
    String prefix() default "";
}
