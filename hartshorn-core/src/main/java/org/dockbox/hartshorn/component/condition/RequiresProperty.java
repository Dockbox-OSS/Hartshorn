package org.dockbox.hartshorn.component.condition;

import org.dockbox.hartshorn.util.reflect.Extends;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Extends(RequiresCondition.class)
@RequiresCondition(PropertyCondition.class)
public @interface RequiresProperty {
    String name();

    String withValue() default "";

    boolean matchIfMissing() default false;

    boolean failOnNoMatch() default false;
}
