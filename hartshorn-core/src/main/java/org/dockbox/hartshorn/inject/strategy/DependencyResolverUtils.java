/*
 * Copyright 2019-2023 the original author or authors.
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

package org.dockbox.hartshorn.inject.strategy;

import java.util.Set;
import java.util.stream.Collectors;

import org.dockbox.hartshorn.component.ComponentKey;
import org.dockbox.hartshorn.inject.HandledInjection;
import org.dockbox.hartshorn.inject.Strict;
import org.dockbox.hartshorn.util.CollectionUtilities;
import org.dockbox.hartshorn.util.StringUtilities;
import org.dockbox.hartshorn.util.introspect.ElementAnnotationsIntrospector;
import org.dockbox.hartshorn.util.introspect.view.AnnotatedElementView;
import org.dockbox.hartshorn.util.introspect.view.ExecutableElementView;
import org.dockbox.hartshorn.util.introspect.view.GenericTypeView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

import jakarta.inject.Inject;
import jakarta.inject.Named;

public class DependencyResolverUtils {

    public static Set<ComponentKey<?>> resolveDependencies(TypeView<?> type) {
        Set<ComponentKey<?>> setterDependencies = type.methods().annotatedWith(Inject.class).stream()
                .flatMap(method -> DependencyResolverUtils.resolveDependencies(method).stream())
                .collect(Collectors.toSet());
        Set<ComponentKey<?>> fieldDependencies = type.fields().annotatedWith(Inject.class).stream()
                .map(DependencyResolverUtils::resolveComponentKey)
                .collect(Collectors.toSet());

        return CollectionUtilities.merge(setterDependencies, fieldDependencies);
    }

    public static Set<ComponentKey<?>> resolveDependencies(ExecutableElementView<?> executable) {
        return executable.parameters().all().stream()
                .filter(parameter -> !parameter.annotations().has(HandledInjection.class))
                .map(DependencyResolverUtils::resolveComponentKey)
                .collect(Collectors.toSet());
    }

    public static <T, E extends AnnotatedElementView & GenericTypeView<T>> ComponentKey<T> resolveComponentKey(E element) {
        TypeView<T> type = element.genericType();
        ComponentKey.Builder<T> keyBuilder = ComponentKey.builder(type);

        ElementAnnotationsIntrospector annotations = element.annotations();
        annotations.get(Named.class)
                .filter(qualifier -> StringUtilities.notEmpty(qualifier.value()))
                .peek(qualifier -> {
                    if (StringUtilities.notEmpty(qualifier.value())) {
                        keyBuilder.name(qualifier);
                    }
                });
        annotations.get(Strict.class)
            .peek(strict -> keyBuilder.strict(strict.value()));

        return keyBuilder.build();
    }
}
