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

package org.dockbox.hartshorn.hsl.objects.virtual;

import org.dockbox.hartshorn.hsl.interpreter.Interpreter;
import org.dockbox.hartshorn.hsl.interpreter.VariableScope;
import org.dockbox.hartshorn.hsl.objects.InstanceReference;
import org.dockbox.hartshorn.hsl.objects.MethodReference;
import org.dockbox.hartshorn.hsl.objects.access.PropertyAccessVerifier;
import org.dockbox.hartshorn.hsl.objects.access.StandardPropertyAccessVerifier;
import org.dockbox.hartshorn.hsl.runtime.DiagnosticMessage;
import org.dockbox.hartshorn.hsl.runtime.FormattedDiagnostic;
import org.dockbox.hartshorn.hsl.runtime.RuntimeError;
import org.dockbox.hartshorn.hsl.runtime.Yield;
import org.dockbox.hartshorn.hsl.token.Token;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents an instance of a {@link VirtualClass} inside a script. The instance is
 * identified by its {@link VirtualClass type}. The instance can carry a variety of
 * properties, which are not bound to a specific contract.
 *
 * @author Guus Lieben
 * @since 22.4
 */
public class VirtualInstance implements InstanceReference {

    private final VirtualClass virtualClass;
    private final Map<String, Object> fields = new HashMap<>();

    public VirtualInstance(final VirtualClass virtualClass) {
        this.virtualClass = virtualClass;
    }

    @Override
    public void set(final Interpreter interpreter, final Token name, final Object value, final VariableScope fromScope) {
        final VirtualProperty field = this.virtualClass.property(name.lexeme());
        if (field == null && !this.virtualClass.isDynamic()) {
            throw new RuntimeError(name, DiagnosticMessage.UNDEFINED_PROPERTY, name.lexeme(), this.virtualClass.name());
        }
        if (field != null) {
            final FormattedDiagnostic accessError = this.accessVerifier().write(name, field, this, fromScope);
            if (accessError != null) {
                throw new RuntimeError(name, accessError.errorIndex(), accessError.arguments());
            }
            if (field.setter() != null) {
                if (field.setter().hasBody()) {
                    try {
                        final Object call = field.setter().bind(this).call(name, interpreter, this, List.of(value));
                        if (call != null) {
                            throw new RuntimeError(field.setter().modifier(), DiagnosticMessage.ILLEGAL_SETTER_RETURN, name.lexeme());
                        }
                        throw new RuntimeError(field.setter().modifier(), DiagnosticMessage.ILLEGAL_SETTER_NO_YIELD, name.lexeme());
                    }
                    catch (final Yield yield) {
                        final Object result = yield.value();
                        this.fields.put(name.lexeme(), result);
                    }
                    return;
                }
            }
            if (field.fieldStatement().isFinal() && this.fields.containsKey(name.lexeme())) {
                throw new RuntimeError(name, DiagnosticMessage.ILLEGAL_FINAL_PROPERTY_REASSIGNMENT, name.lexeme(), this.type().name());
            }
        }
        this.fields.put(name.lexeme(), value);
    }

    @Override
    public Object get(final Interpreter interpreter, final Token name, final VariableScope fromScope) {
        final VirtualProperty field = this.virtualClass.property(name.lexeme());
        if (field != null) {
            final FormattedDiagnostic accessError = this.accessVerifier().read(name, field, this, fromScope);
            if (accessError != null) {
                throw new RuntimeError(name, accessError.errorIndex(), accessError.arguments());
            }
            if (field.getter() != null) {
                if (field.getter().hasBody()) {
                    return field.getter().bind(this).call(name, interpreter, this, List.of());
                }
            }
            return this.fields.get(name.lexeme());
        }
        else {
            final MethodReference method = this.virtualClass.method(name.lexeme());
            if (method != null) return method.bind(this);
        }

        if (this.type().isDynamic()) {
            return this.fields.get(name.lexeme());
        }

        throw new RuntimeError(name, DiagnosticMessage.UNDEFINED_PROPERTY, name.lexeme(), this.virtualClass.name());
    }

    protected PropertyAccessVerifier accessVerifier() {
        return new StandardPropertyAccessVerifier();
    }

    @Override
    public String toString() {
        return this.virtualClass.name() + " instance";
    }

    @Override
    public VirtualClass type() {
        return this.virtualClass;
    }
}
