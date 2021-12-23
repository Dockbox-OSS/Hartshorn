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

package org.dockbox.hartshorn.core.annotations.activate;

import org.dockbox.hartshorn.core.ActivatorFiltered;
import org.dockbox.hartshorn.core.annotations.stereotype.Service;
import org.dockbox.hartshorn.core.boot.Hartshorn;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.services.ComponentProcessor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to mark annotations as service activators. Service activators indicate a specific
 * {@link Service}, {@link ComponentProcessor}, or {@link ActivatorFiltered} is allowed to become
 * active.
 *
 * <p>Service activators always need to be annotated with {@link ServiceActivator}. If an annotation
 * is used as activator, but is not annotated with {@link ServiceActivator}, it will be rejected by
 * the active {@link ApplicationContext}.
 *
 * <p>Service activators offer a way to specify {@link #scanPackages() base packages} which become
 * active when the activator is present on the application activator. These packages will be scanned
 * when the application is initializing.
 *
 * @author Guus Lieben
 * @since 4.1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface ServiceActivator {

    /**
     * The base packages to scan for services if this service activator is present. Defaults to the
     * default {@link Hartshorn#PACKAGE_PREFIX Hartshorn prefix}. If the package has already been
     * processed before, it is up to the active {@link ApplicationContext} to decide whether to
     * process it again.
     *
     * @return The base packages to scan for services.
     */
    String[] scanPackages() default { Hartshorn.PACKAGE_PREFIX };
}
