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

package org.dockbox.hartshorn.hsl.ast.expression;

import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.visitors.ExpressionVisitor;

/**
 * An expression representing an array comprehension, which allows for the creation of a new array
 * by performing a transformation on an existing collection, with optional filtering.
 *
 * <p>In its most basic form, an array comprehension consists of a collection, and a transformation
 * expression. For example:
 * <pre>{@code
 * [x * 2 for x in collection]
 * }</pre>
 *
 * <p>It can also include a filtering condition, allowing for the exclusion of certain elements:
 * <pre>{@code
 * [x * 2 for x in collection if x > 10]
 * }</pre>
 *
 * <p>Additionally, a default expression can be provided, which is used when the condition is not
 * met:
 * <pre>{@code
 * [x * 2 for x in collection if x > 10 else 0]
 * }</pre>
 *
 * <p>The transformation, condition, and default expression can all be arbitrary expressions, with
 * access to the current element of the collection via the provided selector token.
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

    public ArrayComprehensionExpression(
        Expression collection,
        Expression expression,
        Token selector,
        Token forToken, Token inToken,
        Token open, Token close,
        Token ifToken, Expression condition,
        Token elseToken, Expression elseExpression
    ) {
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

    /**
     * Returns the collection expression over which the comprehension iterates.
     *
     * @return the collection expression
     */
    public Expression collection() {
        return this.collection;
    }

    /**
     * Returns the main expression that defines how each element in the collection is transformed.
     * This may be conditionally executed based on the presence of a filtering condition.
     *
     * @return the transformation expression
     */
    public Expression expression() {
        return this.expression;
    }

    /**
     * Returns the selector token representing the variable used to reference the current element
     * in the collection during iteration.
     *
     * @return the selector token
     */
    public Token selector() {
        return this.selector;
    }

    /**
     * Returns the 'for' token used in the comprehension syntax.
     *
     * @return the 'for' token
     */
    public Token forToken() {
        return this.forToken;
    }

    /**
     * Returns the 'in' token used in the comprehension syntax.
     *
     * @return the 'in' token
     */
    public Token inToken() {
        return this.inToken;
    }

    /**
     * Returns the opening token of the comprehension (typically a '[').
     *
     * @return the opening token
     */
    public Token open() {
        return this.open;
    }

    /**
     * Returns the closing token of the comprehension (typically a ']').
     *
     * @return the closing token
     */
    public Token close() {
        return this.close;
    }

    /**
     * Returns the 'if' token used in the comprehension syntax, if a filtering condition is present.
     * If no condition is specified, this may be null.
     *
     * @return the 'if' token
     */
    public Token ifToken() {
        return this.ifToken;
    }

    /**
     * Returns the filtering condition expression that determines whether the transformation
     * expression is applied to each element. If no condition is specified, this may be null.
     *
     * @return the filtering condition expression
     */
    public Expression condition() {
        return this.condition;
    }

    /**
     * Returns the 'else' token used in the comprehension syntax, if a default expression is
     * provided. If no default is specified, this may be null.
     *
     * @return the 'else' token
     */
    public Token elseToken() {
        return this.elseToken;
    }

    /**
     * Returns the default expression that is used when the filtering condition is not met.
     * If no default is specified, this may be null.
     *
     * @return the default expression
     */
    public Expression elseExpression() {
        return this.elseExpression;
    }

    @Override
    public <R> R accept(ExpressionVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
