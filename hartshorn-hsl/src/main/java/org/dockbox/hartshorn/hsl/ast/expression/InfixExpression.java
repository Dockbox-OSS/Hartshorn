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

import org.dockbox.hartshorn.hsl.ast.statement.FunctionStatement;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.visitors.ExpressionVisitor;

/**
 * An expression representing an infix operation, which is an operation that is placed between two
 * expressions. An infix expression differs from a binary expression in that it is not necessarily
 * based on a single operator, but rather a function or method that takes two values and produces a
 * result.
 *
 * <p>For example, in the expression {@code value in list}, the {@code in} operator is an
 * infix operator that checks whether the {@code value} is present in the {@code list}.
 *
 * @see FunctionStatement#functionType()
 * 
 * @since 0.4.12
 * 
 * @author Guus Lieben
 */
public class InfixExpression extends Expression {

    private final Expression leftExp;
    private final Token infixOperator;
    private final Expression rightExp;

    public InfixExpression(Expression leftExp, Token infixOperator, Expression rightExp) {
        super(infixOperator);
        this.leftExp = leftExp;
        this.infixOperator = infixOperator;
        this.rightExp = rightExp;
    }

    /**
     * Returns the left expression of the infix operation, which is typically the first of two
     * arguments to the infix function.
     *
     * @return the left expression
     */
    public Expression leftExpression() {
        return this.leftExp;
    }

    /**
     * Returns the infix operator token of the infix operation, which represents the function or
     * method being invoked between the two expressions.
     *
     * @return the infix operator token
     */
    public Token infixOperatorName() {
        return this.infixOperator;
    }

    /**
     * Returns the right expression of the infix operation, which is typically the second of two
     * arguments to the infix function.
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
