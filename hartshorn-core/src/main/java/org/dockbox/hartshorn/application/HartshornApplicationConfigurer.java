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
import java.util.Properties;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.application.context.DelegatingApplicationContext;
import org.dockbox.hartshorn.application.context.SimpleApplicationContext;
import org.dockbox.hartshorn.application.environment.ApplicationEnvironment;
import org.dockbox.hartshorn.application.environment.ContextualApplicationEnvironment;
import org.dockbox.hartshorn.component.ComponentPostConstructorImpl;
import org.dockbox.hartshorn.component.ScopeAwareComponentProvider;
import org.dockbox.hartshorn.component.populate.MethodsAndFieldsInjectionPointResolver;
import org.dockbox.hartshorn.component.processing.ComponentPostProcessor;
import org.dockbox.hartshorn.component.processing.ComponentPreProcessor;
import org.dockbox.hartshorn.inject.binding.Binder;
import org.dockbox.hartshorn.util.ContextualInitializer;
import org.dockbox.hartshorn.util.Customizer;
import org.dockbox.hartshorn.util.Initializer;
import org.dockbox.hartshorn.util.StreamableConfigurer;
/**
 * High-level application configurer and initializer for Hartshorn applications. This class provides a high-level
 * configuration for the application startup process. This allows you to configure various components in the application
 * startup process, without needing to interact with the components directly.
 *
 * <p>Note that this configurer is highly opinionated and should only be used if you do not need to customize
 * behavior at a lower level. If you need to customize behavior at a lower level, you should use the configuration
 * classes directly.
 *
 * <p>Typically you should not need to access this initializer directly, but instead use the {@link HartshornApplication}
 * class to start your application.
 *
 * @see HartshornApplication
 *
 * @since 0.6.0
 *
 * @author Guus Lieben
 */
public class HartshornApplicationConfigurer {

    private Customizer<StandardApplicationBuilder.Configurer> applicationBuilder = Customizer.useDefaults();
    private Customizer<StandardApplicationContextConstructor.Configurer> applicationContextConstructor = Customizer.useDefaults();
    private Customizer<ContextualApplicationEnvironment.Configurer> environment = Customizer.useDefaults();
    private Customizer<SimpleApplicationContext.Configurer> applicationContext = Customizer.useDefaults();
    private Customizer<MethodsAndFieldsInjectionPointResolver.Configurer> injectionPointResolver = Customizer.useDefaults();
    private Customizer<ComponentPostConstructorImpl.Configurer> componentPostConstructor = Customizer.useDefaults();

    public HartshornApplicationConfigurer arguments(Customizer<StreamableConfigurer<Class<?>, String>> customizer) {
        this.applicationBuilder = this.applicationBuilder.compose(configuration -> configuration.arguments(customizer));
        return this;
    }

    public HartshornApplicationConfigurer activators(Customizer<StreamableConfigurer<ApplicationBootstrapContext, Annotation>> customizer) {
        this.applicationContextConstructor = this.applicationContextConstructor.compose(configuration -> configuration.activators(customizer));
        return this;
    }

    public HartshornApplicationConfigurer componentPreProcessors(Customizer<StreamableConfigurer<ApplicationContext, ComponentPreProcessor>> customizer) {
        this.applicationContextConstructor = this.applicationContextConstructor.compose(configuration -> configuration.componentPreProcessors(customizer));
        return this;
    }

    public HartshornApplicationConfigurer componentPostProcessors(Customizer<StreamableConfigurer<ApplicationContext, ComponentPostProcessor>> customizer) {
        this.applicationContextConstructor = this.applicationContextConstructor.compose(configuration -> configuration.componentPostProcessors(customizer));
        return this;
    }

    public HartshornApplicationConfigurer standaloneComponents(Customizer<StreamableConfigurer<ApplicationBootstrapContext, Class<?>>> customizer) {
        this.applicationContextConstructor = this.applicationContextConstructor.compose(configuration -> configuration.standaloneComponents(customizer));
        return this;
    }

    public HartshornApplicationConfigurer scanPackages(Customizer<StreamableConfigurer<ApplicationBootstrapContext, String>> customizer) {
        this.applicationContextConstructor = this.applicationContextConstructor.compose(configuration -> configuration.scanPackages(customizer));
        return this;
    }

