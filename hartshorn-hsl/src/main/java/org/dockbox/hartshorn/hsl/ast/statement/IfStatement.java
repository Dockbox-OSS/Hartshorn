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
 * A statement representing an if-else conditional structure, which executes a block of code
 * based on the evaluation of a condition. If the condition evaluates to true, the "then
 * branch" is executed; otherwise, the "else branch" is executed if it is provided.
 *
 * <p>The else branch is optional and may be null, indicating that no action should be taken
 * if the condition is false.
 *
 * <p>For example, the statement below represents an if-else structure that checks if a variable
 * `x` is greater than 10:
 * <pre>{@code
 * if (x > 10) {
 *    // Then branch
 * } else {
 *    // Else branch
 * }
 * }</pre>
 *
 * @since 0.4.12
 *
 * @author Guus Lieben
 */
public class IfStatement extends Statement {

    private final Expression condition;
    private final BlockStatement thenBranch;
    private final BlockStatement elseBranch;

    public IfStatement(Expression condition, BlockStatement thenBranch, BlockStatement elseBranch) {
        super(condition);
        this.condition = condition;
        this.thenBranch = thenBranch;
        this.elseBranch = elseBranch;
    }

    public Expression condition() {
        return this.condition;
    }

    public BlockStatement thenBranch() {
        return this.thenBranch;
    }

    public BlockStatement elseBranch() {
        return this.elseBranch;
    }

    @Override
    public <R> R accept(StatementVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
