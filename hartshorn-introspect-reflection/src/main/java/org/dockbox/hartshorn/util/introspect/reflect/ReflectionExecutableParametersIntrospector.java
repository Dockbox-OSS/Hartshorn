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

import org.dockbox.hartshorn.util.introspect.ExecutableParametersIntrospector;
import org.dockbox.hartshorn.util.introspect.Introspector;
import org.dockbox.hartshorn.util.introspect.reflect.view.ReflectionExecutableElementView;
import org.dockbox.hartshorn.util.introspect.view.ParameterView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.option.Option;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

/**
 * TODO: #1059 Add documentation
 *
 * @since 0.4.13
 *
 * @author Guus Lieben
 */
public class ReflectionExecutableParametersIntrospector implements ExecutableParametersIntrospector {

    private final Introspector introspector;
    private final ReflectionExecutableElementView<?> executable;
    private List<ParameterView<?>> parameters;
    private List<TypeView<?>> parameterTypes;
    private List<TypeView<?>> genericParameterTypes;

    public ReflectionExecutableParametersIntrospector(Introspector introspector, ReflectionExecutableElementView<?> executable) {
        this.introspector = introspector;
        this.executable = executable;
    }

    @Override
    public List<TypeView<?>> types() {
        if (this.parameterTypes == null) {
            this.parameterTypes = Arrays.stream(this.executable.executable().getParameterTypes())
                    .map(this.introspector::introspect)
                    .collect(Collectors.toList());
        }
        return this.parameterTypes;
    }

    @Override
    public List<TypeView<?>> genericTypes() {
        if (this.genericParameterTypes == null) {
            this.genericParameterTypes = Arrays.stream(this.executable.executable().getGenericParameterTypes())
                    .map(this.introspector::introspect)
                    .collect(Collectors.toList());
        }
        return this.genericParameterTypes;
    }

    @Override
    public List<ParameterView<?>> all() {
        if (this.parameters == null) {
            List<ParameterView<?>> parameters = new LinkedList<>();
            for (Parameter parameter : this.executable.executable().getParameters()) {
                parameters.add(this.introspector.introspect(parameter));
            }
            this.parameters = parameters;
        }
        return this.parameters;
    }

    @Override
    public List<ParameterView<?>> annotatedWith(Class<? extends Annotation> annotation) {
        return this.all().stream()
                .filter(parameter -> parameter.annotations().has(annotation))
                .toList();
    }

    @Override
    public Option<ParameterView<?>> at(int index) {
        if (this.all().size() > index) {
            return Option.of(this.all().get(index));
        }
        else {
            return Option.empty();
        }
    }

    @Override
    public int count() {
        return this.executable.executable().getParameterCount();
    }

    @Override
    public boolean matches(Class<?>... parameterTypes) {
        return this.matches(Arrays.asList(parameterTypes));
    }

    @Override
    public boolean matchesExact(Class<?>... parameterTypes) {
        return this.matchesExact(Arrays.asList(parameterTypes));
    }

    @Override
    public boolean matchesExact(List<Class<?>> parameterTypes) {
        return this.matches(parameterTypes, TypeView::is);
    }

    @Override
    public boolean matches(List<Class<?>> parameterTypes) {
        return this.matches(parameterTypes, TypeView::isParentOf);
    }

    private boolean matches(List<Class<?>> parameterTypes, BiPredicate<TypeView<?>, Class<?>> predicate) {
        if (parameterTypes.size() != this.count()) {
            return false;
        }
        List<TypeView<?>> types = this.types();
        for (int i = 0; i < types.size(); i++) {
            TypeView<?> type = types.get(i);
            Class<?> expected = parameterTypes.get(i);
            if (!predicate.test(type, expected)) {
                return false;
            }
        }
        return true;

    }
}
