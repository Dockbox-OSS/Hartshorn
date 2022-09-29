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
import org.dockbox.hartshorn.context.DefaultContext;
import org.dockbox.hartshorn.util.collections.MultiMap;
import org.dockbox.hartshorn.util.collections.StandardMultiMap.CopyOnWriteArrayListMultiMap;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public abstract class AbstractPrefixContext<S> extends DefaultContext implements PrefixContext {

    private final ApplicationManager manager;

    private final Map<String, S> prefixes = new ConcurrentHashMap<>();
    private final MultiMap<Class<? extends Annotation>, Class<? extends Annotation>> annotationHierarchy = new CopyOnWriteArrayListMultiMap<>();

    protected AbstractPrefixContext(final ApplicationManager manager) {
        this.manager = manager;
    }

    protected ApplicationManager manager() {
        return this.manager;
    }

    protected abstract S process(String prefix);

    protected S get(final String prefix) {
        return this.prefixes.computeIfAbsent(prefix, this::process);
    }

    protected Collection<S> all() {
        return this.prefixes.values();
    }

    @Override
    public void prefix(final String prefix) {
        if (!this.prefixes.containsKey(prefix)) {
            this.manager().log().debug("Registering and caching prefix '%s'".formatted(prefix));
            this.prefixes.put(prefix, this.process(prefix));
        }
    }

    @Override
    public Set<String> prefixes() {
        return this.prefixes.keySet();
    }

    /**
     * Gets types decorated with a given annotation, both classes and annotationsWith. The prefix is
     * typically a package. If the annotation is present on a parent of the type, the highest level
     * member will be included.
     *
     * @param <A> The annotation constraint
     * @param annotation The annotation expected to be present on one or more types
     *
     * @return The annotated types
     */
    @Override
    public <A extends Annotation> Collection<TypeView<?>> types(final Class<A> annotation) {
        return this.types(annotation, false);
    }

    /**
     * Gets types decorated with a given annotation, both classes and annotationsWith. The prefix is
     * typically a package. If the annotation is present on a parent of the type, it will only be
     * included if {@code skipParents} is false.
     *
     * @param <A> The annotation constraint
     * @param annotation The annotation expected to be present on one or more types
     * @param skipParents Whether to include the type if supertypes are annotated
     *
     * @return The annotated types
     */
    @Override
    public <A extends Annotation> Collection<TypeView<?>> types(final Class<A> annotation, final boolean skipParents) {
        return this.prefixes().stream()
                .flatMap(prefix -> this.types(prefix, annotation, skipParents).stream())
                .collect(Collectors.toSet());
    }

    @Override
    public <T> Collection<TypeView<? extends T>> children(final TypeView<T> parent) {
        return this.children(parent.type());
    }

    protected <A extends Annotation> Set<Class<? extends Annotation>> extensions(final Class<A> annotation) {
        if (this.annotationHierarchy.isEmpty()) {
            for (final TypeView<? extends Annotation> annotationType : this.children(Annotation.class)) {
                for (final Class<? extends Annotation> selfOrParent : this.manager().annotationLookup().annotationHierarchy(annotationType.type())) {
                    this.annotationHierarchy.put(selfOrParent, annotationType.type());
                }
            }
        }

        final Collection<Class<? extends Annotation>> hierarchy = this.annotationHierarchy.get(annotation);

        if (hierarchy.isEmpty()) return Set.of(annotation);
        else return Set.copyOf(hierarchy);
    }
}
