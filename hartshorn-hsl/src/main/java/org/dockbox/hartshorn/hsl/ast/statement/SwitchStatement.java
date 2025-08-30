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
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.visitors.StatementVisitor;

import java.util.List;

/**
 * A switch statement, which allows for conditional branching based on the value of an expression. The switch
 * statement must have at least one case, which may be a {@link SwitchCase#isDefault() default case}.
 *
 * <p>The switch statement consists of three main components: the switch expression, the list of cases, and an
 * optional default case. The switch expression is evaluated once, and its result is compared against the values
 * of each case in the list.
 *
 * <p>Unlike traditional switch statements in some programming languages, the HSL switch statement does not
 * require a break statement to prevent fall-through behavior. Each case is evaluated independently, and the
 * execution will not continue to the next case. The use of {@link BreakStatement break statements} is still
 * allowed to exit the switch statement early, if desired.
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
