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

package org.dockbox.hartshorn.inject;

import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.util.introspect.view.ConstructorView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.option.Attempt;
import org.dockbox.hartshorn.util.option.Option;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class CyclingConstructorAnalyzer {

    private static final Map<Class<?>, ConstructorView<?>> cache = new ConcurrentHashMap<>();

    public static <C> Attempt<ConstructorView<C>, ? extends ApplicationException> findConstructor(final TypeView<C> type) {
        return findConstructor(type, true);
    }

    private static <C> Attempt<ConstructorView<C>, ? extends ApplicationException> findConstructor(final TypeView<C> type, final boolean checkForCycles) {
        if (type.isAbstract()) return Attempt.empty();
        if (cache.containsKey(type.type())) {
            return Attempt.of((ConstructorView<C>) cache.get(type.type()));
        }

        ConstructorView<C> optimalConstructor;
        final List<? extends ConstructorView<C>> constructors = type.constructors().injectable();
        if (constructors.isEmpty()) {
            final Option<? extends ConstructorView<C>> defaultConstructor = type.constructors().defaultConstructor();
            if (defaultConstructor.absent()) {
                if (type.constructors().bound().isEmpty()) {
                    return Attempt.of(new MissingInjectConstructorException(type));
                }
                else {
                    return Attempt.empty(); // No injectable constructors found, but there are bound constructors
                }
            }
            else optimalConstructor = defaultConstructor.get();
        }
        else {
            // An optimal constructor is the one with the highest amount of injectable parameters, so as many dependencies
            // can be satiated at once.
            optimalConstructor = constructors.get(0);
            for (final ConstructorView<C> constructor : constructors) {
                if (optimalConstructor.parameters().count() < constructor.parameters().count()) {
                    optimalConstructor = constructor;
                }
            }
        }

        if (checkForCycles) {
            final List<TypeView<?>> path = findCyclicPath(optimalConstructor, type);
            if (!path.isEmpty()) return Attempt.of(new CyclicComponentException(type, path));
        }

        return Attempt.<ConstructorView<C>, ApplicationException>of(optimalConstructor).peek(c -> {
            // Don't store if there may be a cycle in the dependency graph
            if (checkForCycles) cache.put(type.type(), c);
        });
    }

    public static List<TypeView<?>> findCyclicPath(final TypeView<?> type) {
        return findConstructor(type, false).map(c -> {
            final List<TypeView<?>> path = findCyclicPath(c, type);
            return finalizeLookup(type, path);
        }).orElseGet(ArrayList::new);
    }

    private static List<TypeView<?>> finalizeLookup(final TypeView<?> source, final List<TypeView<?>> path) {
        if (path.isEmpty()) return path;
        path.add(0, source);
        path.add(source);
        return path;
    }

    private static List<TypeView<?>> findCyclicPath(final ConstructorView<?> constructor, final TypeView<?> lookForType) {
        final List<TypeView<?>> path = new ArrayList<>();
        if (constructor.parameters().count() == 0) return path;
        for (final TypeView<?> parameterType : constructor.parameters().types()) {
            if (parameterType.equals(lookForType)) {
                return List.of(constructor.type());
            }
            final Option<? extends ConstructorView<?>> parameterConstructor = findConstructor(parameterType, false);
            if (parameterConstructor.present()) {
                final List<TypeView<?>> cyclicPath = findCyclicPath(parameterConstructor.get(), lookForType);
                if (!cyclicPath.isEmpty()) {
                    if (!(cyclicPath.size() == 1 && cyclicPath.get(0).equals(parameterType))) {
                        path.add(parameterType);
                    }
                    path.addAll(cyclicPath);
                    return path;
                }
            }
        }
        return path;
    }
}
