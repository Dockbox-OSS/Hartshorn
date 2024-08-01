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

package org.dockbox.hartshorn.hsl.objects.external;

import java.util.Map;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.dockbox.hartshorn.hsl.ScriptEvaluationError;
import org.dockbox.hartshorn.hsl.interpreter.VariableScope;
import org.dockbox.hartshorn.hsl.objects.ClassReference;
import org.dockbox.hartshorn.hsl.objects.ExternalObjectReference;
import org.dockbox.hartshorn.hsl.runtime.ExecutionOptions;
import org.dockbox.hartshorn.hsl.runtime.Phase;
import org.dockbox.hartshorn.hsl.runtime.ScriptRuntime;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.util.introspect.view.FieldView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.option.Option;

/**
 * Represents a single nullable {@link Object} instance that can be accessed from an HSL
 * runtime. This instance can be used to access properties of the instance. The instance
 * needs to be made available to the runtime through {@link ScriptRuntime#global(String, Object)}
 * or {@link ScriptRuntime#global(Map)}, where the instance is made available globally
 * to the runtime.
 *
 * @since 0.4.12
 *
 * @author Guus Lieben
 */
public class ExternalInstance implements ExternalObjectReference {

    private final Object instance;
    private final TypeView<Object> type;

    public <T> ExternalInstance(T instance, TypeView<T> type) {
        if (instance != null && !type.isInstance(instance)) {
            throw new IllegalArgumentException("Instance of type %s is not an instance of %s".formatted(instance.getClass().getName(), type.name()));
        }
        this.instance = instance;
        this.type = (TypeView<Object>) type;
    }

    /**
     * Returns the {@link Object} instance represented by this instance.
     * @return The {@link Object} instance represented by this instance.
     */
    public Object instance() {
        return this.instance;
    }

    @Override
    public void set(Token name, Object value, VariableScope fromScope, ExecutionOptions options) {
        Option<FieldView<Object, ?>> field = this.type.fields().named(name.lexeme());
        if (field.present()) {
            try {
                field.get().set(this.instance(), value);
            }
            catch(Throwable throwable) {
                throw new ScriptEvaluationError(
                        throwable, "Failed to set property %s on external instance of type %s".formatted(name.lexeme(), this.type.name()),
                        Phase.INTERPRETING, name
                );
            }
        }
        else {
            throw this.propertyDoesNotExist(name);
        }
    }

    @Override
    public Object get(Token name, VariableScope fromScope, ExecutionOptions options) {
        Object[] methods = this.type.methods().all().stream()
                .filter(method -> method.name().equals(name.lexeme()))
                .toArray();

        if (methods.length > 1 && !options.permitAmbiguousExternalFunctions()) {
            throw new ScriptEvaluationError(
                    "Ambiguous method call for method %s".formatted(name.lexeme()),
                    Phase.INTERPRETING, name
            );
        }

        if (methods.length > 0) {
            return new ExternalFunction(this.type, name.lexeme());
        }

        Option<FieldView<Object, ?>> field = this.type.fields().named(name.lexeme());
        if (field.present()) {
            try {
                return field.get().get(this.instance());
            }
            catch(Throwable throwable) {
                throw new ScriptEvaluationError(
                        throwable,
                        "Failed to get property %s from external instance of type %s".formatted(name.lexeme(), this.type.name()),
                        Phase.INTERPRETING,
                        name
                );
            }
        }
        else {
            throw this.propertyDoesNotExist(name);
        }
    }

    private ScriptEvaluationError propertyDoesNotExist(Token name) {
        return new ScriptEvaluationError(
                "Property %s does not exist on external instance of type %s".formatted(name.lexeme(), this.type.name()),
                Phase.INTERPRETING, name
        );
    }

    @Override
    public String toString() {
        return String.valueOf(this.instance);
    }

    @NonNull
    @Override
    public ClassReference type() {
        return new ExternalClass<>(this.type);
    }

    @Override
    public @Nullable Object externalObject() {
        return this.instance();
    }
}
