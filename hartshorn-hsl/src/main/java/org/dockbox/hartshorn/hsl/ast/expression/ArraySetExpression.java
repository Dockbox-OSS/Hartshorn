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

import org.dockbox.hartshorn.hsl.ast.NamedNode;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.visitors.ExpressionVisitor;

/**
 * An expression representing the setting of a value in an array at a specific index. For example,
 * the expression <code>array[0] = value</code> sets the first element of the array named
 * <code>array</code> to the value specified by the <code>value</code> expression.
 *
 * @since 0.4.12
 * 
 * @author Guus Lieben
 */
public class ArraySetExpression extends Expression implements NamedNode {

    private final Token name;
    private final Expression index;
    private final Expression value;

    public ArraySetExpression(Token name, Expression index, Expression value) {
        super(name);
        this.name = name;
        this.index = index;
        this.value = value;
    }

    @Override
    public Token name() {
        return this.name;
    }

    public Expression index() {
        return this.index;
    }

    public Expression value() {
        return this.value;
    }

    @Override
    public <R> R accept(ExpressionVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
