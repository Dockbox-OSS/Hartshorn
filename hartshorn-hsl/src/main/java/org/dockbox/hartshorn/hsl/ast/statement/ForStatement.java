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

package org.dockbox.hartshorn.hsl.ast.statement;

import org.dockbox.hartshorn.hsl.ast.expression.Expression;
import org.dockbox.hartshorn.hsl.visitors.StatementVisitor;

/**
 * A statement representing a for loop, which is a control flow statement that allows code to be
 * executed repeatedly based on a condition. The for loop consists of an initializer, a condition,
 * an increment statement, and a body that contains the statements to be executed in each
 * iteration.
 *
 * <p>The initializer is typically a variable declaration, the condition is an expression that
 * evaluates to a boolean,
 * and the increment is a statement that modifies the loop variable after each iteration.
 *
 * <p>For example, the statement below represents a for loop that initializes a variable {@code i}
 * to 0, continues looping while {@code i} is less than 10, and increments {@code i} by 1 in each
 * iteration:
 * <pre>{@code
 * for (var i = 0; i < 10; i++) {
 *    // Loop body
 * }
 * }</pre>
 *
 * @since 0.4.12
 *
 * @author Guus Lieben
 */
public class ForStatement extends BodyStatement {

    private final VariableStatement initializer;
    private final Expression condition;
    private final Statement increment;

    public ForStatement(
        VariableStatement initializer,
        Expression condition,
        Statement increment,
        BlockStatement loopBody
    ) {
        super(initializer, loopBody);
        this.initializer = initializer;
        this.condition = condition;
        this.increment = increment;
    }

    /**
     * Returns the initializer variable statement, which represents the variable declaration
     * that initializes the loop variable.
     *
     * @return the initializer variable statement
     */
    public VariableStatement initializer() {
        return this.initializer;
    }

    /**
     * Returns the condition expression, which represents the condition that is evaluated before
     * each iteration of the loop.
     *
     * @return the condition expression
     */
    public Expression condition() {
        return this.condition;
    }

    /**
     * Returns the increment statement, which represents the statement that modifies the loop
     * variable after each iteration.
     *
     * @return the increment statement
     */
    public Statement increment() {
        return this.increment;
    }

    @Override
    public <R> R accept(StatementVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
