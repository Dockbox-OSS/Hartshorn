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

import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.visitors.ExpressionVisitor;

/**
 * An expression representing a postfix operation, which is an operation that applies an operator to
 * an expression after the expression has been evaluated.
 *
 * <p>For example, in the expression <code>a++</code>, the <code>++</code> operator is a postfix
 * operator that increments the value of <code>a</code> by 1 after the expression has been
 * evaluated. Thus, the result of this expression is the original value of <code>a</code>, while the
 * value of
 * <code>a</code> itself is incremented by 1.
 *
 * @see PrefixExpression the counterpart to this expression, which applies an operator before the
 * expression is evaluated.
 *
 * @since 0.4.12
 *
 * @author Guus Lieben
 */
public class PostfixExpression extends Expression {

    private final Token operator;
    private final Expression leftExpression;

    public PostfixExpression(Token operator, Expression leftExpression) {
        super(operator);
        this.operator = operator;
        this.leftExpression = leftExpression;
    }

    /**
     * Returns the operator token of this postfix expression.
     *
     * @return the operator token
     */
    public Token operator() {
        return this.operator;
    }

    /**
     * Returns the expression to which the postfix operator is applied.
     *
     * @return the left expression
     */
    public Expression leftExpression() {
        return this.leftExpression;
    }

    @Override
    public <R> R accept(ExpressionVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
