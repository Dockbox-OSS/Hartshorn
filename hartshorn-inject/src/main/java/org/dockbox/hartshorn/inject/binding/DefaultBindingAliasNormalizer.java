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
 * Default implementation of the {@link BindingAliasNormalizer} interface. This implementation attempts to retain as much
 * information from the base key as logically possible, while still creating a valid alias key.
 *
 * <p>Alias keys created from an alias type will retain all qualifiers from the base key, but will replace the type with the
 * alias type.
 *
 * <p>Alias keys created from an alias qualifier will lose all qualifiers from the base key, but will retain all other
 * information.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public class DefaultBindingAliasNormalizer implements BindingAliasNormalizer {

    @Override
    public <T> ComponentKey<? super T> alias(ComponentKey<T> baseKey, Class<? super T> aliasType) {
        return baseKey.mutable()
            .type(aliasType)
            .build();
    }

    @Override
    public <T> ComponentKey<T> alias(ComponentKey<T> baseKey, QualifierKey<T> aliasQualifier) {
        return baseKey.mutable()
            .withoutQualifiers()
            .qualifier(aliasQualifier)
            .build();
    }
}
