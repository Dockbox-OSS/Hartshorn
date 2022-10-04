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

package org.dockbox.hartshorn.hsl.objects.external;

import org.dockbox.hartshorn.hsl.interpreter.VariableScope;
import org.dockbox.hartshorn.hsl.objects.ClassReference;
import org.dockbox.hartshorn.hsl.objects.ExternalObjectReference;
import org.dockbox.hartshorn.hsl.objects.InstanceReference;
import org.dockbox.hartshorn.hsl.runtime.RuntimeError;
import org.dockbox.hartshorn.hsl.runtime.StandardRuntime;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.util.introspect.view.FieldView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

import java.util.Map;

/**
 * Represents a single nullable {@link Object} instance that can be accessed from an HSL
 * runtime. This instance can be used to access properties of the instance. The instance
 * needs to be made available to the runtime through {@link StandardRuntime#global(String, Object)}
 * or {@link StandardRuntime#global(Map)}, where the instance is made available globally
 * to the runtime.
 *
 * @author Guus Lieben
 * @since 22.4
 */
public class ExternalInstance implements InstanceReference, ExternalObjectReference {

    private final Object instance;
    private final TypeView<Object> type;

    public <T> ExternalInstance(final T instance, final TypeView<T> type) {
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
    public void set(final Token name, final Object value, final VariableScope fromScope) {
        this.type.fields().named(name.lexeme())
                .map(field -> (FieldView<Object, Object>) field)
                .present(field -> field.set(this.instance(), value))
                .orThrow(() -> this.propertyDoesNotExist(name));
    }

    @Override
    public Object get(final Token name, final VariableScope fromScope) {
        final boolean isMethod = this.type.methods().all().stream()
                .anyMatch(method -> method.name().equals(name.lexeme()));

        if (isMethod) return new ExternalFunction(this.type, name.lexeme());

        return this.type.fields().named(name.lexeme())
                .flatMap(field -> field.get(this.instance()))
                .orThrow(() -> this.propertyDoesNotExist(name));
    }

    private RuntimeError propertyDoesNotExist(final Token name) {
        return new RuntimeError(name, "Property %s does not exist on external instance of type %s".formatted(name.lexeme(), this.type.name()));
    }

    @Override
    public String toString() {
        return String.valueOf(this.instance);
    }

    @Override
    public ClassReference type() {
        return new ExternalClass<>(this.type);
    }

    @Override
    public Object externalObject() {
        return this.instance();
    }
}
