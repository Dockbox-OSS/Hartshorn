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

package org.dockbox.hartshorn.hsl.ast.expression;

import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.visitors.ExpressionVisitor;

import java.util.List;

public class FunctionCallExpression extends Expression {

    private final Expression callee;
    private final Token closingParenthesis;
    private final List<Expression> arguments;

    public FunctionCallExpression(final Expression callee, final Token paren, final List<Expression> arguments) {
        super(callee.line());
        this.callee = callee;
        this.closingParenthesis = paren;
        this.arguments = arguments;
    }

    public Expression callee() {
        return this.callee;
    }

    public Token closingParenthesis() {
        return this.closingParenthesis;
    }

    public List<Expression> arguments() {
        return this.arguments;
    }

    @Override
    public <R> R accept(final ExpressionVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
