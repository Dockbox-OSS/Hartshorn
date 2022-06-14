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
import org.dockbox.hartshorn.util.Result;
import org.dockbox.hartshorn.util.reflect.ConstructorContext;
import org.dockbox.hartshorn.util.reflect.TypeContext;

/**
 * A {@link ContextDrivenProvider} is a {@link Provider} that uses a {@link ConstructorContext} to
 * create a new instance of a class. The constructor is looked up based on its parameters, where the
 * constructor with the most parameters is chosen in order to satisfy as many dependencies as possible.
 *
 * <p>If no injectable constructors can be found, the default constructor is used instead. If this
 * constructor is not injectable, an {@link IllegalStateException} is thrown.
 *
 * @param <C>
 *         The type of the class to create.
 *
 * @author Guus Lieben
 * @see Provider
 * @see SupplierProvider
 * @since 21.4
 */
public class ContextDrivenProvider<C> implements Provider<C> {

    private final TypeContext<? extends C> context;
    private final CyclingConstructorAnalyzer<C> analyzer;
    private ConstructorContext<? extends C> optimalConstructor;

    public ContextDrivenProvider(final Class<? extends C> type) {
        this(TypeContext.of(type));
    }

    public ContextDrivenProvider(final TypeContext<? extends C> context) {
        this.context = context;
        this.analyzer = new CyclingConstructorAnalyzer(context);
    }

    @Override
    public final Result<ObjectContainer<C>> provide(final ApplicationContext context) {
        return this.optimalConstructor()
                .flatMap(constructor -> constructor.createInstance(context))
                .map(this.context().type()::cast)
                .map(instance -> new ObjectContainer<>(instance, false));
    }

    protected Result<? extends ConstructorContext<? extends C>> optimalConstructor() {
        if (this.optimalConstructor == null) {
            this.optimalConstructor = this.analyzer.findOptimalConstructor()
                    .rethrowUnchecked()
                    .orNull();
        }
        return Result.of(this.optimalConstructor);
    }

    public TypeContext<? extends C> context() {
        return this.context;
    }
}
