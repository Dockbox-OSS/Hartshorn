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

package org.dockbox.hartshorn.application;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.processing.ComponentProcessor;
import org.dockbox.hartshorn.component.processing.ServiceActivator;
import org.dockbox.hartshorn.util.Result;
import org.dockbox.hartshorn.util.reflect.TypeContext;

import java.lang.annotation.Annotation;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class StandardActivatorHolder implements ModifiableActivatorHolder {

    private final Set<Annotation> activators = ConcurrentHashMap.newKeySet();
    private final ApplicationContext applicationContext;

    public StandardActivatorHolder(final ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public Set<Annotation> activators() {
        return Set.copyOf(this.activators);
    }

    @Override
    public boolean hasActivator(final Class<? extends Annotation> activator) {
        final Result<ServiceActivator> annotation = TypeContext.of(activator).annotation(ServiceActivator.class);
        if (annotation.absent())
            throw new IllegalArgumentException("Requested activator " + activator.getSimpleName() + " is not decorated with @ServiceActivator");

        return this.activators.stream()
                .map(Annotation::annotationType)
                .toList()
                .contains(activator);
    }

    @Override
    public <A> A activator(final Class<A> activator) {
        return this.activators.stream()
                .filter(a -> a.annotationType().equals(activator))
                .findFirst()
                .map(activator::cast)
                .orElse(null);
    }

    @Override
    public void addActivator(final Annotation annotation) {
        if (this.activators.contains(annotation)) return;
        final TypeContext<? extends Annotation> annotationType = TypeContext.of(annotation.annotationType());
        final Result<ServiceActivator> activator = annotationType.annotation(ServiceActivator.class);
        if (activator.present()) {
            this.activators.add(annotation);
            for (final String scan : activator.get().scanPackages()) {
                this.applicationContext.bind(scan);
            }

            for (final Class<? extends ComponentProcessor> processor : activator.get().processors()) {
                final ComponentProcessor componentProcessor = this.applicationContext.get(processor);
                this.applicationContext.add(componentProcessor);
            }

            this.applicationContext.environment().annotationsWith(annotationType, ServiceActivator.class).forEach(this::addActivator);
        }
    }
}
