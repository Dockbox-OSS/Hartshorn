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

package test.org.dockbox.hartshorn.core.application

import org.dockbox.hartshorn.application.ApplicationContextConstructor
import org.dockbox.hartshorn.application.DefaultBindingConfigurer
import org.dockbox.hartshorn.application.ExceptionHandler
import org.dockbox.hartshorn.application.StandardApplicationBuilder
import org.dockbox.hartshorn.application.StandardApplicationContextConstructor
import org.dockbox.hartshorn.application.context.ApplicationContext
import org.dockbox.hartshorn.application.context.DelegatingApplicationContext
import org.dockbox.hartshorn.application.context.DependencyGraphInitializer
import org.dockbox.hartshorn.application.context.SimpleApplicationContext
import org.dockbox.hartshorn.application.environment.ApplicationArgumentParser
import org.dockbox.hartshorn.application.environment.ApplicationEnvironment
import org.dockbox.hartshorn.application.environment.ApplicationFSProvider
import org.dockbox.hartshorn.application.environment.ClasspathResourceLocator
import org.dockbox.hartshorn.application.environment.ContextualApplicationEnvironment
import org.dockbox.hartshorn.component.ComponentLocator
import org.dockbox.hartshorn.component.ComponentPostConstructor
import org.dockbox.hartshorn.component.ComponentPostConstructorImpl
import org.dockbox.hartshorn.component.ComponentProvider
import org.dockbox.hartshorn.component.ScopeAwareComponentProvider
import org.dockbox.hartshorn.component.condition.ConditionMatcher
import org.dockbox.hartshorn.inject.ApplicationDependencyResolver
import org.dockbox.hartshorn.inject.ConfigurationDependencyVisitor
import org.dockbox.hartshorn.inject.DependencyResolver
import org.dockbox.hartshorn.inject.binding.Binder
import org.dockbox.hartshorn.inject.binding.ComponentInstanceFactory
import org.dockbox.hartshorn.inject.processing.DependencyGraphBuilder
import org.dockbox.hartshorn.introspect.ViewContextAdapter
import org.dockbox.hartshorn.logging.ApplicationLogger
import org.dockbox.hartshorn.logging.AutoSwitchingApplicationLogger
import org.dockbox.hartshorn.proxy.ProxyOrchestrator
import org.dockbox.hartshorn.util.ContextualInitializer
import org.dockbox.hartshorn.util.Customizer
import org.dockbox.hartshorn.util.Initializer
import org.dockbox.hartshorn.util.StreamableConfigurer
import org.dockbox.hartshorn.util.introspect.annotations.AnnotationLookup
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.util.function.BiFunction
import java.util.function.Function
import java.util.stream.Stream

class BootstrapConfigurationContractTests {

    @Test
    fun testApplicationBuilderContract() {
        val instance = StandardApplicationBuilder.Configurer()

        assertDeferred(instance) { configurer, deferred: ApplicationContextConstructor? -> configurer.constructor(deferred) }
        assertInitializer(instance) { configurer, initializer -> configurer.constructor(initializer) }
        assertContextInitializer(instance) { configurer, initializer -> configurer.constructor(initializer) }

        assertCustom(instance) { configurer -> configurer.inferMainClass() }
        assertDeferred(instance) { configurer, deferred: Class<*>? -> configurer.mainClass(deferred) }
        assertInitializer(instance) { configurer, initializer -> configurer.mainClass(initializer) }

        assertDeferred(instance) { configurer, deferred: Array<String>? -> configurer.arguments(*(deferred ?: arrayOf("a"))) }
        assertDeferred(instance) { configurer, deferred: List<String>? -> configurer.arguments(deferred) }
        assertCustomizer(instance) { configurer, customizer -> configurer.arguments(customizer) }
    }

    @Test
    fun testApplicationConstructorContract() {
        val instance = StandardApplicationContextConstructor.Configurer()

        assertCustomizer(instance) { configurer, customizer -> configurer.activators(customizer) }
        assertCustomizer(instance) { configurer, customizer -> configurer.componentPreProcessors(customizer) }
        assertCustomizer(instance) { configurer, customizer -> configurer.componentPostProcessors(customizer) }
        assertCustomizer(instance) { configurer, customizer -> configurer.standaloneComponents(customizer) }
        assertCustomizer(instance) { configurer, customizer -> configurer.scanPackages(customizer) }

        assertDeferred(instance) { configurer, deferred: ApplicationEnvironment? -> configurer.environment(deferred) }
        assertContextInitializer(instance) { configurer, initializer -> configurer.environment(initializer) }

        assertDeferred(instance) { configurer, deferred: Boolean? -> configurer.includeBasePackages(deferred ?: true) }
        assertContextInitializer(instance) { configurer, initializer -> configurer.includeBasePackages(initializer) }
    }

