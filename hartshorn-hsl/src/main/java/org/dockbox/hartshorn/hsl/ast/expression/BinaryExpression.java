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
 * An expression representing a binary operation, which consists of two expressions and an operator
 * that defines the operation to be performed on those expressions. The operator can be any valid
 * operator in the HSL language, such as addition, subtraction, multiplication, division, or logical
 * operations.
 *
 * @see org.dockbox.hartshorn.hsl.token.type.ArithmeticTokenType
 * @see org.dockbox.hartshorn.hsl.token.type.ConditionTokenType
 *
 * @since 0.4.12
 *
 * @author Guus Lieben
 */
public class BinaryExpression extends Expression {

    private final Expression leftExp;
    private final Token operator;
    private final Expression rightExp;

    public BinaryExpression(Expression leftExp, Token operator, Expression rightExp) {
        super(operator);
        this.leftExp = leftExp;
        this.operator = operator;
        this.rightExp = rightExp;
    }

    /**
     * Returns the left operand expression of the binary operation.
     *
     * @return the left expression
     */
    public Expression leftExpression() {
        return this.leftExp;
    }

    /**
     * Returns the operator token of the binary operation.
     *
     * @return the operator token
     */
    public Token operator() {
        return this.operator;
    }

    /**
     * Returns the right operand expression of the binary operation.
     *
     * @return the right expression
     */
    public Expression rightExpression() {
        return this.rightExp;
    }

    @Override
    public <R> R accept(ExpressionVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