    public HartshornApplicationConfigurer includeBasePackages(boolean includeBasePackages) {
        return this.includeBasePackages(ContextualInitializer.of(includeBasePackages));
    }

    public HartshornApplicationConfigurer includeBasePackages(ContextualInitializer<ApplicationBuildContext, Boolean> includeBasePackages) {
        this.applicationContextConstructor = this.applicationContextConstructor.compose(configuration -> configuration.includeBasePackages(includeBasePackages));
        return this;
    }

    /**
     * Enables or disables the banner. If the banner is enabled, it will be printed to the console when the
     * application starts. The banner is enabled by default.
     *
     * @param enableBanner whether to enable or disable the banner
     * @return the current {@link HartshornApplicationConfigurer} instance
     */
    public HartshornApplicationConfigurer enableBanner(ContextualInitializer<Properties, Boolean> enableBanner) {
        this.environment = this.environment.compose(configuration -> configuration.enableBanner(enableBanner));
        return this;
    }

    /**
     * Enables the banner. If the banner is enabled, it will be printed to the console when the application
     * starts. The banner is enabled by default.
     *
     * @return the current {@link HartshornApplicationConfigurer} instance
     */
    public HartshornApplicationConfigurer enableBanner() {
        return this.enableBanner(ContextualInitializer.of(true));
    }

    /**
     * Disables the banner. If the banner is disabled, it will not be printed to the console when the application
     * starts. The banner is enabled by default.
     *
     * @return the current {@link HartshornApplicationConfigurer} instance
     */
    public HartshornApplicationConfigurer disableBanner() {
        return this.enableBanner(ContextualInitializer.of(false));
    }


    /**
     * Enables or disables batch mode. Batch mode is typically used for optimizations specific to applications
     * which will spawn multiple application contexts with shared resources. Batch mode is disabled by default.
     *
     * @param enableBatchMode whether to enable or disable batch mode
     * @return the current {@link HartshornApplicationConfigurer} instance
     */
    public HartshornApplicationConfigurer enableBatchMode(ContextualInitializer<Properties, Boolean> enableBatchMode) {
        this.environment = this.environment.compose(configuration -> configuration.enableBatchMode(enableBatchMode));
        return this;
    }

    /**
     * Enables strict mode. Strict mode is typically used to indicate that a lookup should only return a value if
     * it is explicitly bound to the key, and not if it is bound to a sub-type of the key.
     *
     * @return the current {@link HartshornApplicationConfigurer} instance
     */
    public HartshornApplicationConfigurer enableStrictMode() {
        return this.enableStrictMode(ContextualInitializer.of(true));
    }

    /**
     * Disables strict mode. Strict mode is typically used to indicate that a lookup should only return a value if
     * it is explicitly bound to the key, and not if it is bound to a sub-type of the key.
     *
     * @return the current {@link HartshornApplicationConfigurer} instance
     */
    public HartshornApplicationConfigurer disableStrictMode() {
        return this.enableStrictMode(ContextualInitializer.of(false));
    }

    /**
     * Enables or disables strict mode. Strict mode is typically used to indicate that a lookup should only return a
     * value if it is explicitly bound to the key, and not if it is bound to a sub-type of the key.
     *
     * @param enableStrictMode whether to enable or disable strict mode
     * @return the current {@link HartshornApplicationConfigurer} instance
     */
    public HartshornApplicationConfigurer enableStrictMode(ContextualInitializer<Properties, Boolean> enableStrictMode) {
        this.environment = this.environment.compose(configuration -> configuration.enableStrictMode(enableStrictMode));
        return this;
    }

    /**
     * Enables batch mode. Batch mode is typically used for optimizations specific to applications which will
     * spawn multiple application contexts with shared resources. Batch mode is disabled by default.
     *
     * @return the current {@link HartshornApplicationConfigurer} instance
     */
    public HartshornApplicationConfigurer enableBatchMode() {
        return this.enableBatchMode(ContextualInitializer.of(true));
    }

    /**
     * Disables batch mode. Batch mode is typically used for optimizations specific to applications which will
     * spawn multiple application contexts with shared resources. Batch mode is disabled by default.
     *
     * @return the current {@link HartshornApplicationConfigurer} instance
     */
    public HartshornApplicationConfigurer disableBatchMode() {
        return this.enableBatchMode(ContextualInitializer.of(false));
    }

