/*
 * Copyright 2019-2023 the original author or authors.
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

import org.dockbox.hartshorn.hsl.ast.expression.LiteralExpression;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.visitors.StatementVisitor;

public class SwitchCase extends Statement {

    private final Statement body;
    private final LiteralExpression expression;
    private final boolean isDefault;

    public SwitchCase(Token caseToken, Statement body, LiteralExpression expression, boolean isDefault) {
        super(caseToken);
        this.body = body;
        this.expression = expression;
        this.isDefault = isDefault;
    }

    public Statement body() {
        return this.body;
    }

    public LiteralExpression expression() {
        return this.expression;
    }

    public boolean isDefault() {
        return this.isDefault;
    }

    @Override
    public <R> R accept(StatementVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
