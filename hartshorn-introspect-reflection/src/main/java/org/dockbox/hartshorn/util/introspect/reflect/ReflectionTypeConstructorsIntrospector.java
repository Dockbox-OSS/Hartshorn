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
import org.dockbox.hartshorn.util.introspect.TypeConstructorsIntrospector;
import org.dockbox.hartshorn.util.introspect.view.ConstructorView;
import org.dockbox.hartshorn.util.option.Option;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * TODO: #1059 Add documentation
 *
 * @param <T> ...
 *
 * @since 0.4.13
 *
 * @author Guus Lieben
 */
public class ReflectionTypeConstructorsIntrospector<T> implements TypeConstructorsIntrospector<T> {

    private final Class<T> type;
    private final Introspector introspector;

    private Option<ConstructorView<T>> defaultConstructor;
    private List<ConstructorView<T>> constructors;
    public ReflectionTypeConstructorsIntrospector(Class<T> type, Introspector introspector) {
        this.type = type;
        this.introspector = introspector;
    }

    @Override
    public Option<ConstructorView<T>> defaultConstructor() {
        if (this.defaultConstructor == null) {
            this.defaultConstructor = Option.of(() -> this.introspector.introspect(this.type.getDeclaredConstructor()));
        }
        return this.defaultConstructor;
    }

    @Override
    public List<ConstructorView<T>> annotatedWith(Class<? extends Annotation> annotation) {
        return this.all()
                .stream().filter(constructor -> constructor.annotations().has(annotation))
                .collect(Collectors.toList());
    }

    @Override
    public Option<ConstructorView<T>> withParameters(List<Class<?>> parameters) {
        return Option.of(() -> {
            Constructor<T> constructor = this.type.getDeclaredConstructor(parameters.toArray(new Class[0]));
            return this.introspector.introspect(constructor);
        });
    }

    @Override
    public List<ConstructorView<T>> all() {
        if (this.constructors == null) {
            this.constructors = Arrays.stream(this.type.getConstructors())
                    .map(constructor -> (Constructor<T>) constructor)
                    .map(this.introspector::introspect)
                    .collect(Collectors.toList());
        }
        return this.constructors;
    }

    @Override
    public int count() {
        return this.all().size();
    }
}
