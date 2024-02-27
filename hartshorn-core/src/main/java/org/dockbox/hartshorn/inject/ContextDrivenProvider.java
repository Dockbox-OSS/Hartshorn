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
 * @author Guus Lieben
 * @see Provider
 * @see SupplierProvider
 * @since 0.4.3
 */
public class ContextDrivenProvider<C> implements TypeAwareProvider<C> {

    private final ComponentKey<? extends C> context;

    private ConstructorView<? extends C> optimalConstructor;

    public ContextDrivenProvider(ComponentKey<? extends C> type) {
        this.context = type;
    }

    @Override
    public Option<ObjectContainer<C>> provide(ApplicationContext context, ComponentRequestContext requestContext) throws ApplicationException {
        Option<? extends ConstructorView<? extends C>> constructor = this.optimalConstructor(context);
        if (constructor.absent()) {
            return Option.empty();
        }
        try {
            ViewContextAdapter contextAdapter = new IntrospectionViewContextAdapter(context);
            contextAdapter.add(requestContext);
            return contextAdapter.scope(this.context.scope())
                    .create(constructor.get())
                    .cast(this.type())
                    .map(ComponentObjectContainer::new);
        }
        catch (Throwable throwable) {
            throw new ApplicationException("Failed to create instance of type " + this.type().getName(), throwable);
        }
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
