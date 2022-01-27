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

package org.dockbox.hartshorn.core.context;

import org.dockbox.hartshorn.core.MetaProvider;
import org.dockbox.hartshorn.core.annotations.context.LogExclude;
import org.dockbox.hartshorn.core.boot.ApplicationLogger;
import org.dockbox.hartshorn.core.boot.ClasspathResourceLocator;
import org.dockbox.hartshorn.core.boot.ExceptionHandler;
import org.dockbox.hartshorn.core.context.element.MethodContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.core.domain.Exceptional;
import org.dockbox.hartshorn.core.exceptions.ApplicationException;
import org.dockbox.hartshorn.core.services.ComponentLocator;
import org.dockbox.hartshorn.core.services.ComponentProcessor;
import org.slf4j.Logger;

@LogExclude
public interface ApplicationContext extends
        ApplicationBinder,
        ComponentProvider,
        ApplicationPropertyHolder,
        ExceptionHandler,
        ApplicationLogger,
        ActivatorSource
{

    <T> T populate(T type);

    void add(ComponentProcessor<?> processor);

    ComponentLocator locator();

    ClasspathResourceLocator resourceLocator();

    MetaProvider meta();

    ApplicationEnvironment environment();

    <T> T invoke(MethodContext<T, ?> method);

    <T, P> T invoke(MethodContext<T, P> method, P instance);

    @Override
    default Logger log() {
        return this.environment().manager().log();
    }

    default <C extends Context> Exceptional<C> first(final TypeContext<C> context) {
        return this.first(context.type());
    }

    default <C extends Context> Exceptional<C> first(final Class<C> context) {
        return this.first(this, context);
    }

    void enable(Object instance) throws ApplicationException;
}
