/*
 * Copyright 2019-2022 the original author or authors.
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

public class ForStatement extends BodyStatement {

    private final VariableStatement initializer;
    private final Expression condition;
    private final Statement increment;

    public ForStatement(final VariableStatement initializer, final Expression condition, final Statement increment, final BlockStatement loopBody) {
        super(initializer, loopBody);
        this.initializer = initializer;
        this.condition = condition;
        this.increment = increment;
    }

    public VariableStatement initializer() {
        return this.initializer;
    }

    public Expression condition() {
        return this.condition;
    }

    public Statement increment() {
        return this.increment;
    }

    @Override
    public <R> R accept(final StatementVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
