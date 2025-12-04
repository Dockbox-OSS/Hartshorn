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

package org.dockbox.hartshorn.hsl.objects.external;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.dockbox.hartshorn.hsl.ScriptEvaluationError;
import org.dockbox.hartshorn.hsl.interpreter.Interpreter;
import org.dockbox.hartshorn.hsl.interpreter.VariableScope;
import org.dockbox.hartshorn.hsl.objects.ClassReference;
import org.dockbox.hartshorn.hsl.objects.ExternalObjectReference;
import org.dockbox.hartshorn.hsl.objects.access.StandardPropertyAccessVerifier;
import org.dockbox.hartshorn.hsl.runtime.DiagnosticMessage;
import org.dockbox.hartshorn.hsl.runtime.Phase;
import org.dockbox.hartshorn.hsl.runtime.ScriptRuntime;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.util.describe.ObjectDescriber;
import org.dockbox.hartshorn.util.introspect.view.FieldView;
import org.dockbox.hartshorn.util.option.Option;
import org.dockbox.hartshorn.util.types.TypeUtils;

import java.util.Map;

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
    private final ExternalClass<?> type;

    public <T> ExternalInstance(T instance, ExternalClass<T> type) {
        if (instance != null && !type.type().isInstance(instance)) {
            // TODO: Migrate to ScriptEvaluationError
            throw new IllegalArgumentException("Instance of type %s is not an instance of %s".formatted(instance.getClass().getName(), type.name()));
        }
        this.instance = instance;
        this.type = type;
    }

    /**
     * Returns the {@link Object} instance represented by this instance.
     * @return The {@link Object} instance represented by this instance.
     */
    public Object instance() {
        return this.instance;
    }

    @Override
    public void set(final Interpreter interpreter, final Token name, final Object value, VariableScope fromScope) {
        Option<FieldView<?, ?>> field = this.type.type().fields()
                .named(name.lexeme())
                .map(f -> TypeUtils.unchecked(f, FieldView.class));
        if (field.present()) {
            try {
                field.get().set(this.instance(), value);
            }
            catch(Throwable throwable) {
                throw ScriptEvaluationError.builder(Phase.INTERPRETING)
                    .at(name)
                    .message(DiagnosticMessage.PROPERTY_ACCESS_FAILURE,
                        StandardPropertyAccessVerifier.WRITE_ACTION,
                        name.lexeme(),
                        this.type().name(),
                        throwable.getMessage())
                    .cause(throwable)
                    .build();
            }
        }
        else {
            throw this.propertyDoesNotExist(name);
        }
    }

    @Override
    public Object get(final Interpreter interpreter, final Token name, VariableScope fromScope) {
        Object[] methods = this.type.type().methods().all().stream()
                .filter(method -> method.name().equals(name.lexeme()))
                .toArray();

        if (methods.length > 1 && !interpreter.executionOptions().permitAmbiguousExternalFunctions()) {
            throw ScriptEvaluationError.builder(Phase.INTERPRETING)
                .at(name)
                .message(DiagnosticMessage.AMBIGUOUS_FUNCTION_CALL, name.lexeme())
                .build();
        }

        if (methods.length > 0) {
            return new ExternalFunction(this.type, name.lexeme());
        }

        Option<FieldView<?, ?>> field = this.type.type().fields()
                .named(name.lexeme())
                .map(f -> TypeUtils.unchecked(f, FieldView.class));
        if (field.present()) {
            try {
                return field.get().get(this.instance());
            }
            catch(Throwable throwable) {
                throw ScriptEvaluationError.builder(Phase.INTERPRETING)
                    .at(name)
                    .message(DiagnosticMessage.PROPERTY_ACCESS_FAILURE,
                        StandardPropertyAccessVerifier.READ_ACTION,
                        name.lexeme(),
                        this.type().name(),
                        throwable.getMessage()
                    )
                    .cause(throwable)
                    .build();
            }
        }
        else {
            throw this.propertyDoesNotExist(name);
        }
    }

    private ScriptEvaluationError propertyDoesNotExist(Token name) {
        throw ScriptEvaluationError.builder(Phase.INTERPRETING)
            .at(name)
            .message(DiagnosticMessage.UNDEFINED_PROPERTY, name.lexeme(), this.type.name())
            .build();
    }

    @Override
    public String toString() {
        return ObjectDescriber.of(this)
                .field("instance", this.instance)
                .describe();
    }

    @NonNull
    @Override
    public ClassReference type() {
        return this.type;
    }

    @Override
    public @Nullable Object externalObject() {
        return this.instance();
    }
}