    /**
     * Enables or disables the printing of stacktraces when exceptions occur. Stacktraces are enabled by default.
     *
     * @param showStacktraces whether to enable or disable stacktraces
     * @return the current {@link HartshornApplicationConfigurer} instance
     */
    public HartshornApplicationConfigurer showStacktraces(ContextualInitializer<Properties, Boolean> showStacktraces) {
        this.environment = this.environment.compose(configuration -> configuration.showStacktraces(showStacktraces));
        return this;
    }

    /**
     * Enables the printing of stacktraces when exceptions occur. Stacktraces are enabled by default.
     *
     * @return the current {@link HartshornApplicationConfigurer} instance
     */
    public HartshornApplicationConfigurer showStacktraces() {
        return this.showStacktraces(ContextualInitializer.of(true));
    }

    /**
     * Disables the printing of stacktraces when exceptions occur. Stacktraces are enabled by default.
     *
     * @return the current {@link HartshornApplicationConfigurer} instance
     */
    public HartshornApplicationConfigurer hideStacktraces() {
        return this.showStacktraces(ContextualInitializer.of(false));
    }

    /**
     * Sets whether the application is running in a build environment. This is typically used to disable
     * certain features that are not required in a build environment. By default this will follow the result
     * of
     *
     * @param isBuildEnvironment whether the application is running in a build environment
     *
     * @return the current {@link HartshornApplicationConfigurer} instance
     */
    public HartshornApplicationConfigurer isBuildEnvironment(ContextualInitializer<ApplicationEnvironment, Boolean> isBuildEnvironment) {
        this.environment = this.environment.compose(configuration -> configuration.isBuildEnvironment(isBuildEnvironment));
        return this;
    }

    /**
     * Sets whether the application is running in a build environment. This is typically used to disable
     * certain features that are not required in a build environment. This is disabled by default.
     *
     * @param isBuildEnvironment whether the application is running in a build environment
     *
     * @return the current {@link HartshornApplicationConfigurer} instance
     */
    public HartshornApplicationConfigurer isBuildEnvironment(boolean isBuildEnvironment) {
        return this.isBuildEnvironment(ContextualInitializer.of(isBuildEnvironment));
    }

    /**
     * Configures the {@link DefaultBindingConfigurer} that is used by the {@link DelegatingApplicationContext} to
     * configure bindings that should be available by default.
     *
     * @param defaultBindings the {@link DefaultBindingConfigurer} to use
     * @return the current instance
     */
    public HartshornApplicationConfigurer defaultBindings(ContextualInitializer<ApplicationContext, ? extends DefaultBindingConfigurer> defaultBindings) {
        this.applicationContext = this.applicationContext.compose(configuration -> configuration.defaultBindings(defaultBindings));
        return this;
    }

    /**
     * Configures the {@link DefaultBindingConfigurer} that is used by the {@link DelegatingApplicationContext} to
     * configure bindings that should be available by default.
     *
     * @param defaultBindings the {@link DefaultBindingConfigurer} to use
     * @return the current instance
     */
    public HartshornApplicationConfigurer defaultBindings(DefaultBindingConfigurer defaultBindings) {
        return this.defaultBindings(ContextualInitializer.of(defaultBindings));
    }

    /**
     * Configures the {@link DefaultBindingConfigurer} that is used by the {@link DelegatingApplicationContext} to
     * configure bindings that should be available by default.
     *
     * @param defaultBindings the {@link DefaultBindingConfigurer} to use
     * @return the current instance
     */
    public HartshornApplicationConfigurer defaultBindings(BiConsumer<ApplicationContext, Binder> defaultBindings) {
        return this.defaultBindings(context -> binder -> defaultBindings.accept(context.input(), binder));
    }


    @SafeVarargs
    public final HartshornApplicationConfigurer injectMarkerAnnotations(Class<? extends Annotation>... annotations) {
        this.injectMarkerAnnotations(collection -> collection.addAll(annotations));
        return this;
    }

