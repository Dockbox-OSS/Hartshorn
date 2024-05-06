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

package org.dockbox.hartshorn.util.introspect.reflect;

import org.dockbox.hartshorn.util.introspect.Introspector;
import org.dockbox.hartshorn.util.introspect.TypeVariablesIntrospector;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.option.Option;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * TODO: #1059 Add documentation
 *
 * @since 0.4.13
 *
 * @author Guus Lieben
 */
public class ReflectionTypeVariablesIntrospector implements TypeVariablesIntrospector {

    private final Introspector introspector;
    private final List<TypeVariable<?>> variables;
    private List<TypeView<?>> typeViews;

    public ReflectionTypeVariablesIntrospector(Introspector introspector, List<TypeVariable<?>> variables) {
        this.introspector = introspector;
        this.variables = variables;
    }

    @Override
    public Option<TypeView<?>> at(int index) {
        if (this.variables.size() > index) {
            return Option.of(this.introspector.introspect((Type) this.variables.get(index)));
        }
        return Option.empty();
    }

    @Override
    public List<TypeView<?>> all() {
        if (this.typeViews == null) {
            this.typeViews = this.variables.stream()
                    .map(variable -> this.introspector.introspect((Type) variable))
                    .collect(Collectors.toList());
        }
        return this.typeViews;
    }

    @Override
    public int count() {
        return this.all().size();
    }
}
