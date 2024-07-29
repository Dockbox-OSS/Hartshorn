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

package org.dockbox.hartshorn.hsl.objects.virtual;

import java.util.HashMap;
import java.util.Map;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.dockbox.hartshorn.hsl.ScriptEvaluationError;
import org.dockbox.hartshorn.hsl.ast.statement.FieldStatement;
import org.dockbox.hartshorn.hsl.interpreter.VariableScope;
import org.dockbox.hartshorn.hsl.objects.InstanceReference;
import org.dockbox.hartshorn.hsl.objects.MethodReference;
import org.dockbox.hartshorn.hsl.objects.access.PropertyAccessVerifier;
import org.dockbox.hartshorn.hsl.objects.access.StandardPropertyAccessVerifier;
import org.dockbox.hartshorn.hsl.runtime.ExecutionOptions;
import org.dockbox.hartshorn.hsl.runtime.Phase;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.util.ObjectDescriber;

/**
 * Represents an instance of a {@link VirtualClass} inside a script. The instance is
 * identified by its {@link VirtualClass type}. The instance can carry a variety of
 * properties, which are not bound to a specific contract.
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

    @Override
    public void set(Token name, Object value, VariableScope fromScope, ExecutionOptions options) {
        FieldStatement field = this.virtualClass.field(name.lexeme());
        if (field == null && !this.virtualClass.isDynamic()) {
            throw new ScriptEvaluationError("Undefined property '" + name.lexeme() + "'.", Phase.INTERPRETING, name);
        }
        if (field != null && field.isFinal() && this.fields.containsKey(name.lexeme())) {
            throw new ScriptEvaluationError("Cannot reassign final property '" + name.lexeme() + "'.", Phase.INTERPRETING, name);
        }
        if (field != null) {
            this.checkScopeCanAccess(name, field, fromScope);
        }
        this.fields.put(name.lexeme(), value);
    }

    @Override
    public Object get(Token name, VariableScope fromScope, ExecutionOptions options) {
        FieldStatement field = this.virtualClass.field(name.lexeme());
        if (this.virtualClass.isDynamic() || (field != null && this.fields.containsKey(name.lexeme()))) {
            if (field != null) {
                this.checkScopeCanAccess(name, field, fromScope);
            }
            return this.fields.get(name.lexeme());
        }
        MethodReference method = this.virtualClass.method(name.lexeme());
        if (method != null) {
            return method.bind(this);
        }
        throw new ScriptEvaluationError("Undefined property '" + name.lexeme() + "'.", Phase.INTERPRETING, name);
    }

    private void checkScopeCanAccess(Token at, FieldStatement field, VariableScope fromScope) {
        PropertyAccessVerifier verifier = this.accessVerifier();
        if (!verifier.verify(at, field, this, fromScope)) {
            throw new ScriptEvaluationError("Cannot access property '" + field.name().lexeme() + "' outside of its class scope.", Phase.INTERPRETING, at);
        }
    }

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
