package org.dockbox.hartshorn.application;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.application.environment.ApplicationEnvironment;
import org.dockbox.hartshorn.application.environment.ApplicationFSProvider;
import org.dockbox.hartshorn.application.environment.ClasspathResourceLocator;
import org.dockbox.hartshorn.application.lifecycle.LifecycleObservable;
import org.dockbox.hartshorn.component.ComponentLocator;
import org.dockbox.hartshorn.component.ComponentPopulator;
import org.dockbox.hartshorn.component.ComponentPostConstructor;
import org.dockbox.hartshorn.component.ComponentProvider;
import org.dockbox.hartshorn.component.PostProcessingComponentProvider;
import org.dockbox.hartshorn.component.condition.ConditionMatcher;
import org.dockbox.hartshorn.inject.binding.Binder;
import org.dockbox.hartshorn.introspect.ViewContextAdapter;
import org.dockbox.hartshorn.logging.ApplicationLogger;
import org.dockbox.hartshorn.proxy.ApplicationProxier;
import org.dockbox.hartshorn.util.introspect.Introspector;
import org.dockbox.hartshorn.util.introspect.ProxyLookup;
import org.dockbox.hartshorn.util.introspect.annotations.AnnotationLookup;
import org.slf4j.Logger;

public class InitializingContextBinderConfiguration implements ContextBinderConfiguration<InitializingContext> {

    @Override
    public void configureBindings(final InitializingContext context, final Binder binder) {
        // Application context
        binder.bind(ComponentProvider.class).singleton(context.applicationContext());
        binder.bind(ExceptionHandler.class).singleton(context.applicationContext());
        binder.bind(ApplicationContext.class).singleton(context.applicationContext());
        binder.bind(ApplicationPropertyHolder.class).singleton(context.applicationContext());

        // Application environment
        binder.bind(Introspector.class).singleton(context.environment());
        binder.bind(ApplicationEnvironment.class).singleton(context.environment());
        binder.bind(ProxyLookup.class).singleton(context.environment());
        binder.bind(ApplicationLogger.class).singleton(context.environment());
        binder.bind(ApplicationProxier.class).singleton(context.environment());
        binder.bind(LifecycleObservable.class).singleton(context.environment());
        binder.bind(ApplicationFSProvider.class).singleton(context.environment());

        // Standalone components - alphabetical order
        binder.bind(AnnotationLookup.class).singleton(context.annotationLookup());
        binder.bind(ComponentLocator.class).singleton(context.componentLocator());
        binder.bind(ComponentPopulator.class).singleton(context.componentPopulator());
        binder.bind(ComponentPostConstructor.class).singleton(context.componentPostConstructor());
        binder.bind(ComponentProvider.class).singleton(context.componentProvider());
        binder.bind(ConditionMatcher.class).singleton(context.conditionMatcher());
        binder.bind(ClasspathResourceLocator.class).singleton(context.resourceLocator());
        binder.bind(ViewContextAdapter.class).singleton(context.viewContextAdapter());

        // Standalone components - special behavior
        if (context.componentProvider() instanceof PostProcessingComponentProvider provider)
            binder.bind(PostProcessingComponentProvider.class).singleton(provider);

        // Dynamic components
        binder.bind(Logger.class).to(context.applicationContext()::log);
    }
}
