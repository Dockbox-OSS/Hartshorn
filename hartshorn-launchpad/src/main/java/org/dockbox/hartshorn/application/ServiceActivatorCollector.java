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

package org.dockbox.hartshorn.application;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.dockbox.hartshorn.inject.activation.ServiceActivator;
import org.dockbox.hartshorn.util.introspect.Introspector;

public class ServiceActivatorCollector {

    private final Introspector introspector;

    public ServiceActivatorCollector(Introspector introspector) {
        this.introspector = introspector;
    }

    public Set<Annotation> serviceActivators(
        Class<?> forClass
    ) {
        return this.collectServiceActivatorsOnType(forClass)
            .stream()
            .flatMap(activator -> this.collectServiceActivatorsRecursively(activator).stream())
            .collect(Collectors.toSet());
    }

    public Set<Annotation> collectServiceActivatorsRecursively(Annotation annotation) {
        Set<Annotation> serviceActivatorsOnAnnotation = this.collectServiceActivatorsOnType(annotation.annotationType());
        Set<Annotation> activators = new HashSet<>(serviceActivatorsOnAnnotation);
        activators.add(annotation);

        for (Annotation activator : serviceActivatorsOnAnnotation) {
            activators.addAll(this.collectServiceActivatorsRecursively(activator));
        }
        return activators;
    }

    protected Set<Annotation> collectServiceActivatorsOnType(Class<?> type) {
        return this.introspector
            .introspect(type)
            .annotations()
            .annotedWith(ServiceActivator.class);
    }

    public Set<ServiceActivator> collectDeclarationsOnActivator(Annotation annotation) {
        return this.introspector.introspect(annotation.annotationType())
            .annotations()
            .all(ServiceActivator.class);
    }
}
