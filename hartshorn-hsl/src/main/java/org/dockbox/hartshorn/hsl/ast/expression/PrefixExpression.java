/*
 * Copyright 2019-2026 the original author or authors.
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

import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.visitors.ExpressionVisitor;

/**
 * An expression representing a prefix operation, which is an operation that applies an operator to
 * an expression before the expression has been evaluated.
 *
 * <p>For example, in the expression {@code ++a}, the {@code ++} operator is a prefix
 * operator
 * that increments the value of {@code a} by 1 before the expression is evaluated. Thus, the
 * result of this expression is the incremented value of {@code a}, and the value of
 * {@code a} itself is also incremented by 1.
 *
 * @see PostfixExpression the counterpart to this expression, which applies an operator after the
 * expression is evaluated.
 *
 * @since 0.4.12
 *
 * @author Guus Lieben
 */
public class PrefixExpression extends Expression {

    private final Token prefixFunName;
    private final Expression rightExpression;

    public PrefixExpression(Token prefixFunName, Expression rightExpression) {
        super(prefixFunName);
        this.prefixFunName = prefixFunName;
        this.rightExpression = rightExpression;
    }

    /**
     * Returns the token representing the prefix operator applied to the expression.
     *
     * @return the prefix operator token
     */
    public Token prefixOperatorName() {
        return this.prefixFunName;
    }

    /**
     * Returns the expression to which the prefix operator is applied.
     *
     * @return the right operand expression
     */
    public Expression rightExpression() {
        return this.rightExpression;
    }

    @Override
    public <R> R accept(ExpressionVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
