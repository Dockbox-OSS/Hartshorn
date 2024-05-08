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
public class ArrayComprehensionExpression extends Expression {

    private final Expression collection;
    private final Expression expression;

    private final Token selector;
    private final Token forToken;
    private final Token inToken;

    private final Token open;
    private final Token close;

    private final Token ifToken;
    private final Expression condition;

    private final Token elseToken;
    private final Expression elseExpression;

    public ArrayComprehensionExpression(Expression collection,
                                        Expression expression,
                                        Token selector,
                                        Token forToken, Token inToken,
                                        Token open, Token close,
                                        Token ifToken, Expression condition,
                                        Token elseToken, Expression elseExpression) {
        super(open);
        this.collection = collection;
        this.expression = expression;
        this.selector = selector;
        this.forToken = forToken;
        this.inToken = inToken;
        this.open = open;
        this.close = close;
        this.ifToken = ifToken;
        this.condition = condition;
        this.elseToken = elseToken;
        this.elseExpression = elseExpression;
    }

    public Expression collection() {
        return this.collection;
    }

    public Expression expression() {
        return this.expression;
    }

    public Token selector() {
        return this.selector;
    }

    public Token forToken() {
        return this.forToken;
    }

    public Token inToken() {
        return this.inToken;
    }

    public Token open() {
        return this.open;
    }

    public Token close() {
        return this.close;
    }

    public Token ifToken() {
        return this.ifToken;
    }

    public Expression condition() {
        return this.condition;
    }

    public Token elseToken() {
        return this.elseToken;
    }

    public Expression elseExpression() {
        return this.elseExpression;
    }

    @Override
    public <R> R accept(ExpressionVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
