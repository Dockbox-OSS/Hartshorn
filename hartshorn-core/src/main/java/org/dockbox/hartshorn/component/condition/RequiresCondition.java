package org.dockbox.hartshorn.component.condition;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresCondition {
    Class<? extends Condition> value();

    boolean failOnNoMatch() default false;
}
