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

package org.dockbox.hartshorn.inject;

import org.dockbox.hartshorn.component.ComponentKey;
import org.dockbox.hartshorn.component.CompositeQualifier;
import org.dockbox.hartshorn.util.TypeUtils;
import org.dockbox.hartshorn.util.introspect.Introspector;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

public class ComponentKeyDependencyDeclarationContext<T> implements DependencyDeclarationContext<T> {

    private final ComponentKey<T> key;
    private final TypeView<T> type;

    public ComponentKeyDependencyDeclarationContext(Introspector introspector, ComponentKey<T> key) {
        this.key = key;
        this.type = TypeUtils.adjustWildcards(introspector.introspect(key.parameterizedType()), TypeView.class);
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
