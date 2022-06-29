package org.dockbox.hartshorn.hsl.condition;

import org.dockbox.hartshorn.component.condition.RequiresCondition;
import org.dockbox.hartshorn.util.reflect.Extends;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Extends(RequiresCondition.class)
@RequiresCondition(condition = ExpressionCondition.class)
public @interface RequiresExpression {
    String value();
}
