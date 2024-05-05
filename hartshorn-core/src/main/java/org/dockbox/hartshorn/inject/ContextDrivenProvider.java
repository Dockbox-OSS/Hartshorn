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

package org.dockbox.hartshorn.inject;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.ComponentKey;
import org.dockbox.hartshorn.introspect.IntrospectionViewContextAdapter;
import org.dockbox.hartshorn.introspect.ViewContextAdapter;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.util.Tristate;
import org.dockbox.hartshorn.util.introspect.view.ConstructorView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.option.Option;

/**
 * A {@link ContextDrivenProvider} is a {@link Provider} that uses a {@link ConstructorView} to
 * create a new instance of a class. The constructor is looked up based on its parameters, where the
 * constructor with the most parameters is chosen in order to satisfy as many dependencies as possible.
 *
 * <p>If no injectable constructors can be found, the default constructor is used instead. If this
 * constructor is not injectable, an {@link IllegalStateException} is thrown.
 *
 * @param <C> The type of the class to create.
 *
 * @see Provider
 * @see SupplierProvider
 *
 * @since 0.4.3
 *
 * @author Guus Lieben
 */
public class ContextDrivenProvider<C> implements TypeAwareProvider<C> {

    private final ComponentKey<? extends C> context;
    private final LifecycleType lifecycleType;

    private ConstructorView<? extends C> optimalConstructor;
    private boolean lazy = true;

    private ContextDrivenProvider(ComponentKey<? extends C> type, LifecycleType lifecycleType) {
        this.context = type;
        this.lifecycleType = lifecycleType;
    }

    /**
     * Creates a new {@link ContextDrivenProvider} for the given type, with a prototype lifecycle.
     *
     * @param type the type of the component to create
     * @param <T> the type of the component to create
     *
     * @return a new {@link ContextDrivenProvider} for the given type
     */
    public static <T> ContextDrivenProvider<T> forPrototype(ComponentKey<? extends T> type) {
        return new ContextDrivenProvider<>(type, LifecycleType.PROTOTYPE);
    }

    /**
     * Creates a new {@link ContextDrivenProvider} for the given type, with a singleton lifecycle.
     *
     * @param type the type of the component to create
     * @param <T> the type of the component to create
     *
     * @return a new {@link ContextDrivenProvider} for the given type
     */
    public static <T> ContextDrivenProvider<T> forSingleton(ComponentKey<? extends T> type) {
        return new ContextDrivenProvider<>(type, LifecycleType.SINGLETON);
    }

    /**
     * Sets the lazy flag for the provider. If the provider is lazy, the instance will only be created
     * when it is first requested. If the provider is not lazy, the instance will be created immediately
     * after the provider is registered. Note that it remains up to the container to respect this flag,
     * and determine when to create the instance.
     *
     * @param lazy whether the provider should be lazy
     * @return the provider
     */
    public ContextDrivenProvider<C> lazy(boolean lazy) {
        this.lazy = lazy;
        return this;
    }

    @Override
    public Option<ObjectContainer<C>> provide(ApplicationContext context, ComponentRequestContext requestContext) throws ApplicationException {
        Option<? extends ConstructorView<? extends C>> constructor = this.optimalConstructor(context);
        if (constructor.absent()) {
            return Option.empty();
        }
        try {
            ViewContextAdapter contextAdapter = new IntrospectionViewContextAdapter(context);
            contextAdapter.addContext(requestContext);
            return contextAdapter.scope(this.context.scope())
                    .create(constructor.get())
                    .cast(this.type())
                    .map(instance -> ComponentObjectContainer.ofLifecycleType(instance, this.lifecycleType));
        }
        catch (Throwable throwable) {
            throw new ApplicationException("Failed to create instance of type " + this.type().getName(), throwable);
        }
    }

    @Override
    public LifecycleType defaultLifecycle() {
        return this.lifecycleType;
    }

    @Override
    public Tristate defaultLazy() {
        return Tristate.valueOf(this.lazy);
    }

    protected Option<? extends ConstructorView<? extends C>> optimalConstructor(ApplicationContext applicationContext) throws ApplicationException {
        TypeView<? extends C> typeView = applicationContext.environment().introspector().introspect(this.type());
        if (this.optimalConstructor == null) {
            try {
                this.optimalConstructor = ComponentConstructorResolver.create(applicationContext).findConstructor(typeView).orNull();
            }
            catch(Throwable throwable) {
                throw new ApplicationException(throwable);
            }
        }
        return Option.of(this.optimalConstructor);
    }

    @Override
    public Class<? extends C> type() {
        return this.context.type();
    }
}
