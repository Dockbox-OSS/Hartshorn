/*
 * Copyright (C) 2020 Guus Lieben
 *
 * This framework is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.hartshorn.core.boot;

import org.dockbox.hartshorn.core.MetaProvider;
import org.dockbox.hartshorn.core.Modifiers;
import org.dockbox.hartshorn.core.InjectConfiguration;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.context.ApplicationEnvironment;
import org.dockbox.hartshorn.core.context.PrefixContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.core.services.ComponentLocator;
import org.dockbox.hartshorn.core.services.ComponentPostProcessor;
import org.dockbox.hartshorn.core.services.ComponentPreProcessor;

import java.lang.annotation.Annotation;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

public interface ApplicationFactory<Self extends ApplicationFactory<Self, C>, C extends ApplicationContext> {

    Self modifiers(Modifiers... modifiers);

    Self modifier(Modifiers modifier);

    Self activator(TypeContext<?> activator);

    Self argument(String argument);

    Self arguments(String... args);

    Self serviceActivator(Annotation annotation);

    Self serviceActivators(Set<Annotation> annotations);

    Self applicationConfigurator(ApplicationConfigurator applicationConfigurator);

    Self applicationProxier(ApplicationProxier applicationProxier);

    Self applicationLogger(ApplicationLogger applicationLogger);

    Self applicationFSProvider(ApplicationFSProvider applicationFSProvider);

    Self applicationEnvironment(BiFunction<PrefixContext, ApplicationManager, ApplicationEnvironment> applicationEnvironment);

    Self componentLocator(Function<ApplicationContext, ComponentLocator> componentLocator);

    Self postProcessor(ComponentPostProcessor<?> postProcessor);

    Self preProcessor(ComponentPreProcessor<?> processor);

    Self metaProvider(Function<ApplicationContext, MetaProvider> metaProvider);

    Self resourceLocator(Function<ApplicationContext, ClasspathResourceLocator> resourceLocator);

    Self exceptionHandler(ExceptionHandler exceptionHandler);

    Self prefixContext(Function<ApplicationManager, PrefixContext> prefixContext);

    Self prefix(String prefix);

    Self prefixes(String... prefixes);

    Self prefixes(Set<String> prefixes);

    Self configuration(InjectConfiguration injectConfiguration);

    Self self();

    C create();
}
