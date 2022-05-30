package org.dockbox.hartshorn.component.condition;


import org.dockbox.hartshorn.util.reflect.Extends;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Extends(RequiresCondition.class)
@RequiresCondition(ClassCondition.class)
public @interface RequiresClass {

    String name();

    boolean failOnNoMatch() default false;
}
