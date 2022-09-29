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
import org.dockbox.hartshorn.util.introspect.view.ConstructorView;
import org.dockbox.hartshorn.util.introspect.view.TypeView;

/**
 * A {@link ContextDrivenProvider} is a {@link Provider} that uses a {@link ConstructorView} to
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

    private final Class<? extends C> context;
    private ConstructorView<? extends C> optimalConstructor;

    public ContextDrivenProvider(final Class<? extends C> type) {
        this.context = type;
    }

    @Override
    public final Result<ObjectContainer<C>> provide(final ApplicationContext context) {
        return this.optimalConstructor(context)
                .flatMap(ConstructorView::createWithContext)
                .map(this.type()::cast)
                .map(instance -> new ObjectContainer<>(instance, false));
    }

    protected Result<? extends ConstructorView<? extends C>> optimalConstructor(final ApplicationContext applicationContext) {
        final TypeView<? extends C> typeView = applicationContext.environment().introspect(this.type());
        if (this.optimalConstructor == null) {
            this.optimalConstructor = CyclingConstructorAnalyzer.findConstructor(typeView)
                    .rethrowUnchecked()
                    .orNull();
        }
        return Result.of(this.optimalConstructor);
    }

    public Class<? extends C> type() {
        return this.context;
    }
}
