package org.dockbox.hartshorn.launchpad.configuration;

import org.dockbox.hartshorn.inject.ExceptionHandler;
import org.dockbox.hartshorn.inject.InjectionCapableApplication;
import org.dockbox.hartshorn.inject.InjectorEnvironment;
import org.dockbox.hartshorn.inject.binding.Binder;
import org.dockbox.hartshorn.inject.binding.HierarchicalBinder;
import org.dockbox.hartshorn.inject.component.ComponentRegistry;
import org.dockbox.hartshorn.inject.processing.HierarchicalBinderPostProcessor;
import org.dockbox.hartshorn.inject.processing.ProcessingPriority;
import org.dockbox.hartshorn.inject.provider.ComponentProvider;
import org.dockbox.hartshorn.inject.provider.HierarchicalComponentProvider;
import org.dockbox.hartshorn.inject.provider.HierarchicalComponentProviderOrchestrator;
import org.dockbox.hartshorn.inject.provider.SingletonCacheComponentProvider;
import org.dockbox.hartshorn.inject.provider.singleton.SingletonCache;
import org.dockbox.hartshorn.inject.scope.Scope;
import org.dockbox.hartshorn.launchpad.ApplicationContext;
import org.dockbox.hartshorn.launchpad.DelegatingApplicationContext;
import org.dockbox.hartshorn.launchpad.environment.ApplicationEnvironment;
import org.dockbox.hartshorn.launchpad.environment.ClasspathResourceLocator;
import org.dockbox.hartshorn.launchpad.environment.FileSystemProvider;
import org.dockbox.hartshorn.launchpad.lifecycle.LifecycleObservable;
import org.dockbox.hartshorn.launchpad.lifecycle.ObservableApplicationEnvironment;
import org.dockbox.hartshorn.proxy.ProxyOrchestrator;
import org.dockbox.hartshorn.util.introspect.Introspector;
import org.dockbox.hartshorn.util.introspect.ProxyLookup;
import org.dockbox.hartshorn.util.introspect.annotations.AnnotationLookup;

public class DefaultConfigurationBinderPostProcessor implements HierarchicalBinderPostProcessor {

    @Override
    public void process(InjectionCapableApplication application, Scope scope, HierarchicalBinder binder) {
        // Application environment
        binder.bind(InjectorEnvironment.class).singleton(application.environment());
        if (application.environment() instanceof ApplicationEnvironment applicationEnvironment) {
            binder.bind(ApplicationEnvironment.class).singleton(applicationEnvironment);
            binder.bind(FileSystemProvider.class).singleton(applicationEnvironment.fileSystem());
            binder.bind(ClasspathResourceLocator.class).singleton(applicationEnvironment.classpath());
            binder.bind(ComponentRegistry.class)
                    .processAfterInitialization(false)
                    .singleton(applicationEnvironment.componentRegistry());
        }
        binder.bind(Introspector.class).singleton(application.environment().introspector());
        binder.bind(AnnotationLookup.class).singleton(application.environment().introspector().annotations());
        binder.bind(ProxyLookup.class).singleton(application.environment().proxyOrchestrator());
        binder.bind(ProxyOrchestrator.class).singleton(application.environment().proxyOrchestrator());

        if (application instanceof ObservableApplicationEnvironment observableEnvironment) {
            binder.bind(LifecycleObservable.class).singleton(observableEnvironment);
        }

        // Application context
        binder.bind(InjectionCapableApplication.class).singleton(application);
        if (application instanceof ApplicationContext applicationContext) {
            binder.bind(ApplicationContext.class).singleton(applicationContext);
            binder.bind(ExceptionHandler.class).singleton(applicationContext);
        }
        binder.bind(ComponentProvider.class).singleton(application.defaultProvider());

        if (application instanceof DelegatingApplicationContext delegatingApplicationContext) {
            ComponentProvider componentProvider = delegatingApplicationContext.componentProvider();
            binder.bind(Scope.class)
                    .processAfterInitialization(false)
                    .singleton(componentProvider.scope());

            if (componentProvider instanceof HierarchicalComponentProviderOrchestrator scopeAwareComponentProvider) {
                HierarchicalComponentProvider applicationProvider = scopeAwareComponentProvider.applicationProvider();

                if (applicationProvider instanceof SingletonCacheComponentProvider singletonCacheComponentProvider) {
                    binder.bind(SingletonCache.class)
                            .processAfterInitialization(false)
                            .lazySingleton(singletonCacheComponentProvider::singletonCache);
                }
            }
        }

        // Common bindings
        binder.bind(Binder.class).singleton(binder);
        if (binder instanceof HierarchicalBinder hierarchicalBinder) {
            binder.bind(HierarchicalBinder.class).singleton(hierarchicalBinder);
        }
    }

    @Override
    public int priority() {
        return ProcessingPriority.NORMAL_PRECEDENCE;
    }
}
