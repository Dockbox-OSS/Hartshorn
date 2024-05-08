/*
 * Copyright 2019-2024 the original author or authors.
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
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.visitors.StatementVisitor;

import java.util.List;

/**
 * TODO: #1061 Add documentation
 *
 * @since 0.4.12
 *
 * @author Guus Lieben
 */
public class SwitchStatement extends Statement {

    private final Expression expression;
    private final List<SwitchCase> cases;
    private final SwitchCase defaultCase;

    public SwitchStatement(Token switchToken, Expression expression, List<SwitchCase> cases, SwitchCase defaultCase) {
        super(switchToken);
        this.expression = expression;
        this.cases = cases;
        this.defaultCase = defaultCase;
    }

    public Expression expression() {
        return this.expression;
    }

    public List<SwitchCase> cases() {
        return this.cases;
    }

    public SwitchCase defaultCase() {
        return this.defaultCase;
    }

    @Override
    public <R> R accept(StatementVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
