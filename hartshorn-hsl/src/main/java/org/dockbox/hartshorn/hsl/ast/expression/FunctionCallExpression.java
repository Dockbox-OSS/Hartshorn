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

import java.util.List;

/**
 * An expression representing a function call, which is an operation that invokes a function with a
 * specified set of arguments. This may be a function on an object, a static function, or a global
 * function.
 *
 * @since 0.4.12
 *
 * @author Guus Lieben
 */
public class FunctionCallExpression extends Expression {

    private final Expression callee;
    private final Token closingParenthesis;
    private final Token openParenthesis;
    private final List<Expression> arguments;

    public FunctionCallExpression(
        Expression callee,
        Token open,
        Token close,
        List<Expression> arguments
    ) {
        super(callee);
        this.callee = callee;
        this.openParenthesis = open;
        this.closingParenthesis = close;
        this.arguments = arguments;
    }

    /**
     * Returns the expression representing the function being called.
     *
     * @return the callee expression
     */
    public Expression callee() {
        return this.callee;
    }

    /**
     * Returns the token representing the opening parenthesis for the arguments of the function
     * call.
     *
     * @return the opening parenthesis token
     */
    public Token openParenthesis() {
        return this.openParenthesis;
    }

    /**
     * Returns the token representing the closing parenthesis for the arguments of the function
     * call.
     *
     * @return the closing parenthesis token
     */
    public Token closingParenthesis() {
        return this.closingParenthesis;
    }

    /**
     * Returns the list of expressions representing the arguments passed to the function call.
     *
     * @return the list of argument expressions
     */
    public List<Expression> arguments() {
        return this.arguments;
    }

    @Override
    public <R> R accept(ExpressionVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