    @Test
    fun testContextualEnvironmentContract() {
        val instance = ContextualApplicationEnvironment.Configurer()

        assertContextInitializer(instance) { configurer, initializer -> configurer.enableBanner(initializer) }
        assertCustom(instance) { configurer -> configurer.enableBanner() }
        assertCustom(instance) { configurer -> configurer.disableBanner() }

        assertContextInitializer(instance) { configurer, initializer -> configurer.enableBatchMode(initializer) }
        assertCustom(instance) { configurer -> configurer.enableBatchMode() }
        assertCustom(instance) { configurer -> configurer.disableBatchMode() }

        assertContextInitializer(instance) { configurer, initializer -> configurer.showStacktraces(initializer) }
        assertCustom(instance) { configurer -> configurer.showStacktraces() }
        assertCustom(instance) { configurer -> configurer.hideStacktraces() }

        assertDeferred(instance) { configurer, deferred: ProxyOrchestrator? -> configurer.applicationOrchestrator(deferred) }
        assertContextInitializer(instance) { configurer, initializer -> configurer.applicationOrchestrator(initializer) }

        assertDeferred(instance) { configurer, deferred: ApplicationFSProvider? -> configurer.applicationFSProvider(deferred) }
        assertContextInitializer(instance) { configurer, initializer -> configurer.applicationFSProvider(initializer) }

        assertDeferred(instance) { configurer, deferred: ExceptionHandler? -> configurer.exceptionHandler(deferred) }
        assertContextInitializer(instance) { configurer, initializer -> configurer.exceptionHandler(initializer) }

        assertDeferred(instance) { configurer, deferred: ApplicationArgumentParser? -> configurer.applicationArgumentParser(deferred) }
        assertContextInitializer(instance) { configurer, initializer -> configurer.applicationArgumentParser(initializer) }

        assertDeferred(instance) { configurer, deferred: ApplicationLogger? -> configurer.applicationLogger(deferred) }
        assertContextInitializer(instance) { configurer, initializer -> configurer.applicationLogger(initializer) }

        assertDeferred(instance) { configurer, deferred: ClasspathResourceLocator? -> configurer.classpathResourceLocator(deferred) }
        assertContextInitializer(instance) { configurer, initializer -> configurer.classpathResourceLocator(initializer) }

        assertDeferred(instance) { configurer, deferred: AnnotationLookup? -> configurer.annotationLookup(deferred) }
        assertContextInitializer(instance) { configurer, initializer -> configurer.annotationLookup(initializer) }

        assertDeferred(instance) { configurer, deferred: ApplicationContext? -> configurer.applicationContext(deferred) }
        assertContextInitializer(instance) { configurer, initializer -> configurer.applicationContext(initializer) }
    }

    @Test
    fun testAutoSwitchingApplicationLoggerContract() {
        val instance = AutoSwitchingApplicationLogger.Configurer()

        assertDeferred(instance) { configurer, deferred: ApplicationLogger? -> configurer.defaultFallback(deferred) }
        assertContextInitializer(instance) { configurer, initializer -> configurer.defaultFallback(initializer) }
    }

    @Test
    fun testSimpleApplicationContextContract() {
        val instance = SimpleApplicationContext.Configurer()

        assertDeferred(instance) { configurer, deferred: DependencyGraphInitializer? -> configurer.dependencyGraphInitializer(deferred) }
        assertContextInitializer(instance) { configurer, initializer -> configurer.dependencyGraphInitializer(initializer) }
    }

    @Test
    fun testDelegatingApplicationContextContract() {
        val instance = DelegatingApplicationContext.Configurer()

        assertDeferred(instance) { configurer, deferred: ComponentLocator? -> configurer.componentLocator(deferred) }
        assertContextInitializer(instance) { configurer, initializer -> configurer.componentLocator(initializer) }

        assertDeferred(instance) { configurer, deferred: ComponentProvider? -> configurer.componentProvider(deferred) }
        assertContextInitializer(instance) { configurer, initializer -> configurer.componentProvider(initializer) }

        assertDeferred(instance) { configurer, deferred: DefaultBindingConfigurer? -> configurer.defaultBindings(deferred) }
        assertContextInitializer(instance) { configurer, initializer -> configurer.defaultBindings(initializer) }

        val biConsumerDefaultBindingsResult = instance.defaultBindings { applicationContext: ApplicationContext, binder: Binder -> }
        Assertions.assertSame(instance, biConsumerDefaultBindingsResult)
    }

