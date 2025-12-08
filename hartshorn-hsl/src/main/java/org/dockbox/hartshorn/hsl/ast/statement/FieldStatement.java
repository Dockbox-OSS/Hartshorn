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

import org.dockbox.hartshorn.hsl.ScriptEvaluationError;
import org.dockbox.hartshorn.hsl.ast.NamedNode;
import org.dockbox.hartshorn.hsl.ast.expression.Expression;
import org.dockbox.hartshorn.hsl.runtime.DiagnosticMessage;
import org.dockbox.hartshorn.hsl.runtime.Phase;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.visitors.StatementVisitor;

/**
 * A field statement, which represents the declaration of a field within a class. A field may have
 * an optional modifier (e.g., public, private) and an optional initializer expression.
 *
 * <p>For example, the following field declaration includes a modifier and an initializer:
 * <pre>{@code
 * public final myField = 42;
 * }</pre>
 *
 * <p>In this example, <code>public</code> is the modifier, <code>myField</code> is the name of
 * the field, and <code>42</code> is the initializer expression that sets the initial value of the
 * field. Additionally, the field is marked as <code>final</code>, indicating that its value cannot
 * be changed after initialization.
 *
 * @since 0.4.12
 *
 * @author Guus Lieben
 */
public class FieldStatement extends FinalizableStatement implements MemberStatement, NamedNode {

    private final Token modifier;
    private final Token name;
    private final Expression initializer;

    private FieldGetStatement getter;
    private FieldSetStatement setter;

    public FieldStatement(Token modifier, Token name, Expression initializer, boolean isFinal) {
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
    public <R> R accept(StatementVisitor<R> visitor) {
        return visitor.visit(this);
    }

    public void withGetter(FieldGetStatement statement) {
        if (statement.field() != this) {
            throw ScriptEvaluationError.builder(Phase.PARSING)
                .message(DiagnosticMessage.FIELD_MEMBER_X_NOT_FOR_FIELD_Y,
                    "getter",
                    this.name.lexeme())
                .at(statement.modifier())
                .build();
        }
        if (this.getter != null) {
            throw ScriptEvaluationError.builder(Phase.PARSING)
                .message(DiagnosticMessage.DUPLICATE_FIELD_MEMBER_X_FOR_FIELD_Y,
                    "getter",
                    this.name.lexeme())
                .at(statement.modifier())
                .build();
        }
        this.getter = statement;
    }

    public void withSetter(FieldSetStatement statement) {
        if (statement.field() != this) {
            throw ScriptEvaluationError.builder(Phase.PARSING)
                .message(DiagnosticMessage.FIELD_MEMBER_X_NOT_FOR_FIELD_Y,
                    "setter",
                    this.name.lexeme())
                .at(statement.modifier())
                .build();
        }
        if (this.setter != null) {
            throw ScriptEvaluationError.builder(Phase.PARSING)
                .message(DiagnosticMessage.DUPLICATE_FIELD_MEMBER_X_FOR_FIELD_Y,
                    "setter",
                    this.name.lexeme())
                .at(statement.modifier())
                .build();
        }
        this.setter = statement;
    }
}
