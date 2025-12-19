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

package org.dockbox.hartshorn.inject.binding;

import org.dockbox.hartshorn.inject.ComponentKey;
import org.dockbox.hartshorn.inject.QualifierKey;

/**
 * A binding alias normalizer is responsible for transforming a base key into an alias key. This is
 * typically only used directly by {@link AliasBindingFunction} implementations, or early in the
 * binding process when a binding is not yet finalized.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public interface BindingAliasNormalizer {

    /**
     * Creates an alias key for the given base key, using the provided alias type. The alias key is
     * a new key that is based on the base key, but with the type replaced by the alias type.
     * Depending on the implementation, this may either choose to keep or discard qualifiers from
     * the original key.
     *
     * @param baseKey The base key to create an alias for.
     * @param aliasType The type to use as the alias.
     * @param <T> The type of the component.
     *
     * @return The alias key.
     */
    <T> ComponentKey<? super T> alias(ComponentKey<T> baseKey, Class<? super T> aliasType);

    /**
     * Creates an alias key for the given base key, using the provided alias key. The alias key is a
     * new key that is based on the base key, but with the type replaced by the alias key. Depending
     * on the implementation, this may either choose to keep or discard qualifiers from the original
     * key.
     *
     * @param baseKey The base key to create an alias for.
     * @param aliasQualifier The qualifier to use for the alias.
     * @param <T> The type of the component.
     *
     * @return The alias key.
     */
    <T> ComponentKey<T> alias(ComponentKey<T> baseKey, QualifierKey<T> aliasQualifier);
}
