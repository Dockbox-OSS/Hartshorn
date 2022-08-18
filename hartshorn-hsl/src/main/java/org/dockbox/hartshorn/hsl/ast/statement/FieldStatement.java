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

package org.dockbox.hartshorn.hsl.ast.statement;

import org.dockbox.hartshorn.hsl.ast.expression.Expression;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.visitors.StatementVisitor;

public class FieldStatement extends FinalizableStatement implements MemberStatement {

    private final Token modifier;
    private final Token name;
    private final Expression initializer;

    private FieldGetStatement getter;
    private FieldSetStatement setter;

    public FieldStatement(final Token modifier, final Token name, final Expression initializer, final boolean isFinal) {
        super(modifier != null ? modifier : name, isFinal);
        this.modifier = modifier;
        this.name = name;
        this.initializer = initializer;
    }

    public Expression initializer() {
        return this.initializer;
    }

    public FieldGetStatement getter() {
        return this.getter;
    }

    public FieldSetStatement setter() {
        return this.setter;
    }

    @Override
    public Token name() {
        return this.name;
    }

    @Override
    public Token modifier() {
        return this.modifier;
    }

    @Override
    public <R> R accept(final StatementVisitor<R> visitor) {
        return visitor.visit(this);
    }

    public void withGetter(final FieldGetStatement statement) {
        if (statement.field() != this) {
            throw new IllegalArgumentException("Getter is not for field '%s'".formatted(this.name.lexeme()));
        }
        if (this.getter != null) {
            throw new IllegalStateException("Duplicate getter for field '%s'".formatted(this.name.lexeme()));
        }
        this.getter = statement;
    }

    public void withSetter(final FieldSetStatement statement) {
        if (statement.field() != this) {
            throw new IllegalArgumentException("Setter is not for field '%s'".formatted(this.name.lexeme()));
        }
        if (this.setter != null) {
            throw new IllegalArgumentException("Duplicate setter for field '%s'".formatted(this.name.lexeme()));
        }
        this.setter = statement;
    }
}
