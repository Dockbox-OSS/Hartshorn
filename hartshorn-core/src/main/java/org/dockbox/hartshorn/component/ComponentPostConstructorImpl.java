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

package org.dockbox.hartshorn.component;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.introspect.ViewContextAdapter;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.util.Customizer;
import org.dockbox.hartshorn.util.Lazy;
import org.dockbox.hartshorn.util.LazyInitializer;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.option.Attempt;

import java.util.List;

import jakarta.annotation.PostConstruct;

public class ComponentPostConstructorImpl implements ComponentPostConstructor {

    private final ApplicationContext applicationContext;
    private final Lazy<ViewContextAdapter> contextAdapter;

    protected ComponentPostConstructorImpl(final ApplicationContext applicationContext, final Configurer configurer) {
        this.applicationContext = applicationContext;
        this.contextAdapter = configurer.viewContextAdapter.initialize(applicationContext);
    }

    @Override
    public <T> T doPostConstruct(final T instance) throws ApplicationException {
        final TypeView<T> typeView = this.applicationContext.environment().introspect(instance);
        final List<MethodView<T, ?>> postConstructMethods = typeView.methods().annotatedWith(PostConstruct.class);

        for (final MethodView<T, ?> postConstructMethod : postConstructMethods) {
            final Object[] arguments = this.contextAdapter.get().loadParameters(postConstructMethod);
            final Attempt<?, Throwable> result = postConstructMethod.invoke(instance, arguments);

            if (result.errorPresent()) {
                final Throwable error = result.error();

                if (error instanceof ApplicationException applicationException) {
                    throw applicationException;
                } else {
                    throw new ApplicationException(error);
                }
            }
        }
        return instance;
    }

    public static LazyInitializer<ApplicationContext, ComponentPostConstructor> create(final Customizer<Configurer> customizer) {
        return context -> {
            final Configurer configurer = new Configurer();
            customizer.configure(configurer);
            return new ComponentPostConstructorImpl(context, configurer);
        };
    }

    public static class Configurer {

        private LazyInitializer<ApplicationContext, Lazy<ViewContextAdapter>> viewContextAdapter = context -> Lazy.of(context, ViewContextAdapter.class);

        public Configurer viewContextAdapter(final ViewContextAdapter viewContextAdapter) {
            return this.viewContextAdapter(Lazy.ofInstance(ViewContextAdapter.class, viewContextAdapter));
        }

        public Configurer viewContextAdapter(final Lazy<ViewContextAdapter> lazyViewContextAdapter) {
            return this.viewContextAdapter(LazyInitializer.of(lazyViewContextAdapter));
        }

        public Configurer viewContextAdapter(final LazyInitializer<ApplicationContext, Lazy<ViewContextAdapter>> viewContextAdapter) {
            this.viewContextAdapter = viewContextAdapter;
            return this;
        }
    }
}
