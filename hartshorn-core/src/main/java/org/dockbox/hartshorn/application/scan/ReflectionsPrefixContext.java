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

package org.dockbox.hartshorn.application.scan;

import org.dockbox.hartshorn.application.environment.ApplicationManager;
import org.dockbox.hartshorn.util.introspect.Introspector;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ReflectionsPrefixContext extends AbstractPrefixContext<Reflections> {

    public ReflectionsPrefixContext(final ApplicationManager manager) {
        super(manager);
    }

    @Override
    protected Reflections process(final String prefix) {
        final FilterBuilder inputsFilter = new FilterBuilder();
        inputsFilter.includePackage(prefix);
        return new Reflections(new ConfigurationBuilder()
                .forPackage(prefix)
                .setInputsFilter(inputsFilter)
                .setScanners(Scanners.TypesAnnotated, Scanners.SubTypes));
    }

    @Override
    public <A extends Annotation> Collection<TypeView<?>> types(final String prefix, final Class<A> annotation, final boolean skipParents) {
        final Reflections reflections = this.get(prefix);
        final Set<Class<? extends Annotation>> extensions = this.extensions(annotation);
        final Set<TypeView<?>> types = new HashSet<>();

        this.manager().log().debug("Scanning for types with annotation {} in prefix {}", annotation.getName(), prefix);
        final Introspector introspector = this.manager().applicationContext().environment();
        for (final Class<? extends Annotation> extension : extensions) {
            for (final Class<?> type : reflections.getTypesAnnotatedWith(extension, !skipParents)) {
                types.add(introspector.introspect(type));
            }
        }
        this.manager().log().debug("Found {} types with annotation {} in prefix {}", types.size(), annotation.getName(), prefix);
        return Set.copyOf(types);
    }

    /**
     * Gets all sub-types of a given type. The prefix is typically a package. If no sub-types exist
     * for the given type, and empty list is returned.
     *
     * @param parent The parent type to scan for subclasses
     * @param <T> The type of the parent
     *
     * @return The list of sub-types, or an empty list
     */
    @Override
    public <T> Collection<TypeView<? extends T>> children(final Class<T> parent) {
        final Set<Class<? extends T>> subTypes = new HashSet<>();
        this.manager().log().debug("Scanning for sub-types of {}", parent.getName());
        for (final Reflections reflections : this.all()) {
            subTypes.addAll(reflections.getSubTypesOf(parent));
        }
        final Introspector introspector = this.manager().applicationContext().environment();
        this.manager().log().debug("Found {} sub-types of {}", subTypes.size(), parent.getName());
        return List.copyOf(subTypes).stream()
                .map(introspector::introspect)
                .collect(Collectors.toList());
    }
}
