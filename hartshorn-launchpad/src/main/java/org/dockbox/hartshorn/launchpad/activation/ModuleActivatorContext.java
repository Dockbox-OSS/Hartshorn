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

import org.dockbox.hartshorn.inject.DefaultFallbackCompatibleContext;
import org.dockbox.hartshorn.launchpad.ApplicationContext;
import org.dockbox.hartshorn.launchpad.launch.ApplicationContextFactory;
import org.dockbox.hartshorn.reporting.DiagnosticsPropertyCollector;
import org.dockbox.hartshorn.reporting.Reportable;
import org.dockbox.hartshorn.util.describe.ObjectDescriber;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Carrier context for {@link ModuleActivator} annotations. This context is used to store all
 * {@link ModuleActivator} annotations that are found in the application. This context is used
 * to determine which activators are available, and to retrieve the actual annotation instance.
 *
 * <p>Depending on the {@link ApplicationContextFactory} that is used to create the
 * {@link ApplicationContext}, this context may be used to supply the {@link ModuleActivator}
 * annotations.
 *
 * <p>This context should always be attached to the {@link ApplicationContext}, and yield the
 * same result as {@link ApplicationContext#activators()}.
 *
 * @see ApplicationContextFactory
 * @see ApplicationContext#activators()
 * @see ModuleActivator
 * @see ModuleActivatorHolder
 *
 * @since 0.4.13
 *
 * @author Guus Lieben
 */
public class ModuleActivatorContext extends DefaultFallbackCompatibleContext implements Reportable {

    private final Map<Class<? extends Annotation>, Annotation> activators = new ConcurrentHashMap<>();

    public ModuleActivatorContext(Set<Annotation> moduleActivators) {
        for (Annotation moduleActivator : moduleActivators) {
            if (!moduleActivator.annotationType().isAnnotationPresent(ModuleActivator.class)) {
                throw new IllegalArgumentException("Annotation " + moduleActivator + " is not a valid module activator");
            }
            this.activators.put(moduleActivator.annotationType(), moduleActivator);
        }
    }

    /**
     * Returns all {@link ModuleActivator} annotations that are available in this context. This
     * includes all activators that are provided to this context, and will not filter out any hierarchical
     * activators.
     *
     * @return All {@link ModuleActivator} annotations that are available in this context.
     */
    public Set<Annotation> activators() {
        return Set.copyOf(this.activators.values());
    }

    /**
     * Returns whether this context contains an activator for the given {@link ModuleActivator}. This
     * method will only return {@code true} if the given activator is directly available in this context,
     * but not if the given activator is hierarchical through (virtual) inheritance.
     *
     * @param activator The activator to check for
     * @return {@code true} if the given activator is directly available in this context, {@code false} otherwise.
     *
     * @see org.dockbox.hartshorn.util.introspect.annotations.Extends
     */
    public boolean hasActivator(Class<? extends Annotation> activator) {
        if (!activator.isAnnotationPresent(ModuleActivator.class)) {
            throw new InvalidActivatorException("Requested activator " + activator.getSimpleName() + " is not decorated with @" + ModuleActivator.class.getSimpleName());
        }
        return this.activators.containsKey(activator);
    }

    /**
     * Returns the {@link ModuleActivator} annotation instance for the given activator type. This method
     * will only return a value if the given activator is directly available in this context, but not if
     * the given activator is hierarchical through (virtual) inheritance.
     *
     * @param activator The activator to retrieve
     * @param <A> The type of the activator
     *
     * @return The {@link ModuleActivator} annotation instance for the given activator type, or {@code null} if the
     *         given activator is not directly available in this context.
     */
    public <A> A activator(Class<A> activator) {
        Annotation annotation = this.activators.get(activator);
        if (annotation != null) {
            return activator.cast(annotation);
        }
        return null;
    }

    @Override
    public void report(DiagnosticsPropertyCollector collector) {
        String[] activators = this.activators().stream()
                .map(activator -> activator.annotationType().getCanonicalName())
                .toArray(String[]::new);
        collector.property("activators").writeStrings(activators);
    }

    @Override
    public String toString() {
        return ObjectDescriber.of(this)
                .field("activators", this.activators)
                .describe();
    }
}
