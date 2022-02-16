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

package org.dockbox.hartshorn.core.context;

import org.dockbox.hartshorn.core.boot.ApplicationManager;
import org.dockbox.hartshorn.core.context.element.TypeContext;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import lombok.Getter;

public class ContextualApplicationEnvironment implements ApplicationEnvironment {

    @Getter private final PrefixContext prefixContext;
    @Getter private final ApplicationManager manager;

    public ContextualApplicationEnvironment(final PrefixContext prefixContext, final ApplicationManager manager) {
        this.manager = manager;
        this.prefixContext = prefixContext;
        this.manager().log().debug("Created new application environment (isCI: %s, prefixCount: %d)".formatted(this.isCI(), this.prefixContext().prefixes().size()));
    }


    @Override
    public boolean isCI() {
        return this.manager.isCI();
    }

    @Override
    public void prefix(final String prefix) {
        this.prefixContext.prefix(prefix);
    }

    @Override
    public <A extends Annotation> Collection<TypeContext<?>> types(final Class<A> annotation) {
        return this.prefixContext.types(annotation);
    }

    @Override
    public <A extends Annotation> Collection<TypeContext<?>> types(final String prefix, final Class<A> annotation, final boolean skipParents) {
        return this.prefixContext.types(prefix, annotation, skipParents);
    }

    @Override
    public <A extends Annotation> Collection<TypeContext<?>> types(final Class<A> annotation, final boolean skipParents) {
        return this.prefixContext.types(annotation, skipParents);
    }

    @Override
    public <T> Collection<TypeContext<? extends T>> children(final TypeContext<T> parent) {
        return this.prefixContext.children(parent);
    }

    @Override
    public <T> Collection<TypeContext<? extends T>> children(final Class<T> parent) {
        return this.prefixContext.children(parent);
    }

    @Override
    public List<Annotation> annotationsWith(final TypeContext<?> type, final Class<? extends Annotation> annotation) {
        final Collection<Annotation> annotations = new ArrayList<>();
        for (final Annotation typeAnnotation : type.annotations()) {
            if (TypeContext.of(typeAnnotation.annotationType()).annotation(annotation).present()) {
                annotations.add(typeAnnotation);
            }
        }
        return List.copyOf(annotations);
    }
}