    @Test
    fun testScopeAwareComponentProviderContract() {
        val instance = ScopeAwareComponentProvider.Configurer()

        assertDeferred(instance) { configurer, deferred: ComponentPostConstructor? -> configurer.componentPostConstructor(deferred) }
        assertContextInitializer(instance) { configurer, initializer -> configurer.componentPostConstructor(initializer) }

        assertDeferred(instance) { configurer, deferred: ComponentInstanceFactory? -> configurer.componentInstanceFactory(deferred) }
        assertContextInitializer(instance) { configurer, initializer -> configurer.componentInstanceFactory(initializer) }
    }

    @Test
    fun testComponentPostConstructorImplContract() {
        val instance = ComponentPostConstructorImpl.Configurer()

        assertDeferred(instance) { configurer, deferred: ViewContextAdapter? -> configurer.viewContextAdapter(deferred) }
        assertContextInitializer(instance) { configurer, initializer -> configurer.viewContextAdapter(initializer) }
    }

    @Test
    fun testDependencyGraphInitializerContract() {
        val instance = DependencyGraphInitializer.Configurer()

        assertDeferred(instance) { configurer, deferred: DependencyResolver? -> configurer.dependencyResolver(deferred) }
        assertContextInitializer(instance) { configurer, initializer -> configurer.dependencyResolver(initializer) }

        assertDeferred(instance) { configurer, deferred: DependencyGraphBuilder? -> configurer.dependencyGraphBuilder(deferred) }
        assertContextInitializer(instance) { configurer, initializer -> configurer.dependencyGraphBuilder(initializer) }

        assertDeferred(instance) { configurer, deferred: ConfigurationDependencyVisitor? -> configurer.dependencyVisitor(deferred) }
        assertContextInitializer(instance) { configurer, initializer -> configurer.dependencyVisitor(initializer) }
    }

    @Test
    fun testApplicationDependencyResolverContract() {
        val instance = ApplicationDependencyResolver.Configurer()

        assertDeferred(instance) { configurer, deferred: ConditionMatcher? -> configurer.conditionMatcher(deferred) }
        assertContextInitializer(instance) { configurer, initializer -> configurer.conditionMatcher(initializer) }
    }

    @Test
    fun testStreamableConfigurerContract() {
        val instance = StreamableConfigurer.empty<Any, Any>()

        var result: StreamableConfigurer<Any, Any>? = null
        result = instance.add(null as Any?)
        Assertions.assertSame(instance, result)

        result = instance.add(Initializer.of(null))
        Assertions.assertSame(instance, result)

        result = instance.add(ContextualInitializer.of(null as Any?))
        Assertions.assertSame(instance, result)

        result = instance.addAll(listOf(null as Any?))
        Assertions.assertSame(instance, result)

        result = instance.addAll(*arrayOf(null as Any?))
        Assertions.assertSame(instance, result)

        result = instance.addAll(listOf(Initializer.of(null)))
        Assertions.assertSame(instance, result)

        result = instance.addAll(*arrayOf(ContextualInitializer.of(null as Any?)))
        Assertions.assertSame(instance, result)

        result = instance.remove(ContextualInitializer.of(null as Any?))
        Assertions.assertSame(instance, result)

        result = instance.clear()
        Assertions.assertSame(instance, result)

        val stream: Stream<*> = instance.stream()
        Assertions.assertNotNull(stream)
    }

    protected fun <T> assertCustom(configurer: T, deferredFunction: Function<T, T>) {
        val result = deferredFunction.apply(configurer)
        Assertions.assertSame(configurer, result)
    }

    protected fun <T, C> assertDeferred(configurer: T, deferredFunction: BiFunction<T, C?, T>) {
        val result = deferredFunction.apply(configurer, null)
        Assertions.assertSame(configurer, result)
    }

    protected fun <T, I, C> assertContextInitializer(configurer: T, initializerFunction: BiFunction<T, ContextualInitializer<I, C?>, T>) {
        val result = initializerFunction.apply(configurer) { _ -> null }
        Assertions.assertSame(configurer, result)
    }

    protected fun <T, C> assertInitializer(configurer: T, initializerFunction: BiFunction<T, Initializer<C?>, T>) {
        val result = initializerFunction.apply(configurer) { null }
        Assertions.assertSame(configurer, result)
    }

    protected fun <T, C> assertCustomizer(configurer: T, customizableFunction: BiFunction<T, Customizer<C>, T>) {
        val result = customizableFunction.apply(configurer) { it: C -> }
        Assertions.assertSame(configurer, result)
    }
}