    public HartshornApplicationConfigurer injectMarkerAnnotations(Set<Class<? extends Annotation>> annotations) {
        this.injectMarkerAnnotations(collection -> collection.addAll(annotations));
        return this;
    }

    public HartshornApplicationConfigurer injectMarkerAnnotations(Customizer<StreamableConfigurer<ApplicationEnvironment, Class<? extends Annotation>>> customizer) {
        this.injectionPointResolver = this.injectionPointResolver.compose(configuration -> configuration.annotations(customizer));
        return this;
    }

    @SafeVarargs
    public final HartshornApplicationConfigurer onInitializedAnnotations(Class<? extends Annotation>... annotations) {
        this.onInitializedAnnotations(collection -> collection.addAll(annotations));
        return this;
    }

    public HartshornApplicationConfigurer onInitializedAnnotations(Set<Class<? extends Annotation>> annotations) {
        this.onInitializedAnnotations(collection -> collection.addAll(annotations));
        return this;
    }

    public HartshornApplicationConfigurer onInitializedAnnotations(Customizer<StreamableConfigurer<ApplicationContext, Class<? extends Annotation>>> customizer) {
        this.componentPostConstructor = this.componentPostConstructor.compose(configuration -> configuration.annotations(customizer));
        return this;
    }

    public HartshornApplicationConfigurer withJavaxAnnotations() {
        this.componentPostConstructor = this.componentPostConstructor.compose(ComponentPostConstructorImpl.Configurer::withJavaxAnnotations);
        this.injectionPointResolver = this.injectionPointResolver.compose(MethodsAndFieldsInjectionPointResolver.Configurer::withJavaxAnnotations);
        return this;
    }

    public HartshornApplicationConfigurer withJakartaAnnotations() {
        this.componentPostConstructor = this.componentPostConstructor.compose(ComponentPostConstructorImpl.Configurer::withJakartaAnnotations);
        this.injectionPointResolver = this.injectionPointResolver.compose(MethodsAndFieldsInjectionPointResolver.Configurer::withJakartaAnnotations);
        return this;
    }

    public static Initializer<ApplicationContext> createInitializer(
        Customizer<StandardApplicationBuilder.Configurer> builderCustomizer,
        Customizer<HartshornApplicationConfigurer> applicationCustomizer
    ) {
        return () -> {
            HartshornApplicationConfigurer configurer = new HartshornApplicationConfigurer();
            applicationCustomizer.configure(configurer);
            Customizer<StandardApplicationBuilder.Configurer> customizer = builderCustomizer
                .compose(configurer::configureApplicationBuilder);
            return StandardApplicationBuilder.create(customizer).create();
        };
    }

    private void configureApplicationBuilder(StandardApplicationBuilder.Configurer builder) {
        this.applicationBuilder.configure(builder);
        builder.constructor(this.initializer(
            StandardApplicationContextConstructor::create,
            this::configureApplicationConstructor
        ));
    }

    private void configureApplicationConstructor(StandardApplicationContextConstructor.Configurer constructor) {
        this.applicationContextConstructor.configure(constructor);
        constructor.environment(this.initializer(
            ContextualApplicationEnvironment::create,
            this::configureApplicationEnvironment
        ));
    }

    private void configureApplicationEnvironment(ContextualApplicationEnvironment.Configurer environment) {
        this.environment.configure(environment);
        environment.applicationContext(this.initializer(
            SimpleApplicationContext::create,
            this::configureApplicationContext
        ));
        environment.injectionPointsResolver(MethodsAndFieldsInjectionPointResolver.create(this.injectionPointResolver));
    }

    private void configureApplicationContext(SimpleApplicationContext.Configurer context) {
        this.applicationContext.configure(context);
        context.componentProvider(this.initializer(
            ScopeAwareComponentProvider::create,
            this::configureComponentProvider
        ));
    }

    private void configureComponentProvider(ScopeAwareComponentProvider.Configurer provider) {
        provider.componentPostConstructor(ComponentPostConstructorImpl.create(this.componentPostConstructor));
    }

    private <T, C, F> ContextualInitializer<C, T> initializer(
        Function<Customizer<F>, ContextualInitializer<C, T>> initializer,
        Customizer<F> customizer
    ) {
        return input -> initializer
            .apply(customizer)
            .initialize(input);
    }
}
