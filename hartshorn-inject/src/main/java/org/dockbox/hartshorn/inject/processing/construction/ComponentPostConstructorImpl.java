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

package org.dockbox.hartshorn.inject.processing.construction;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Set;
import org.dockbox.hartshorn.application.DefaultBindingConfigurerContext;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.inject.annotations.OnInitialized;
import org.dockbox.hartshorn.introspect.IntrospectionViewContextAdapter;
import org.dockbox.hartshorn.introspect.ViewContextAdapter;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.util.ContextualInitializer;
import org.dockbox.hartshorn.util.Customizer;
import org.dockbox.hartshorn.util.LazyStreamableConfigurer;
import org.dockbox.hartshorn.util.SingleElementContext;
import org.dockbox.hartshorn.util.StreamableConfigurer;
import org.dockbox.hartshorn.util.TypeUtils;
import org.dockbox.hartshorn.util.introspect.Introspector;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

/**
 * TODO: #1060 Add documentation
 *
 * @since 0.4.12
 *
 * @author Guus Lieben
 */
public class ComponentPostConstructorImpl implements ComponentPostConstructor {

    private final Introspector introspector;
    private final ViewContextAdapter contextAdapter;
    private final Set<Class<? extends Annotation>> annotations;

    protected ComponentPostConstructorImpl(SingleElementContext<? extends ApplicationContext> initializerContext, Configurer configurer) {
        this.applicationContext = initializerContext.input();
        this.contextAdapter = configurer.viewContextAdapter.initialize(initializerContext.transform(this.applicationContext));
        this.annotations = Set.copyOf(configurer.annotations.initialize(initializerContext.transform(this.applicationContext)));
    }

    @Override
    public <T> T doPostConstruct(T instance) throws ApplicationException {
        TypeView<T> typeView = this.introspector.introspect(instance);
        List<MethodView<T, ?>> postConstructMethods = typeView.methods().annotatedWithAny(this.annotations);

        for (MethodView<T, ?> postConstructMethod : postConstructMethods) {
            Object[] arguments = this.contextAdapter.loadParameters(postConstructMethod);
            try {
                postConstructMethod.invoke(instance, arguments);
            }
            catch (ApplicationException e) {
                throw e;
            }
            catch (Throwable e) {
                throw new ApplicationException(e);
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

    /**
     * TODO: #1060 Add documentation
     *
     * @since 0.6.0
     *
     * @author Guus Lieben
     */
    public static class Configurer {

        private final LazyStreamableConfigurer<ApplicationContext, Class<? extends Annotation>> annotations = LazyStreamableConfigurer.of(
                OnInitialized.class);

        private ContextualInitializer<ApplicationContext, ViewContextAdapter> viewContextAdapter = ContextualInitializer.of(IntrospectionViewContextAdapter::new);

        public Configurer viewContextAdapter(ViewContextAdapter lazyViewContextAdapter) {
            return this.viewContextAdapter(ContextualInitializer.of(lazyViewContextAdapter));
        }

        public Configurer viewContextAdapter(ContextualInitializer<ApplicationContext, ViewContextAdapter> viewContextAdapter) {
            this.viewContextAdapter = viewContextAdapter;
            return this;
        }

        /**
         * Configures the annotations to be used for component post-construction callbacks. By default, this only contains
         * {@link OnInitialized}.
         *
         * @param annotations The annotations to use for post-construction callbacks
         * @return The current configurer, for chaining
         */
        @SafeVarargs
        public final Configurer annotations(Class<? extends Annotation>... annotations) {
            this.annotations(collection -> collection.addAll(annotations));
            return this;
        }

        /**
         * Configures the annotations to be used for component post-construction callbacks. By default, this only contains
         * {@link OnInitialized}.
         *
         * @param annotations The annotations to use for post-construction callbacks
         * @return The current configurer, for chaining
         */
        public Configurer annotations(Set<Class<? extends Annotation>> annotations) {
            this.annotations(collection -> collection.addAll(annotations));
            return this;
        }

        /**
         * Configures the annotations to be used for component post-construction callbacks. By default, this only contains
         * {@link OnInitialized}.
         *
         * @param customizer The customizer to configure the annotations
         * @return The current configurer, for chaining
         */
        public Configurer annotations(Customizer<StreamableConfigurer<ApplicationContext, Class<? extends Annotation>>> customizer) {
            this.annotations.customizer(customizer);
            return this;
        }

        /**
         * Adds support for {@code javax.annotation.PostConstruct} if it is present on the classpath. Disabled by default,
         * but can be enabled for backwards compatibility.
         *
         * @return the current configurer, for chaining
         */
        public Configurer withJavaxAnnotations() {
            return this.annotations(collection -> {
                TypeUtils.<Annotation>forName("javax.annotation.PostConstruct").peek(collection::add);
            });
        }

        /**
         * Adds support for {@code jakarta.annotation.PostConstruct} if it is present on the classpath. Disabled by default,
         * but can be enabled for backwards compatibility.
         *
         * @return the current configurer, for chaining
         */
        public Configurer withJakartaAnnotations() {
            return this.annotations(collection -> {
                TypeUtils.<Annotation>forName("jakarta.annotation.PostConstruct").peek(collection::add);
            });
        }
    }
}
