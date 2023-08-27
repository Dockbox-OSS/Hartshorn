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

package org.dockbox.hartshorn.application;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.application.environment.ApplicationEnvironment;
import org.dockbox.hartshorn.application.environment.ApplicationFSProvider;
import org.dockbox.hartshorn.application.lifecycle.LifecycleObservable;
import org.dockbox.hartshorn.component.ComponentProvider;
import org.dockbox.hartshorn.inject.binding.Binder;
import org.dockbox.hartshorn.logging.ApplicationLogger;
import org.dockbox.hartshorn.logging.LogExclude;
import org.dockbox.hartshorn.proxy.ApplicationProxier;
import org.dockbox.hartshorn.util.introspect.Introspector;
import org.dockbox.hartshorn.util.introspect.ProxyLookup;
import org.dockbox.hartshorn.util.introspect.annotations.AnnotationLookup;
import org.slf4j.Logger;

@LogExclude
public class ContextualEnvironmentBinderConfiguration implements EnvironmentBinderConfiguration {

    @Override
    public void configureBindings(final ApplicationEnvironment environment, final DefaultBindingConfigurer configurer, final Binder binder) {
        // Application context
        binder.bind(ComponentProvider.class).singleton(environment.applicationContext());
        binder.bind(ExceptionHandler.class).singleton(environment.applicationContext());
        binder.bind(ApplicationContext.class).singleton(environment.applicationContext());
        binder.bind(ApplicationPropertyHolder.class).singleton(environment.applicationContext());

        // Application environment
        binder.bind(Introspector.class).singleton(environment);
        binder.bind(ApplicationEnvironment.class).singleton(environment);
        binder.bind(ProxyLookup.class).singleton(environment);
        binder.bind(ApplicationLogger.class).singleton(environment);
        binder.bind(ApplicationProxier.class).singleton(environment);
        binder.bind(LifecycleObservable.class).singleton(environment);
        binder.bind(ApplicationFSProvider.class).singleton(environment);
        binder.bind(AnnotationLookup.class).singleton(environment);

        // Dynamic components
        binder.bind(Logger.class).to(environment.applicationContext()::log);

        // Custom default bindings. Runs last to allow for modification of default bindings.
        configurer.configure(binder);

        // TODO: Move to configurer
//        // Standalone components - alphabetical order
//        binder.bind(ComponentLocator.class).singleton(context.componentLocator());
//        binder.bind(ComponentPopulator.class).singleton(context.componentPopulator());
//        binder.bind(ComponentPostConstructor.class).singleton(context.componentPostConstructor());
//        binder.bind(ComponentProvider.class).singleton(context.componentProvider());
//        binder.bind(ConditionMatcher.class).singleton(context.conditionMatcher());
//        binder.bind(ClasspathResourceLocator.class).singleton(context.resourceLocator());
//        binder.bind(ViewContextAdapter.class).singleton(context.viewContextAdapter());
//
//        // Standalone components - special behavior
//        if (context.componentProvider() instanceof PostProcessingComponentProvider provider)
//            binder.bind(PostProcessingComponentProvider.class).singleton(provider);
    }
}
