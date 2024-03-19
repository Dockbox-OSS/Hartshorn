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
import java.lang.annotation.Documented;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import org.dockbox.hartshorn.application.ExceptionHandler;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.Component;
import org.dockbox.hartshorn.component.ComponentKey;
import org.dockbox.hartshorn.component.populate.ComponentInjectionPointsResolver;
import org.dockbox.hartshorn.context.ContextCarrier;
import org.dockbox.hartshorn.inject.ComponentKeyResolver;
import org.dockbox.hartshorn.proxy.ProxyOrchestrator;
import org.dockbox.hartshorn.util.introspect.Introspector;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

/**
 * The environment of an active application. The environment can only be responsible for one {@link ApplicationContext},
 * and will never be bound to multiple contexts at the same time.
 *
 * @since 0.4.4
 *
 * @author Guus Lieben
 */
public interface ApplicationEnvironment extends ContextCarrier, ExceptionHandler {

    /**
     * Gets the {@link ProxyOrchestrator} for the current environment. The orchestrator is responsible for all proxy
     * operations within the environment. Proxies may be created outside of the orchestrator, and depending on the
     * supported {@link org.dockbox.hartshorn.util.introspect.ProxyLookup proxy lookups} the orchestrator may or may not
     * support these external proxies.
     *
     * @return The proxy orchestrator
     */
    ProxyOrchestrator proxyOrchestrator();

    /**
     * Gets the {@link FileSystemProvider file system provider} for the current environment. The provider is
     * responsible for all file system operations within the environment. This may or may not be the same as the binding
     * for {@link FileSystemProvider}, but is typically the same.
     *
     * @return The file system provider
     */
    FileSystemProvider fileSystem();

    /**
     * Gets the {@link ClasspathResourceLocator} for the current environment. The locator is responsible for locating
     * resources on the classpath, and is typically used for loading configuration files and similar bundled resources.
     * This may or may not be the same as the binding for {@link ClasspathResourceLocator}, but is typically the same.
     *
     * @return The classpath resource locator
     */
    ClasspathResourceLocator classpath();

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
    boolean isBuildEnvironment();

    /**
     * Indicates whether the current environment is running in batch mode. Batch mode is typically used for
     * optimizations specific to applications which will spawn multiple application contexts with shared resources.
     *
     * @return {@code true} if the environment is running in batch mode, {@code false} otherwise.
     */
    boolean isBatchMode();

    /**
     * Indicates whether strict mode is enabled. Strict mode is typically used to indicate that a lookup should only
     * return a value if it is explicitly bound to the key, and not if it is bound to a sub-type of the key. This value
     * is typically the default value of {@link ComponentKey#strict()} if it is not explicitly set.
     *
     * @return {@code true} if strict mode is enabled, {@code false} otherwise.
     */
    boolean isStrictMode();

    /**
     * Gets types decorated with a given annotation, both classes and annotations.
     *
     * @param <A> The annotation constraint
     * @param annotation The annotation expected to be present on one or more types
     * @return The annotated types
     */
    <A extends Annotation> Collection<TypeView<?>> types(Class<A> annotation);

    /**
     * Gets all sub-types of a given type. The prefix is typically a package. If no sub-types exist for the given type,
     * and empty list is returned.
     *
     * @param parent The parent type to scan for subclasses
     * @param <T> The type of the parent
     * @return The list of sub-types, or an empty list
     */
    <T> Collection<TypeView<? extends T>> children(Class<T> parent);

    /**
     * Gets annotations of the given type, which are decorated with the given annotation. For example, given the
     * annotation and class below, the result of requesting annotations with {@link Documented} for {@code MyClass}
     * would be a list containing {@code MyAnnotation}, but not {@code AnotherAnnotation}.
     *
     * <pre>{@code
     * @Documented
     * public @interface MyAnnotation { }
     *
     * public @interface AnotherAnnotation { }
     *
     * @MyAnnotation
     * public class MyClass { }
     * }</pre>
     *
     * @param type The type to scan for annotations
     * @param annotation The annotation expected to be present on zero or more annotations
     * @return The annotated annotations
     */
    List<Annotation> annotationsWith(TypeView<?> type, Class<? extends Annotation> annotation);

    /**
     * Gets annotations of the given type, which are decorated with the given annotation. For example, given the
     * annotation and class below, the result of requesting annotations with {@link Documented} for {@code MyClass}
     * would be a list containing {@code MyAnnotation}, but not {@code AnotherAnnotation}.
     *
     * <pre>{@code
     * @Documented
     * public @interface MyAnnotation { }
     *
     * public @interface AnotherAnnotation { }
     *
     * @MyAnnotation
     * public class MyClass { }
     * }</pre>
     *
     * @param type The type to scan for annotations
     * @param annotation The annotation expected to be present on zero or more annotations
     * @return The annotated annotations
     */
    List<Annotation> annotationsWith(Class<?> type, Class<? extends Annotation> annotation);

    /**
     * Indicates whether the given type should be treated as a singleton. How this is determined is up to the
     * implementation, but typically this is determined by the presence of the {@link jakarta.inject.Singleton}
     * annotation, or the value of {@link Component#singleton()}.
     *
     * @param type The type to check
     * @return {@code true} if the type should be treated as a singleton, {@code false} otherwise.
     */
    boolean singleton(Class<?> type);

    /**
     * Indicates whether the given type should be treated as a singleton. How this is determined is up to the
     * implementation, but typically this is determined by the presence of the {@link jakarta.inject.Singleton}
     * annotation, or the value of {@link Component#singleton()}.
     *
     * @param type The type to check
     * @return {@code true} if the type should be treated as a singleton, {@code false} otherwise.
     */
    boolean singleton(TypeView<?> type);

    /**
     * Gets the raw arguments passed to the application. This is typically the arguments passed to the main method, or
     * indirectly set in {@link org.dockbox.hartshorn.application.HartshornApplication#create(String...)}. The
     * arguments are returned as a {@link Properties} object, where the key is the argument name, and the value is the
     * argument value. The key/value pair is parsed by the active {@link ApplicationArgumentParser}.
     *
     * @see org.dockbox.hartshorn.application.environment.StandardApplicationArgumentParser
     * @see org.dockbox.hartshorn.application.environment.ApplicationArgumentParser
     *
     * @return The raw arguments
     */
    Properties rawArguments();

    /**
     * Gets the {@link ComponentKeyResolver} for the current environment. The resolver is responsible for resolving
     * {@link ComponentKey}s for a given element. This is typically used for resolving {@link ComponentKey}s for binding
     * declarations and injection points.
     *
     * @return The component key resolver
     */
    ComponentKeyResolver componentKeyResolver();

    /**
     * Gets the {@link ComponentInjectionPointsResolver} for the current environment. The resolver is responsible for
     * resolving {@link org.dockbox.hartshorn.component.populate.ComponentInjectionPoint}s for a given type.
     *
     * @return The component injection points resolver
     */
    ComponentInjectionPointsResolver injectionPointsResolver();
}
