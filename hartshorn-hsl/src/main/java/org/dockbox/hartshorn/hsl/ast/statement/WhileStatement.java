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

package org.dockbox.hartshorn.hsl.ast.statement;

import org.dockbox.hartshorn.hsl.ast.expression.Expression;
import org.dockbox.hartshorn.hsl.visitors.StatementVisitor;

/**
 * A while statement, which repeatedly executes a block of code as long as a given condition
 * evaluates to true.
 *
 * <p>For example, the statement below will execute the loop body as long as the
 * value of {@code condition} is true:
 * <pre>{@code
 * while (condition) {
 *    // loop body
 * }
 * }</pre>
 *
 * @since 0.4.12
 *
 * @author Guus Lieben
 */
public class WhileStatement extends BodyStatement {

    private final Expression condition;

    public WhileStatement(Expression condition, BlockStatement loopBody) {
        super(condition, loopBody);
        this.condition = condition;
    }

    /**
     * The condition that is evaluated before each iteration of the loop.
     *
     * @return the loop condition
     */
    public Expression condition() {
        return this.condition;
    }

    @Override
    public <R> R accept(StatementVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
