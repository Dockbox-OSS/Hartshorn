package org.dockbox.hartshorn.hsl.condition;

import org.dockbox.hartshorn.component.condition.RequiresCondition;
import org.dockbox.hartshorn.util.reflect.Extends;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Conditional annotation used to define custom conditions using HSL expressions. This uses the
 * {@link ExpressionCondition} to evaluate conditions.
 *
 * <pre>{@code
 * @RequiresExpression("1 + 2 == 3")
 * void performAction() { ... }
 * }</pre>
 *
 * @author Guus Lieben
 * @since 22.4
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Extends(RequiresCondition.class)
@RequiresCondition(condition = ExpressionCondition.class)
public @interface RequiresExpression {

    /**
     * The expression to evaluate. Context of the expression depends on the {@link ExpressionCondition}
     * and any additional provided context.
     */
    String value();
}
