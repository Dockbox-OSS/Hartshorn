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

package org.dockbox.hartshorn.inject.graph.declaration;

import org.dockbox.hartshorn.component.CompositeQualifier;
import org.dockbox.hartshorn.inject.ComponentKey;
import org.dockbox.hartshorn.inject.CompositeQualifier;
import org.dockbox.hartshorn.inject.provider.Provider;
import org.dockbox.hartshorn.util.TypeUtils;
import org.dockbox.hartshorn.util.introspect.Introspector;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

/**
 * TODO: #1060 Add documentation
 *
 * @param <T> ...
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public class ComponentKeyDependencyDeclarationContext<T> implements DependencyDeclarationContext<T> {

    private final ComponentKey<T> key;
    private final Provider<T> provider;
    private final TypeView<T> type;

    public ComponentKeyDependencyDeclarationContext(Introspector introspector, ComponentKey<T> key, Provider<T> provider) {
        this.key = key;
        this.provider = provider;
        this.type = TypeUtils.adjustWildcards(introspector.introspect(key.parameterizedType()), TypeView.class);
    }

    public ComponentKey<T> key() {
        return this.key;
    }

    public Provider<T> provider() {
        return this.provider;
    }

    @Override
    public TypeView<T> type() {
        return this.type;
    }

    @Override
    public CompositeQualifier qualifier() {
        return this.key.qualifier();
    }

    @Override
    public String id() {
        return this.key.name();
    }
}
