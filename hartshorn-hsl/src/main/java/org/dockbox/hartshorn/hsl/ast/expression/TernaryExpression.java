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

package org.dockbox.hartshorn.hsl.ast.expression;

import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.visitors.ExpressionVisitor;

/**
 * TODO: #1061 Add documentation
 *
 * @since 0.4.12
 *
 * @author Guus Lieben
 */
public class TernaryExpression extends Expression {

    private final Expression condition;
    private final Token ternaryOp;
    private final Expression firstExp;
    private final Token colon;
    private final Expression secondExp;

    public TernaryExpression(Expression condition, Token ternaryOp,
                             Expression firstExp, Token colon,
                             Expression secondExp) {
        super(condition);
        this.condition = condition;
        this.ternaryOp = ternaryOp;
        this.firstExp = firstExp;
        this.colon = colon;
        this.secondExp = secondExp;
    }

    public Expression condition() {
        return this.condition;
    }

    public Token ternaryOp() {
        return this.ternaryOp;
    }

    public Expression firstExpression() {
        return this.firstExp;
    }

    public Token colon() {
        return this.colon;
    }

    public Expression secondExpression() {
        return this.secondExp;
    }

    @Override
    public <R> R accept(ExpressionVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
