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
import org.dockbox.hartshorn.inject.IllegalScopeException;
import org.dockbox.hartshorn.inject.QualifierKey;
import org.dockbox.hartshorn.inject.scope.ScopeKey;

/**
 * A binding function that allows for the addition of aliases to a binding. Aliases are additional
 * keys that can be used to reference the same binding. This is useful for example when a binding is
 * defined in multiple modules, and you want to reference the same binding using different keys.
 *
 * @param <T> The type of the component that this binding is for.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public interface AliasBindingFunction<T> extends BindingFunction<T> {

    /**
     * Adds an alias to the current binding. Depending on the implementation, this may either choose
     * to keep or discard qualifiers from the original binding key.
     *
     * @param aliasType The type to register as an alias.
     *
     * @return Itself, for chaining.
     *
     * @see BindingAliasNormalizer
     */
    AliasBindingFunction<T> alias(Class<? super T> aliasType);

    /**
     * Adds an alias to the current binding. The given key is used exactly as-is, without any
     * normalization. This means that any qualifiers that are part of the key are preserved, and any
     * qualifiers on the original key are not explicitly retained unless present in the alias.
     *
     * @param aliasKey The key to register as an alias.
     *
     * @return Itself, for chaining.
     */
    AliasBindingFunction<T> alias(ComponentKey<? super T> aliasKey);

    /**
     * Adds an alias to the current binding. Depending on the implementation, this may either choose
     * to keep or discard existing qualifiers from the original binding key. This means that new
     * qualifiers may either replace existing qualifiers, or be added to the existing qualifiers.
     *
     * @param aliasQualifier The qualifier to use for the alias.
     *
     * @return Itself, for chaining.
     *
     * @see BindingAliasNormalizer
     */
    AliasBindingFunction<T> alias(QualifierKey<T> aliasQualifier);

    @Override
    AliasBindingFunction<T> installTo(ScopeKey scope) throws IllegalScopeException;

    @Override
    AliasBindingFunction<T> processAfterInitialization(boolean processAfterInitialization);

    @Override
    AliasBindingFunction<T> priority(int priority);
}
