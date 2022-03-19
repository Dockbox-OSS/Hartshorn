/*
 * Copyright 2019-2022 the original author or authors.
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
import org.dockbox.hartshorn.util.reflect.ConstructorContext;
import org.dockbox.hartshorn.util.reflect.TypeContext;
import org.dockbox.hartshorn.util.Exceptional;

import java.util.List;

/**
 * A {@link ContextDrivenProvider} is a {@link Provider} that uses a {@link ConstructorContext} to
 * create a new instance of a class. The constructor is looked up based on its parameters, where the
 * constructor with the most parameters is chosen in order to satisfy as many dependencies as possible.
 *
 * <p>If no injectable constructors can be found, the default constructro is used instead. If this
 * constructor is not injectable, an {@link IllegalStateException} is thrown.
 *
 * @param <C>
 *         The type of the class to create.
 *
 * @author Guus Lieben
 * @see Provider
 * @see SupplierProvider
 * @see InstanceProvider
 * @since 21.4
 */
public class ContextDrivenProvider<C> implements Provider<C> {

    private final TypeContext<? extends C> context;
    private ConstructorContext<? extends C> optimalConstructor;

    protected ContextDrivenProvider(final TypeContext<? extends C> context) {
        this.context = context;
    }

    @Override
    public final Exceptional<C> provide(final ApplicationContext context) {
        this.optimalConstructor = this.findOptimalConstructor().orNull();
        return this.create(context);
    }

    protected Exceptional<? extends ConstructorContext<? extends C>> findOptimalConstructor() {
        if (this.context().isAbstract()) return Exceptional.empty();
        if (this.optimalConstructor == null) {
            final List<? extends ConstructorContext<? extends C>> constructors = this.context().injectConstructors();
            if (constructors.isEmpty()) {
                final Exceptional<? extends ConstructorContext<? extends C>> defaultConstructor = this.context().defaultConstructor();
                if (defaultConstructor.absent()) {
                    throw new IllegalStateException("No injectable constructors found for " + this.context().type());
                }
                else this.optimalConstructor = defaultConstructor.get();
            }
            else {

            /*
             An optimal constructor is the one with the highest amount of injectable parameters, so as many dependencies
             can be satiated at once.
             */
                this.optimalConstructor = constructors.get(0);
                for (final ConstructorContext<? extends C> constructor : constructors) {
                    if (this.optimalConstructor.parameterCount() < constructor.parameterCount()) {
                        this.optimalConstructor = constructor;
                    }
                }
            }
        }
        return Exceptional.of(this.optimalConstructor);
    }

    protected Exceptional<C> create(final ApplicationContext context) {
        if (this.optimalConstructor() == null) return Exceptional.empty();
        return this.optimalConstructor().createInstance(context).rethrowUnchecked().map(instance -> (C) instance);
    }

    public TypeContext<? extends C> context() {
        return this.context;
    }

    public ConstructorContext<? extends C> optimalConstructor() {
        return this.optimalConstructor;
    }
}
