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

package org.dockbox.hartshorn.hsl.ast.expression;

import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.visitors.ExpressionVisitor;

public class PrefixExpression extends Expression {

    private final Token prefixFunName;
    private final Expression rightExpression;

    public PrefixExpression(Token prefixFunName, Expression rightExpression) {
        super(prefixFunName);
        this.prefixFunName = prefixFunName;
        this.rightExpression = rightExpression;
    }

    public Token prefixOperatorName() {
        return this.prefixFunName;
    }

    public Expression rightExpression() {
        return this.rightExpression;
    }

    @Override
    public <R> R accept(ExpressionVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
