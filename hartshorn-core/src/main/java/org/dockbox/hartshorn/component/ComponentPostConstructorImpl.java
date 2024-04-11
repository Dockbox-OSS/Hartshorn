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

package org.dockbox.hartshorn.component;

import org.dockbox.hartshorn.application.DefaultBindingConfigurerContext;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.introspect.IntrospectionViewContextAdapter;
import org.dockbox.hartshorn.introspect.ViewContextAdapter;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.util.ContextualInitializer;
import org.dockbox.hartshorn.util.Customizer;
import org.dockbox.hartshorn.util.SingleElementContext;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

import java.util.List;

import jakarta.annotation.PostConstruct;

public class ComponentPostConstructorImpl implements ComponentPostConstructor {

    private final ApplicationContext applicationContext;
    private final ViewContextAdapter contextAdapter;

    protected ComponentPostConstructorImpl(SingleElementContext<? extends ApplicationContext> initializerContext, Configurer configurer) {
        this.applicationContext = initializerContext.input();
        this.contextAdapter = configurer.viewContextAdapter.initialize(initializerContext.transform(this.applicationContext));
    }

    @Override
    public <T> T doPostConstruct(T instance) throws ApplicationException {
        TypeView<T> typeView = this.applicationContext.environment().introspector().introspect(instance);
        List<MethodView<T, ?>> postConstructMethods = typeView.methods().annotatedWith(PostConstruct.class);

        for (MethodView<T, ?> postConstructMethod : postConstructMethods) {
            Object[] arguments = this.contextAdapter.loadParameters(postConstructMethod);
            try {
                postConstructMethod.invoke(instance, arguments);
            }
            catch (ApplicationException e) {
                throw e;
            }
            catch (Throwable e) {
                throw new FailedPostConstructionException(e);
            }
        }
        return instance;
    }

    public static ContextualInitializer<ApplicationContext, ComponentPostConstructor> create(Customizer<Configurer> customizer) {
        return context -> {
            Configurer configurer = new Configurer();
            customizer.configure(configurer);

            ComponentPostConstructorImpl postConstructor = new ComponentPostConstructorImpl(context, configurer);
            DefaultBindingConfigurerContext.compose(context, binder -> {
                binder.bind(ComponentPostConstructor.class).singleton(postConstructor);
                binder.bind(ViewContextAdapter.class).singleton(postConstructor.contextAdapter);
            });
            return postConstructor;
        };
    }

    public static class Configurer {

        private ContextualInitializer<ApplicationContext, ViewContextAdapter> viewContextAdapter = ContextualInitializer.of(IntrospectionViewContextAdapter::new);

        public Configurer viewContextAdapter(ViewContextAdapter lazyViewContextAdapter) {
            return this.viewContextAdapter(ContextualInitializer.of(lazyViewContextAdapter));
        }

        public Configurer viewContextAdapter(ContextualInitializer<ApplicationContext, ViewContextAdapter> viewContextAdapter) {
            this.viewContextAdapter = viewContextAdapter;
            return this;
        }
    }
}
