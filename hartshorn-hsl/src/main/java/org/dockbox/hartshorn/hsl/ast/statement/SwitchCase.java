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

import org.dockbox.hartshorn.hsl.ast.expression.LiteralExpression;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.visitors.StatementVisitor;

/**
 * A switch case statement, representing a single case within a switch construct. This statement
 * holds a body, which is the statement to be executed if the case matches, and an expression, which
 * is the value to match against the switch expression. If the case is a default case, the
 * expression will be {@code null}.
 *
 * @since 0.4.12
 *
 * @author Guus Lieben
 */
public class SwitchCase extends Statement {

    private final Statement body;
    private final LiteralExpression expression;
    private final boolean isDefault;

    public SwitchCase(
        Token caseToken,
        Statement body,
        LiteralExpression expression,
        boolean isDefault
    ) {
        super(caseToken);
        this.body = body;
        this.expression = expression;
        this.isDefault = isDefault;
    }

    /**
     * Returns the body of this switch case, which is the statement to be executed if the case
     * matches.
     *
     * @return the body statement
     */
    public Statement body() {
        return this.body;
    }

    /**
     * Returns the expression of this switch case, which is the value to match against the switch
     * expression. If this is a default case, the expression will be {@code null}.
     *
     * @return the case expression, or {@code null} if this is a default case
     */
    public LiteralExpression expression() {
        return this.expression;
    }

    /**
     * Indicates whether this switch case is the default case.
     *
     * @return {@code true} if this is the default case, {@code false} otherwise
     */
    public boolean isDefault() {
        return this.isDefault;
    }

    @Override
    public <R> R accept(StatementVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
