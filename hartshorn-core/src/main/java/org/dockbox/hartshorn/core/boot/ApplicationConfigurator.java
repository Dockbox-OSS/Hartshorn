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

import org.dockbox.hartshorn.core.InjectConfiguration;
import org.dockbox.hartshorn.core.context.ApplicationContext;

import java.util.Set;

/**
 * The {@link ApplicationConfigurator} is responsible for configuring the {@link ApplicationManager}. Configuration
 * actions can range from setting environment values to overriding internal components. Applications are configured
 * in the {@link ApplicationFactory} after the manager and {@link ApplicationContext} have been created, but before
 * active bindings are added.
 *
 * @author Guus Lieben
 * @since 21.9
 */
public interface ApplicationConfigurator {

    /**
     * Early configuration actions performed after the {@link ApplicationContext} has been created, but before the
     * active bindings are added.
     * @param manager The {@link ApplicationManager} to configure.
     */
    void configure(ApplicationManager manager);

    /**
     * Applies the given {@link InjectConfiguration}s to the given {@link ApplicationManager}. Configurations may be
     * validated, modified, or applied to the {@link ApplicationContext}.
     *
     * @param manager The {@link ApplicationManager} to configure.
     * @param configurations The {@link InjectConfiguration}s to apply.
     */
    void apply(ApplicationManager manager, Set<InjectConfiguration> configurations);

    /**
     * Binds the given prefix to the given {@link ApplicationManager}. Prefixes may be validated, modified, or applied
     * to the {@link ApplicationContext}.
     *
     * @param manager The {@link ApplicationManager} to configure.
     * @param prefix The prefix to bind.
     */
    void bind(ApplicationManager manager, String prefix);
}
