/*
 * Copyright 2019-2025 the original author or authors.
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

package org.dockbox.hartshorn.hsl.ast.expression;

import org.dockbox.hartshorn.hsl.visitors.ExpressionVisitor;

/**
 * An expression representing a grouping of another expression. A grouping is used to indicate
 * that the expression should be evaluated as a single unit, allowing for precedence to be
 * applied correctly in complex expressions.
 *
 * <p>For example, in the expression <code>(a + b) * c</code>, the grouping ensures that
 * <code>a + b</code> is evaluated before multiplying by <code>c</code>. This is particularly
 * useful in mathematical expressions and logical operations where operator precedence might
 * otherwise lead to unexpected results.
 *
 * @since 0.4.12
 *
 * @author Guus Lieben
 */
public class GroupingExpression extends Expression {

    private final Expression expression;

    public GroupingExpression(Expression expression) {
        super(expression);
        this.expression = expression;
    }

    public Expression expression() {
        return this.expression;
    }

    @Override
    public <R> R accept(ExpressionVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
