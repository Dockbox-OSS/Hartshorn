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

package org.dockbox.hartshorn.hsl.callable.external;

import org.dockbox.hartshorn.hsl.callable.PropertyContainer;
import org.dockbox.hartshorn.hsl.runtime.RuntimeError;
import org.dockbox.hartshorn.hsl.runtime.StandardRuntime;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.util.reflect.TypeContext;

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
public class ExternalInstance implements PropertyContainer {

    private final Object instance;
    private final TypeContext<Object> type;

    public ExternalInstance(final Object instance) {
        this.instance = instance;
        this.type = TypeContext.of(instance);
    }

    /**
     * Returns the {@link Object} instance represented by this instance.
     * @return The {@link Object} instance represented by this instance.
     */
    public Object instance() {
        return this.instance;
    }

    @Override
    public void set(final Token name, final Object value) {
        this.type.field(name.lexeme()).present(field -> {
            field.set(this.instance(), value);
        }).orThrow(() -> this.propertyDoesNotExist(name));
    }

    @Override
    public Object get(final Token name) {
        final boolean isMethod = this.type.methods().stream()
                .anyMatch(method -> method.name().equals(name.lexeme()));
        if (isMethod) return new ExternalFunction(this.instance, name.lexeme());

        return this.type.field(name.lexeme())
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
}
