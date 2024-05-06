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

import org.dockbox.hartshorn.util.collections.MultiMap;
import org.dockbox.hartshorn.util.collections.SynchronizedArrayListMultiMap;
import org.dockbox.hartshorn.util.introspect.Introspector;
import org.dockbox.hartshorn.util.introspect.TypeMethodsIntrospector;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.option.Option;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * TODO: #1059 Add documentation
 *
 * @param <T>> ...
 *
 * @since 0.4.13
 *
 * @author Guus Lieben
 */
public class ReflectionTypeMethodsIntrospector<T> implements TypeMethodsIntrospector<T> {

    private final Introspector introspector;
    private final TypeView<T> type;

    private MultiMap<String, MethodView<T, ?>> methods;
    private List<MethodView<T, ?>> declaredAndInheritedMethods;
    private List<MethodView<T, ?>> bridgeMethods;
    private List<MethodView<T, ?>> declaredMethods;

    public ReflectionTypeMethodsIntrospector(Introspector introspector, TypeView<T> type) {
        this.introspector = introspector;
        this.type = type;
    }

    private void collect() {
        Set<Method> allMethods = new HashSet<>();
        List<Method> declaredMethods = List.of(this.type.type().getDeclaredMethods());
        List<Method> methods = List.of(this.type.type().getMethods());
        if (!this.type.superClass().isVoid()) {
            List<Method> superClassMethods = this.type.superClass().methods().all().stream()
                    .filter(method -> method.modifiers().isPublic() || method.modifiers().isProtected())
                    .flatMap(method -> method.method().stream())
                    .toList();
            allMethods.addAll(superClassMethods);
        }
        allMethods.addAll(declaredMethods);
        allMethods.addAll(methods);

        // Close stream as operating on it twice is not allowed
        List<? extends MethodView<T, ?>> introspectors = allMethods.stream()
                .map(this.introspector::introspect)
                .map(method -> (MethodView<T, ?>) method)
                .toList();

        List<? extends MethodView<T, ?>> definedMethods = introspectors.stream()
                .filter(method -> method.method().present())
                .toList();

        this.declaredMethods = definedMethods.stream()
                .filter(method -> declaredMethods.contains(method.method().get()))
                .collect(Collectors.toList());

        this.declaredAndInheritedMethods = definedMethods.stream()
                .filter(method -> !method.method().get().isBridge())
                .collect(Collectors.toList());

        this.bridgeMethods = definedMethods.stream()
                .filter(method -> method.method().get().isBridge())
                .collect(Collectors.toList());
    }

    @Override
    public Option<MethodView<T, ?>> named(String name, Collection<Class<?>> parameterTypes) {
        if (this.methods == null) {
            // Organizing the methods by name and arguments isn't worth the additional overhead for list comparisons,
            // so instead we only link it by name and perform the list comparison on request.
            this.methods = new SynchronizedArrayListMultiMap<>();
            for (MethodView<T, ?> method : this.all()) {
                this.methods.put(method.name(), method);
            }
        }
        if (this.methods.containsKey(name)) {
            Collection<MethodView<T, ?>> overloadingMethods = this.methods.get(name);
            for (MethodView<T, ?> method : overloadingMethods) {
                if (method.parameters().matches(List.copyOf(parameterTypes))) {
                    return Option.of(method);
                }
            }
        }
        return Option.empty();
    }

    @Override
    public List<MethodView<T, ?>> all() {
        if (this.declaredAndInheritedMethods == null) {
            this.collect();
        }
        return this.declaredAndInheritedMethods;
    }

    @Override
    public List<MethodView<T, ?>> declared() {
        if (this.declaredMethods == null) {
            this.collect();
        }
        return this.declaredMethods;
    }

    @Override
    public List<MethodView<T, ?>> annotatedWith(Class<? extends Annotation> annotation) {
        return this.all().stream()
                .filter(method -> method.annotations().has(annotation))
                .toList();
    }

    @Override
    public List<MethodView<T, ?>> bridges() {
        if (this.bridgeMethods == null) {
            this.collect();
        }
        return this.bridgeMethods;
    }
}
