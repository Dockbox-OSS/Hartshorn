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
import org.dockbox.hartshorn.util.introspect.view.TypeView;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class StandardActivatorHolder implements ModifiableActivatorHolder {

    private final Map<Class<? extends Annotation>, Annotation> activators = new ConcurrentHashMap<>();
    private final ApplicationContext applicationContext;

    public StandardActivatorHolder(final ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public Set<Annotation> activators() {
        return Set.copyOf(this.activators.values());
    }

    @Override
    public boolean hasActivator(final Class<? extends Annotation> activator) {
        final TypeView<? extends Annotation> activatorView = this.applicationContext.environment().introspect(activator);
        if (!activatorView.annotations().has(ServiceActivator.class))
            throw new InvalidActivatorException("Requested activator " + activator.getSimpleName() + " is not decorated with @ServiceActivator");

        return this.activators.containsKey(activator);
    }

    @Override
    public <A> A activator(final Class<A> activator) {
        final Annotation annotation = this.activators.get(activator);
        if (annotation != null) {
            return activator.cast(annotation);
        }
        return null;
    }

    @Override
    public void addActivator(final Annotation annotation) {
        if (this.activators.containsKey(annotation.annotationType())) return;
        final TypeView<? extends Annotation> annotationType = this.applicationContext.environment()
                .introspector()
                .introspect(annotation.annotationType());

        final Result<ServiceActivator> activator = annotationType.annotations().get(ServiceActivator.class);
        if (activator.present()) {
            this.activators.put(annotation.annotationType(), annotation);
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
