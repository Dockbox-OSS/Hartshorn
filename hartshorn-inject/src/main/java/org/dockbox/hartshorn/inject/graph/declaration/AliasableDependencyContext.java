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

package org.dockbox.hartshorn.inject.graph.declaration;

import org.dockbox.hartshorn.inject.ComponentKey;
import org.dockbox.hartshorn.inject.QualifierKey;

import java.util.Set;

/**
 * A {@link DependencyContext} that supports aliases. Aliases are additional keys that can be used
 * to reference the same binding. This is useful for example when a binding is defined in multiple
 * modules, and you want to reference the same binding using different keys.
 *
 * @param <T> The type of the component that this context is for.
 *
 * @author Guus Lieben
 * @since 0.7.0
 */
public interface AliasableDependencyContext<T> extends DependencyContext<T> {

    /**
     * Indicates whether this context has any aliases configured.
     *
     * @return {@code true} if this context has any aliases configured, {@code false} otherwise.
     */
    default boolean hasConfiguredAliases() {
        return !this.aliasTypes().isEmpty()
            || !this.aliasKeys().isEmpty()
            || !this.aliasQualifiers().isEmpty();
    }

    /**
     * Returns all types that are registered as aliases for this context.
     *
     * @return All alias types.
     */
    Set<Class<? super T>> aliasTypes();

    /**
     * Returns all keys that are registered as aliases for this context. Note that this does not
     * contain normalized keys from {@link #aliasTypes() types} or
     * {@link #aliasQualifiers() qualifiers}.
     *
     * @return All alias keys.
     */
    Set<ComponentKey<? super T>> aliasKeys();

    /**
     * Returns all qualifiers that are registered as aliases for this context.
     *
     * @return All alias qualifiers.
     */
    Set<QualifierKey<T>> aliasQualifiers();
}
