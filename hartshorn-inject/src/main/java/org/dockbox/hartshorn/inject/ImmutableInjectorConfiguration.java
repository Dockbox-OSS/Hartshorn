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

import org.dockbox.hartshorn.properties.PropertyInitializer;
import org.dockbox.hartshorn.properties.PropertyRegistry;
import org.dockbox.hartshorn.util.configure.ContextualInitializer;
import org.dockbox.hartshorn.util.configure.Customizer;

/**
 * An immutable implementation of the {@link InjectorConfiguration} interface.
 *
 * @param isStrictMode {@link InjectorConfiguration#isStrictMode()}
 * @param allowFallbackToSingleConstructor
 * {@link InjectorConfiguration#allowFallbackToSingleConstructor()}
 * @param requiredByDefault {@link InjectorConfiguration#requiredByDefault()}
 * @param bannerEnabled {@link InjectorConfiguration#bannerEnabled()}
 * @param isBatchMode {@link InjectorConfiguration#isBatchMode()}
 * @param showStacktraces {@link InjectorConfiguration#showStacktraces()}
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public record ImmutableInjectorConfiguration(
        boolean isStrictMode,
        boolean allowFallbackToSingleConstructor,
        boolean requiredByDefault,
        boolean bannerEnabled,
        boolean isBatchMode,
        boolean showStacktraces,
        boolean includeParentScopeForFuzzyMatching
) implements InjectorConfiguration {

    /**
     * Creates a {@link ContextualInitializer} for an {@link InjectorConfiguration} using the
     * provided {@link Customizer}.
     *
     * @param customizer the customizer to configure the {@link Configurer}
     * @return a {@link ContextualInitializer} for an {@link InjectorConfiguration}
     */
    public static ContextualInitializer<PropertyRegistry, InjectorConfiguration> create(
            Customizer<Configurer> customizer
    ) {
        return registry -> {
            Configurer configurer = new Configurer();
            customizer.configure(configurer);
            return new ImmutableInjectorConfiguration(
                    configurer.enableStrictMode.initialize(registry),
                    configurer.allowFallbackToSingleConstructor.initialize(registry),
                    configurer.requiredByDefault.initialize(registry),
                    configurer.enableBanner.initialize(registry),
                    configurer.enableBatchMode.initialize(registry),
                    configurer.showStacktraces.initialize(registry),
                    configurer.includeParentScopeForFuzzyMatching.initialize(registry)
            );
        };
    }

    /**
     * A configurer for the {@link ImmutableInjectorConfiguration}.
     *
     * @since 0.7.0
     *
     * @author Guus Lieben
     */
    public static class Configurer {

        // checkstyle:off LineLength
        private ContextualInitializer<PropertyRegistry, Boolean> enableStrictMode =
                PropertyInitializer.booleanProperty("hartshorn.strict.enabled")
                        .orElseGet(() -> true);

        private ContextualInitializer<PropertyRegistry, Boolean> includeParentScopeForFuzzyMatching =
                PropertyInitializer.booleanProperty("hartshorn.inject.fuzzy-match-with-parent-scopes")
                        .orElseGet(() -> false);

        private ContextualInitializer<PropertyRegistry, Boolean> allowFallbackToSingleConstructor =
                PropertyInitializer.booleanProperty("hartshorn.inject.allow-single-constructor-fallback")
                        .orElseGet(() -> true);

        private ContextualInitializer<PropertyRegistry, Boolean> requiredByDefault =
                PropertyInitializer.booleanProperty("hartshorn.inject.required-by-default")
                        .orElseGet(() -> true);

        private ContextualInitializer<PropertyRegistry, Boolean> enableBanner =
                PropertyInitializer.booleanProperty("hartshorn.banner.enabled")
                        .orElseGet(() -> true);

        private ContextualInitializer<PropertyRegistry, Boolean> enableBatchMode =
                PropertyInitializer.booleanProperty("hartshorn.batch.enabled")
                        .orElseGet(() -> false);

        private ContextualInitializer<PropertyRegistry, Boolean> showStacktraces =
                PropertyInitializer.booleanProperty("hartshorn.exceptions.stacktraces")
                        .orElseGet(() -> true);
        // checkstyle:on LineLength

        /**
         * Enables strict mode. Strict mode is typically used to indicate that a lookup should only
         * return a value if it is explicitly bound to the key, and not if it is bound to a sub-type
         * of the key.
         *
         * @return the current {@link Configurer} instance
         */
        public Configurer enableStrictMode() {
            return this.enableStrictMode(ContextualInitializer.of(true));
        }

        /**
         * Disables strict mode. Strict mode is typically used to indicate that a lookup should only
         * return a value if it is explicitly bound to the key, and not if it is bound to a sub-type
         * of the key.
         *
         * @return the current {@link Configurer} instance
         */
        public Configurer disableStrictMode() {
            return this.enableStrictMode(ContextualInitializer.of(false));
        }

        /**
         * Enables or disables strict mode. Strict mode is typically used to indicate that a lookup
         * should only return a value if it is explicitly bound to the key, and not if it is bound
         * to a sub-type of the key.
         *
         * @param enableStrictMode whether to enable or disable strict mode
         *
         * @return the current {@link Configurer} instance
         */
        public Configurer enableStrictMode(
                ContextualInitializer<PropertyRegistry, Boolean> enableStrictMode
        ) {
            this.enableStrictMode = enableStrictMode;
            return this;
        }

        /**
         * Enables or disables including parent scopes when performing fuzzy matching.
         *
         * @param includeParentScopeForFuzzyMatching initializer to determine whether parent scopes
         * should be included for fuzzy matching.
         *
         * @return the current {@link Configurer} instance
         */
        public Configurer includeParentScopeForFuzzyMatching(
                ContextualInitializer<PropertyRegistry, Boolean> includeParentScopeForFuzzyMatching
        ) {
            this.includeParentScopeForFuzzyMatching = includeParentScopeForFuzzyMatching;
            return this;
        }

        /**
         * Enables including parent scopes when performing fuzzy matching.
         *
         * @return the current {@link Configurer} instance
         */
        public Configurer includeParentScopeForFuzzyMatching() {
            return this.includeParentScopeForFuzzyMatching(ContextualInitializer.of(true));
        }

        /**
         * Disables including parent scopes when performing fuzzy matching.
         *
         * @return the current {@link Configurer} instance
         */
        public Configurer excludeParentScopeForFuzzyMatching() {
            return this.includeParentScopeForFuzzyMatching(ContextualInitializer.of(false));
        }

        /**
         * Enables or disables the fallback to a single constructor.
         *
         * @param allowFallbackToSingleConstructor initializer to determine whether fallback is
         * allowed.
         *
         * @return the current {@link Configurer} instance
         *
         * @see InjectorConfiguration#allowFallbackToSingleConstructor()
         */
        public Configurer allowFallbackToSingleConstructor(
                ContextualInitializer<PropertyRegistry, Boolean> allowFallbackToSingleConstructor
        ) {
            this.allowFallbackToSingleConstructor = allowFallbackToSingleConstructor;
            return this;
        }

        /**
         * Enables fallback to a single constructor.
         *
         * @return the current {@link Configurer} instance
         *
         * @see InjectorConfiguration#allowFallbackToSingleConstructor()
         */
        public Configurer allowFallbackToSingleConstructor() {
            return this.allowFallbackToSingleConstructor(ContextualInitializer.of(true));
        }

        /**
         * Disables fallback to a single constructor.
         *
         * @return the current {@link Configurer} instance
         *
         * @see InjectorConfiguration#allowFallbackToSingleConstructor()
         */
        public Configurer disallowFallbackToSingleConstructor() {
            return this.allowFallbackToSingleConstructor(ContextualInitializer.of(false));
        }

        /**
         * Sets whether injection points are required by default. If this is {@code true}, all
         * injection points will be considered required unless explicitly marked as optional. If
         * this is {@code false}, all injection points will be considered optional unless explicitly
         * marked as required.
         *
         * @param requiredByDefault initializer to determine whether injection points are required
         * by default.
         *
         * @return the current {@link Configurer} instance
         *
         * @see InjectorConfiguration#requiredByDefault()
         */
        public Configurer requiredByDefault(
                ContextualInitializer<PropertyRegistry, Boolean> requiredByDefault
        ) {
            this.requiredByDefault = requiredByDefault;
            return this;
        }

        /**
         * Enables injection points to be required by default. If this is {@code true}, all
         * injection points will be considered required unless explicitly marked as optional.
         *
         * @return the current {@link Configurer} instance
         *
         * @see InjectorConfiguration#requiredByDefault()
         */
        public Configurer requireByDefault() {
            return this.requiredByDefault(ContextualInitializer.of(true));
        }

        /**
         * Disables injection points to be required by default. If this is {@code false}, all
         * injection points will be considered optional unless explicitly marked as required.
         *
         * @return the current {@link Configurer} instance
         *
         * @see InjectorConfiguration#requiredByDefault()
         */
        public Configurer optionalByDefault() {
            return this.requiredByDefault(ContextualInitializer.of(false));
        }

        /**
         * Enables or disables the banner. If the banner is enabled, it will be printed to the
         * console when the application starts. The banner is enabled by default.
         *
         * @param enableBanner whether to enable or disable the banner
         *
         * @return the current {@link Configurer} instance
         */
        public Configurer enableBanner(
                ContextualInitializer<PropertyRegistry, Boolean> enableBanner
        ) {
            this.enableBanner = enableBanner;
            return this;
        }

        /**
         * Enables the banner. If the banner is enabled, it will be printed to the console when the
         * application starts. The banner is enabled by default.
         *
         * @return the current {@link Configurer} instance
         */
        public Configurer enableBanner() {
            return this.enableBanner(ContextualInitializer.of(true));
        }

        /**
         * Disables the banner. If the banner is disabled, it will not be printed to the console
         * when the application starts. The banner is enabled by default.
         *
         * @return the current {@link Configurer} instance
         */
        public Configurer disableBanner() {
            return this.enableBanner(ContextualInitializer.of(false));
        }

        /**
         * Enables or disables batch mode. Batch mode is typically used for optimizations specific
         * to applications which will spawn multiple application contexts with shared resources.
         * Batch mode is disabled by default.
         *
         * @param enableBatchMode whether to enable or disable batch mode
         *
         * @return the current {@link Configurer} instance
         */
        public Configurer enableBatchMode(
                ContextualInitializer<PropertyRegistry, Boolean> enableBatchMode
        ) {
            this.enableBatchMode = enableBatchMode;
            return this;
        }

        /**
         * Enables batch mode. Batch mode is typically used for optimizations specific to
         * applications which will spawn multiple application contexts with shared resources. Batch
         * mode is disabled by default.
         *
         * @return the current {@link Configurer} instance
         */
        public Configurer enableBatchMode() {
            return this.enableBatchMode(ContextualInitializer.of(true));
        }

        /**
         * Disables batch mode. Batch mode is typically used for optimizations specific to
         * applications which will spawn multiple application contexts with shared resources. Batch
         * mode is disabled by default.
         *
         * @return the current {@link Configurer} instance
         */
        public Configurer disableBatchMode() {
            return this.enableBatchMode(ContextualInitializer.of(false));
        }

        /**
         * Enables or disables the printing of stacktraces when exceptions occur. Stacktraces are
         * enabled by default.
         *
         * @param showStacktraces whether to enable or disable stacktraces
         *
         * @return the current {@link Configurer} instance
         */
        public Configurer showStacktraces(
                ContextualInitializer<PropertyRegistry, Boolean> showStacktraces
        ) {
            this.showStacktraces = showStacktraces;
            return this;
        }

        /**
         * Enables the printing of stacktraces when exceptions occur. Stacktraces are enabled by
         * default.
         *
         * @return the current {@link Configurer} instance
         */
        public Configurer showStacktraces() {
            return this.showStacktraces(ContextualInitializer.of(true));
        }

        /**
         * Disables the printing of stacktraces when exceptions occur. Stacktraces are enabled by
         * default.
         *
         * @return the current {@link Configurer} instance
         */
        public Configurer hideStacktraces() {
            return this.showStacktraces(ContextualInitializer.of(false));
        }
    }
}
