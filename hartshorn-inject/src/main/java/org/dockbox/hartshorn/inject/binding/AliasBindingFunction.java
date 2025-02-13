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

public interface AliasBindingFunction<T> extends BindingFunction<T> {

    AliasBindingFunction<T> alias(Class<? super T> aliasType);

    AliasBindingFunction<T> alias(ComponentKey<? super T> aliasKey);

    AliasBindingFunction<T> alias(QualifierKey<T> aliasQualifier);

    @Override
    AliasBindingFunction<T> installTo(ScopeKey scope) throws IllegalScopeException;

    @Override
    AliasBindingFunction<T> processAfterInitialization(boolean processAfterInitialization);

    @Override
    AliasBindingFunction<T> priority(int priority);
}
