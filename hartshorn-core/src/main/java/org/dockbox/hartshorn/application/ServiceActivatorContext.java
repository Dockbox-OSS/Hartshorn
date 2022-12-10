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
import org.dockbox.hartshorn.component.processing.ServiceActivator;
import org.dockbox.hartshorn.context.DefaultContext;
import org.dockbox.hartshorn.reporting.DiagnosticsPropertyCollector;
import org.dockbox.hartshorn.reporting.Reportable;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ServiceActivatorContext extends DefaultContext implements Reportable {

    private final Map<Class<? extends Annotation>, Annotation> activators = new ConcurrentHashMap<>();
    private final ApplicationContext applicationContext;

    public ServiceActivatorContext(final ApplicationContext applicationContext,
                                   final Set<Annotation> serviceActivators) {
        this.applicationContext = applicationContext;
        for (final Annotation serviceActivator : serviceActivators) {
            if (!applicationContext.environment().introspect(serviceActivator).annotations()
                    .has(ServiceActivator.class)) {
                throw new IllegalArgumentException("Annotation " + serviceActivator + " is not a valid service activator");
            }
            this.activators.put(serviceActivator.annotationType(), serviceActivator);
        }
    }

    public Set<Annotation> activators() {
        return Set.copyOf(this.activators.values());
    }

    public boolean hasActivator(final Class<? extends Annotation> activator) {
        final TypeView<? extends Annotation> activatorView = this.applicationContext.environment()
                .introspect(activator);
        if (!activatorView.annotations().has(ServiceActivator.class))
            throw new InvalidActivatorException("Requested activator " + activator.getSimpleName() + " is not decorated with @ServiceActivator");

        return this.activators.containsKey(activator);
    }

    public <A> A activator(final Class<A> activator) {
        final Annotation annotation = this.activators.get(activator);
        if (annotation != null) {
            return activator.cast(annotation);
        }
        return null;
    }

    @Override
    public void report(final DiagnosticsPropertyCollector collector) {
        final String[] activators = this.activators().stream()
                .map(a -> a.annotationType().getCanonicalName())
                .toArray(String[]::new);
        collector.property("activators").write(activators);
    }
}
