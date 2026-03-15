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
import org.dockbox.hartshorn.hsl.token.type.TokenType;
import org.dockbox.hartshorn.hsl.visitors.ExpressionVisitor;

/**
 * An expression representing a logical assignment operation, which combines a logical operator with
 * an assignment.
 *
 * <p>For example, in the expression {@code a &&= b}, the {@code &&=} operator is a
 * logical
 * assignment operator that assigns the result of the logical operation {@code a && b} back to
 * {@code a}.
 *
 * @since 0.4.12
 *
 * @author Guus Lieben
 */
public class LogicalAssignExpression extends AssignExpression {

    private final Token operator;

    public LogicalAssignExpression(Token name, Token operator, Expression rightExp) {
        super(name, rightExp);
        this.operator = operator;
    }

    /**
     * Returns the assignment operator token used in this logical assignment expression.
     *
     * @return the assignment operator token
     */
    public Token assignmentOperator() {
        return this.operator;
    }

    /**
     * Returns the logical operator type associated with this logical assignment expression. This is
     * the logical operator without the assignment component. For example, if the assignment
     * operator is {@code &&=}, this method would return the logical operator type for
     * {@code &&}.
     *
     * @return the logical operator type
     */
    public TokenType logicalOperator() {
        return this.operator.type().assignsWith();
    }

    @Override
    public <R> R accept(ExpressionVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
