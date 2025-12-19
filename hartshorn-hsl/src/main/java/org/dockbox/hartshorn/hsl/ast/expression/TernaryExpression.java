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
 * An expression representing a ternary operation, which is a shorthand for an if-else statement. It
 * consists of a condition, followed by a question mark, then an expression to evaluate if the
 * condition is true, followed by a colon, and finally an expression to evaluate if the condition is
 * false.
 *
 * <p>For example, in the expression <code>condition ? trueExpression : falseExpression</code>, the
 * <code>condition</code> is evaluated first. If it evaluates to true, the
 * <code>trueExpression</code> is
 * evaluated and returned; if it evaluates to false, the <code>falseExpression</code> is evaluated
 * and returned instead.
 *
 * @since 0.4.12
 *
 * @author Guus Lieben
 */
public class TernaryExpression extends Expression {

    private final Expression condition;
    private final Token ternaryOp;
    private final Expression firstExp;
    private final Token colon;
    private final Expression secondExp;

    public TernaryExpression(
        Expression condition, Token ternaryOp,
        Expression firstExp, Token colon,
        Expression secondExp
    ) {
        super(condition);
        this.condition = condition;
        this.ternaryOp = ternaryOp;
        this.firstExp = firstExp;
        this.colon = colon;
        this.secondExp = secondExp;
    }

    /**
     * Returns the condition expression that is evaluated to determine which of the two expressions
     * to evaluate and return.
     *
     * @return the condition expression
     */
    public Expression condition() {
        return this.condition;
    }

    /**
     * Returns the token representing the ternary operator (question mark).
     *
     * @return the ternary operator token
     */
    public Token ternaryOp() {
        return this.ternaryOp;
    }

    /**
     * Returns the first expression that is evaluated if the condition is true.
     *
     * @return the first expression
     */
    public Expression firstExpression() {
        return this.firstExp;
    }

    /**
     * Returns the token representing the colon that separates the two expressions.
     *
     * @return the colon token
     */
    public Token colon() {
        return this.colon;
    }

    /**
     * Returns the second expression that is evaluated if the condition is false.
     *
     * @return the second expression
     */
    public Expression secondExpression() {
        return this.secondExp;
    }

    @Override
    public <R> R accept(ExpressionVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
