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

package org.dockbox.hartshorn.application.context;

import org.dockbox.hartshorn.application.ActivatorHolder;
import org.dockbox.hartshorn.inject.binding.ApplicationBinder;
import org.dockbox.hartshorn.application.ApplicationPropertyHolder;
import org.dockbox.hartshorn.application.environment.ClasspathResourceLocator;
import org.dockbox.hartshorn.application.ExceptionHandler;
import org.dockbox.hartshorn.application.environment.ApplicationEnvironment;
import org.dockbox.hartshorn.component.HierarchicalComponentProvider;
import org.dockbox.hartshorn.component.scope.ScopedContextHolder;
import org.dockbox.hartshorn.context.Context;
import org.dockbox.hartshorn.inject.MetaProvider;
import org.dockbox.hartshorn.logging.LogExclude;
import org.dockbox.hartshorn.logging.ApplicationLogger;
import org.dockbox.hartshorn.util.reflect.MethodContext;
import org.dockbox.hartshorn.util.reflect.TypeContext;
import org.dockbox.hartshorn.util.Exceptional;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.component.ComponentLocator;
import org.dockbox.hartshorn.component.processing.ComponentProcessor;
import org.slf4j.Logger;

import java.io.Closeable;

@LogExclude
public interface ApplicationContext extends
        ApplicationBinder,
        HierarchicalComponentProvider,
        ScopedContextHolder,
        ApplicationPropertyHolder,
        ExceptionHandler,
        ApplicationLogger,
        ActivatorHolder,
        Closeable
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
