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

package org.dockbox.hartshorn.application.environment;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.dockbox.hartshorn.util.introspect.Introspector;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

public class ClassPathEnvironmentTypeResolver implements EnvironmentTypeResolver {

    private final EnvironmentTypeCollector typeCollector;
    private final Introspector introspector;

    public ClassPathEnvironmentTypeResolver(EnvironmentTypeCollector typeCollector, Introspector introspector) {
        this.typeCollector = typeCollector;
        this.introspector = introspector;
    }

    @Override
    public <A extends Annotation> Collection<TypeView<?>> types(Class<A> annotation) {
        return this.typeCollector.types(type -> type.annotations().has(annotation));
    }

    @Override
    public <T> Collection<TypeView<? extends T>> children(Class<T> parent) {
        return this.typeCollector.types(type -> type.isChildOf(parent) && !type.is(parent));
    }

    @Override
    public List<Annotation> annotationsWith(TypeView<?> type, Class<? extends Annotation> annotation) {
        Collection<Annotation> annotations = new ArrayList<>();
        for (Annotation typeAnnotation : type.annotations().all()) {
            if (this.introspector.introspect(typeAnnotation.annotationType()).annotations().has(annotation)) {
                annotations.add(typeAnnotation);
            }
        }
        return List.copyOf(annotations);
    }

    @Override
    public List<Annotation> annotationsWith(Class<?> type, Class<? extends Annotation> annotation) {
        return this.annotationsWith(this.introspector.introspect(type), annotation);
    }
}
