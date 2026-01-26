/*
 * Copyright 2019-2026 the original author or authors.
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

package org.dockbox.hartshorn.inject;

import org.dockbox.hartshorn.inject.binding.BindingHierarchy;

/**
 * Represents the basic configuration of an {@link InjectorEnvironment}.
 *
 * @see InjectorEnvironment#configuration()
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public interface InjectorConfiguration {

    /**
     * Returns whether the injector should be in strict mode. In strict mode, the injector will
     * require all {@link BindingHierarchy binding hierarchies} to be resolved using exact matching
     * {@link ComponentKey keys}. If strict mode is disabled, the injector will attempt to resolve
     * the hierarchy using the most specific key available (fuzzy matching).
     *
     * @return {@code true} if the injector is in strict mode, {@code false} otherwise
     */
    boolean isStrictMode();

    /**
     * Returns whether the injector should allow fallback to the single constructor of a component
     * if there are no explicit injectable constructors available, and there is no default
     * constructor.
     *
     * @return {@code true} if the injector should allow fallback to the single constructor,
     * {@code false} otherwise
     */
    boolean allowFallbackToSingleConstructor();

    /**
     * Returns whether injection points are required by default. If this is {@code true}, all
     * injection points will be considered required unless explicitly marked as optional. If this is
     * {@code false}, all injection points will be considered optional unless explicitly marked as
     * required.
     *
     * @return {@code true} if injection points are required by default, {@code false} otherwise
     */
    boolean requiredByDefault();

    /**
     * Indicates whether the application banner is enabled. If enabled, the banner will be displayed
     * during application startup.
     *
     * @return {@code true} if the banner is enabled, {@code false} otherwise.
     */
    boolean bannerEnabled();

    /**
     * Indicates whether the current environment is running in batch mode. Batch mode is typically
     * used for optimizations specific to applications which will spawn multiple application
     * contexts with shared resources.
     *
     * @return {@code true} if the environment is running in batch mode, {@code false} otherwise.
     */
    boolean isBatchMode();

    /**
     * Indicates whether stack traces should be shown for exceptions thrown during injection.
     *
     * @return {@code true} if stack traces should be shown, {@code false} otherwise.
     */
    boolean showStacktraces();
}
