/*
 * Copyright 2019-2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dockbox.hartshorn.hsl.condition;

import org.dockbox.hartshorn.inject.condition.RequiresCondition;
import org.dockbox.hartshorn.util.introspect.annotations.Extends;

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
 * @since 0.4.12
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Extends(RequiresCondition.class)
@RequiresCondition(condition = ExpressionCondition.class)
public @interface RequiresExpression {

    /**
     * The expression to evaluate. Context of the expression depends on the {@link ExpressionCondition}
     * and any additional provided context.
     *
     * @return The expression to evaluate.
     */
    String value();
}
