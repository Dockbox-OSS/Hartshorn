/*
 * Copyright 2019-2023 the original author or authors.
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

import org.dockbox.hartshorn.application.ExceptionHandler;
import org.dockbox.hartshorn.application.UseBootstrap;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.application.lifecycle.LifecycleObservable;
import org.dockbox.hartshorn.component.Component;
import org.dockbox.hartshorn.component.processing.ServiceActivator;
import org.dockbox.hartshorn.context.ContextCarrier;
import org.dockbox.hartshorn.logging.ApplicationLogger;
import org.dockbox.hartshorn.proxy.ApplicationProxier;
import org.dockbox.hartshorn.component.UseProxying;
import org.dockbox.hartshorn.util.introspect.Introspector;
import org.dockbox.hartshorn.util.introspect.annotations.AnnotationLookup;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.List;

/**
 * The environment of an active application. The environment can only be responsible for one {@link ApplicationContext},
 * and will never be bound to multiple contexts at the same time.
 */
public interface ApplicationEnvironment extends
        Introspector,
        ContextCarrier,
        ApplicationLogger,
        ApplicationProxier,
        LifecycleObservable,
        ApplicationFSProvider,
        ExceptionHandler,
        AnnotationLookup
{

    /**
     * Gets the primary {@link Introspector} for this {@link ApplicationEnvironment}. The introspector is responsible
     * for all introspection operations within the environment. This may or may not be the same as the binding for
     * {@link Introspector}, but is typically the same.
     * @return The primary {@link Introspector}
     */
    Introspector introspector();

    /**
     * Indicates whether the current environment exists within a Continuous Integration environment. If this returns
     * {@code true} this indicates the application is not active in a production environment. For example, the
     * default test suite for the framework will indicate the environment acts as a CI environment.
     *
     * @return {@code true} if the environment is a CI environment, {@code false} otherwise.
     */
    boolean isCI();

    boolean isBatchMode();

    /**
     * Gets types decorated with a given annotation, both classes and annotations.
     *
     * @param <A> The annotation constraint
     * @param annotation The annotation expected to be present on one or more types
     * @return The annotated types
     */
    <A extends Annotation> Collection<TypeView<?>> types(final Class<A> annotation);

    /**
     * Gets all sub-types of a given type. The prefix is typically a package. If no sub-types exist for the given type,
     * and empty list is returned.
     *
     * @param parent The parent type to scan for subclasses
     * @param <T> The type of the parent
     * @return The list of sub-types, or an empty list
     */
    <T> Collection<TypeView<? extends T>> children(final Class<T> parent);

    /**
     * Gets annotations of the given type, which are decorated with the given annotation. For example, if the given
     * annotation is {@link ServiceActivator} on the application
     * activator, the results will include all service activators like {@link UseBootstrap} and {@link UseProxying}.
     *
     * @param type The type to scan for annotations
     * @param annotation The annotation expected to be present on zero or more annotations
     * @return The annotated annotations
     */
    List<Annotation> annotationsWith(final TypeView<?> type, final Class<? extends Annotation> annotation);
    List<Annotation> annotationsWith(final Class<?> type, final Class<? extends Annotation> annotation);

    /**
     * Indicates whether the given type should be treated as a singleton. How this is determined is up to the
     * implementation, but typically this is determined by the presence of the {@link jakarta.inject.Singleton}
     * annotation, or the value of {@link Component#singleton()}.
     *
     * @param type The type to check
     * @return {@code true} if the type should be treated as a singleton, {@code false} otherwise.
     */
    boolean singleton(Class<?> type);
    boolean singleton(TypeView<?> type);
}
