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

package org.dockbox.hartshorn.hsl.objects.virtual;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.dockbox.hartshorn.hsl.ScriptEvaluationError;
import org.dockbox.hartshorn.hsl.interpreter.Interpreter;
import org.dockbox.hartshorn.hsl.interpreter.VariableScope;
import org.dockbox.hartshorn.hsl.objects.InstanceReference;
import org.dockbox.hartshorn.hsl.objects.MethodReference;
import org.dockbox.hartshorn.hsl.objects.access.PropertyAccessVerifier;
import org.dockbox.hartshorn.hsl.objects.access.StandardPropertyAccessVerifier;
import org.dockbox.hartshorn.hsl.runtime.DiagnosticMessage;
import org.dockbox.hartshorn.hsl.runtime.FormattedDiagnostic;
import org.dockbox.hartshorn.hsl.runtime.Phase;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.util.describe.ObjectDescriber;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents an instance of a {@link VirtualClass} inside a script. The instance is identified by
 * its {@link VirtualClass type}. The instance can carry a variety of properties, which are not
 * bound to a specific contract.
 *
 * @since 0.4.12
 *
 * @author Guus Lieben
 */
public class VirtualInstance implements InstanceReference {

    private final VirtualClass virtualClass;
    private final Map<String, Object> fields = new HashMap<>();

    public VirtualInstance(@NonNull VirtualClass virtualClass) {
        this.virtualClass = virtualClass;
    }

    /**
     * The virtual type of this instance.
     *
     * @return the virtual class
     */
    public VirtualClass virtualClass() {
        return this.virtualClass;
    }

    @Override
    public void set(Interpreter interpreter, Token name, Object value, VariableScope fromScope) {
        VirtualProperty field = this.virtualClass.property(name.lexeme());
        if (field == null && !this.virtualClass.isDynamic()) {
            throw ScriptEvaluationError.builder(Phase.INTERPRETING)
                .at(name)
                .message(DiagnosticMessage.UNDEFINED_PROPERTY, name.lexeme(), this.type().name())
                .build();
        }
        if (field != null) {
            FormattedDiagnostic accessError =
                this.accessVerifier().write(name, field, this, fromScope);
            if (accessError != null) {
                throw ScriptEvaluationError.builder(Phase.INTERPRETING)
                    .at(name)
                    .message(accessError)
                    .build();
            }
            if (field.setter() != null) {
                if (field.setter().hasBody()) {
                    field.setter().bind(this).call(name, interpreter, this, List.of(value));
                    return;
                }
            }
            if (field.fieldStatement().isFinal() && this.fields.containsKey(name.lexeme())) {
                throw ScriptEvaluationError.builder(Phase.INTERPRETING)
                    .at(name)
                    .message(DiagnosticMessage.ILLEGAL_FINAL_X_OF_Y_REASSIGNMENT,
                        "property",
                        name.lexeme(),
                        this.type().name())
                    .build();
            }
        }
        this.fields.put(name.lexeme(), value);
    }

    @Override
    public Object get(Interpreter interpreter, Token name, VariableScope fromScope) {
        VirtualProperty field = this.virtualClass.property(name.lexeme());
        if (field != null) {
            FormattedDiagnostic accessError =
                this.accessVerifier().read(name, field, this, fromScope);
            if (accessError != null) {
                throw ScriptEvaluationError.builder(Phase.INTERPRETING)
                    .at(name)
                    .message(accessError)
                    .build();
            }
            if (field.getter() != null) {
                if (field.getter().hasBody()) {
                    return field.getter().bind(this).call(name, interpreter, this, List.of());
                }
            }
            return this.fields.get(name.lexeme());
        }
        else {
            MethodReference method = this.virtualClass.method(name.lexeme());
            if (method != null) return method.bind(this);
        }

        if (this.type().isDynamic()) {
            return this.fields.get(name.lexeme());
        }
        throw ScriptEvaluationError.builder(Phase.INTERPRETING)
            .at(name)
            .message(DiagnosticMessage.UNDEFINED_PROPERTY, name.lexeme(), this.type().name())
            .build();
    }

    /**
     * Gets the property access verifier to use for this instance. This verifier is used to verify
     * whether a property can be accessed or modified.
     *
     * @return the property access verifier
     */
    protected PropertyAccessVerifier accessVerifier() {
        return new StandardPropertyAccessVerifier();
    }

    @Override
    public String toString() {
        return ObjectDescriber.of(this)
            .field("type", this.virtualClass)
            .field("fields", this.fields)
            .describe();
    }

    @NonNull
    @Override
    public VirtualClass type() {
        return this.virtualClass;
    }
}
