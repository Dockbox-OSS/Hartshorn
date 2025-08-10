/*
 * Copyright 2019-2025 the original author or authors.
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

package org.dockbox.hartshorn.launchpad.activation;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Collects {@link ModuleActivator} annotations from a given class and its superclasses.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public class ModuleActivatorCollector {

    public Set<Annotation> moduleActivators(Class<?> forClass) {
        return this.collectModuleActivatorsOnType(forClass)
            .stream()
            .flatMap(activator -> this.collectModuleActivatorsRecursively(activator).stream())
            .collect(Collectors.toSet());
    }

    public Set<Annotation> collectModuleActivatorsRecursively(Annotation annotation) {
        Set<Annotation> moduleActivatorsOnAnnotation = this.collectModuleActivatorsOnType(annotation.annotationType());
        Set<Annotation> activators = new HashSet<>(moduleActivatorsOnAnnotation);
        activators.add(annotation);

        for (Annotation activator : moduleActivatorsOnAnnotation) {
            activators.addAll(this.collectModuleActivatorsRecursively(activator));
        }
        return activators;
    }

    protected Set<Annotation> collectModuleActivatorsOnType(Class<?> type) {
        return Stream.of(type.getAnnotations())
                .filter(annotation -> annotation.annotationType().isAnnotationPresent(ModuleActivator.class))
                .collect(Collectors.toSet());
    }

    public Set<ModuleActivator> collectDeclarationsOnActivator(Annotation annotation) {
        Class<? extends Annotation> annotationType = annotation.annotationType();
        Set<ModuleActivator> activators = new HashSet<>();
        if (annotationType.isAnnotationPresent(ModuleActivator.class)) {
            activators.add(annotationType.getAnnotation(ModuleActivator.class));
        }
        Arrays.stream(annotationType.getAnnotations())
                .filter(ann -> ann.annotationType().isAnnotationPresent(ModuleActivator.class))
                .map(ann -> ann.annotationType().getAnnotation(ModuleActivator.class))
                .forEach(activators::add);
        return activators;
    }
}
