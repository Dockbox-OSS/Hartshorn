/*
 *  Copyright (C) 2020 Guus Lieben
 *
 *  This framework is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as
 *  published by the Free Software Foundation, either version 2.1 of the
 *  License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 *  the GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.hartshorn.core.boot.beta;

import org.dockbox.hartshorn.core.InjectConfiguration;
import org.dockbox.hartshorn.core.Modifier;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;

import java.lang.annotation.Annotation;
import java.util.Set;

public interface ApplicationFactory<Self extends ApplicationFactory<Self, C>, C extends ApplicationContext> {

    Self modifiers(Modifier... modifiers);

    Self modifier(Modifier modifier);

    Self activator(TypeContext<?> activator);

    Self argument(String argument);

    Self arguments(String... args);

    Self serviceActivator(Annotation annotation);

    Self serviceActivators(Set<Annotation> annotations);

    Self applicationConfigurator(ApplicationConfigurator applicationConfigurator);

    Self applicationProxier(ApplicationProxier applicationProxier);

    Self applicationLogger(ApplicationLogger applicationLogger);

    Self prefix(String prefix);

    Self prefixes(String... prefixes);

    Self prefixes(Set<String> prefixes);

    Self configuration(InjectConfiguration injectConfiguration);

    Self self();

    C create();
}
