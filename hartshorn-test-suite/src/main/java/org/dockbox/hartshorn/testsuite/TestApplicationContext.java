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

package org.dockbox.hartshorn.testsuite;

import org.dockbox.hartshorn.application.StartupModifiers;
import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.application.context.StandardDelegatingApplicationContext;
import org.dockbox.hartshorn.application.environment.ApplicationEnvironment;
import org.dockbox.hartshorn.application.environment.ClasspathResourceLocator;
import org.dockbox.hartshorn.component.ComponentLocator;
import org.dockbox.hartshorn.component.ComponentPopulator;
import org.dockbox.hartshorn.component.ComponentProvider;
import org.dockbox.hartshorn.inject.MetaProvider;
import org.dockbox.hartshorn.inject.binding.ComponentBinding;
import org.dockbox.hartshorn.util.reflect.TypeContext;

import java.util.Set;
import java.util.function.Function;

public class TestApplicationContext extends StandardDelegatingApplicationContext {

    public TestApplicationContext(final ApplicationEnvironment environment,
                                  final Function<ApplicationContext, ComponentLocator> componentLocator,
                                  final Function<ApplicationContext, ClasspathResourceLocator> resourceLocator,
                                  final Function<ApplicationContext, MetaProvider> metaProvider,
                                  final Function<ApplicationContext, ComponentProvider> componentProvider,
                                  final Function<ApplicationContext, ComponentPopulator> componentPopulator,
                                  final TypeContext<?> activationSource, final Set<String> args, final Set<StartupModifiers> modifiers) {
        super(environment, componentLocator, resourceLocator, metaProvider, componentProvider, componentPopulator, activationSource, args, modifiers);
    }

    @Override
    protected <T> void handleBinder(final TypeContext<T> implementer, final ComponentBinding annotation) {
        super.handleBinder(implementer, annotation);
    }
}
