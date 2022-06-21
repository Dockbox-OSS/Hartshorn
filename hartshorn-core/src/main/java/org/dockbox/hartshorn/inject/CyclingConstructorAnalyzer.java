/*
 * Copyright 2019-2022 the original author or authors.
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

import org.dockbox.hartshorn.util.Result;
import org.dockbox.hartshorn.util.reflect.ConstructorContext;
import org.dockbox.hartshorn.util.reflect.CyclicComponentException;
import org.dockbox.hartshorn.util.reflect.TypeContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class CyclingConstructorAnalyzer<T> {

    private static final Map<TypeContext<?>, ConstructorContext<?>> cache = new ConcurrentHashMap<>();

    public static <C> Result<ConstructorContext<C>> findConstructor(final TypeContext<C> type) {
        return findConstructor(type, true);
    }

    private static <C> Result<ConstructorContext<C>> findConstructor(final TypeContext<C> type, final boolean checkForCycles) {
        if (type.isAbstract()) return Result.empty();
        if (cache.containsKey(type)) {
            return Result.of(cache.get(type))
                    .map(c -> (ConstructorContext<C>) c);
        }

        ConstructorContext<C> optimalConstructor = null;
        final List<? extends ConstructorContext<C>> constructors = type.injectConstructors();
        if (constructors.isEmpty()) {
            final Result<? extends ConstructorContext<C>> defaultConstructor = type.defaultConstructor();
            if (defaultConstructor.absent()) {
                if (type.boundConstructors().isEmpty()) {
                    return Result.of(new IllegalStateException("No injectable constructors found for " + type.type()));
                }
                else {
                    return Result.empty(); // No injectable constructors found, but there are bound constructors
                }
            }
            else optimalConstructor = defaultConstructor.get();
        }
        else {
            // An optimal constructor is the one with the highest amount of injectable parameters, so as many dependencies
            // can be satiated at once.
            optimalConstructor = constructors.get(0);
            for (final ConstructorContext<C> constructor : constructors) {
                if (optimalConstructor.parameterCount() < constructor.parameterCount()) {
                    optimalConstructor = constructor;
                }
            }
        }

        if (optimalConstructor != null && checkForCycles) {
            final List<TypeContext<?>> path = findCyclicPath(optimalConstructor, type);
            if (!path.isEmpty()) return Result.of(new CyclicComponentException(type, path));
        }

        return Result.of(optimalConstructor).present(c -> {
            // Don't store if there may be a cycle in the dependency graph
            if (checkForCycles) cache.put(type, c);
        });
    }

    public static List<TypeContext<?>> findCyclicPath(final TypeContext<?> type) {
        return findConstructor(type, false).map(c -> {
            final List<TypeContext<?>> path = findCyclicPath(c, type);
            return finalizeLookup(type, path);
        }).orElse(ArrayList::new).get();
    }

    public static List<TypeContext<?>> findCyclicPath(final ConstructorContext<?> constructor) {
        final List<TypeContext<?>> path = findCyclicPath(constructor, constructor.type());
        return finalizeLookup(constructor.type(), path);
    }

    private static List<TypeContext<?>> finalizeLookup(final TypeContext<?> source, final List<TypeContext<?>> path) {
        if (path.isEmpty()) return path;
        path.add(0, source);
        path.add(source);
        return path;
    }

    private static List<TypeContext<?>> findCyclicPath(final ConstructorContext<?> constructor, final TypeContext<?> lookForType) {
        final List<TypeContext<?>> path = new ArrayList<>();
        if (constructor.parameterCount() == 0) return path;
        for (final TypeContext<?> parameterType : constructor.parameterTypes()) {
            if (parameterType.equals(lookForType)) {
                return List.of(constructor.type());
            }
            final Result<? extends ConstructorContext<?>> parameterConstructor = findConstructor(parameterType, false);
            if (parameterConstructor.present()) {
                final List<TypeContext<?>> cyclicPath = findCyclicPath(parameterConstructor.get(), lookForType);
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
