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

package org.dockbox.hartshorn.demo.persistence;

import org.dockbox.hartshorn.core.annotations.activate.ServiceActivator;
import org.dockbox.hartshorn.core.boot.HartshornApplication;
import org.dockbox.hartshorn.commands.annotations.UseCommands;
import org.dockbox.hartshorn.core.annotations.activate.Activator;
import org.dockbox.hartshorn.core.annotations.activate.UseServiceProvision;
import org.dockbox.hartshorn.core.services.ServicePreProcessor;

/**
 * A simple application starter, with specific {@link ServiceActivator service activators}
 * configured to enable only the required {@link ServicePreProcessor service processors}.
 * <p>For readability, each activator has been documented with a short description below.
 */

/* Activates the CommandServiceScanner, enabling command handling and event listening, also activates
   the ConfigurationServiceProcessor and ConfigurationServiceModifier, pre-loading configurations and
   injecting values, respectively */
@UseCommands
/* Activates the ProviderServiceProcessor, enabling custom provisioning with @Provider */
@UseServiceProvision

/*
* Indicates this type is an application activator, providing required metadata for your application. In this example only the
* bootstrap is indicated, ProxyApplicationBootstrap is the default recommended bootstrap, but it is possible to override functionality
* and use your own implementation here directly
*/
@Activator
public final class PersistenceDemoApplication {

    public static void main(final String[] args) {
        HartshornApplication.create(PersistenceDemoApplication.class, args);
    }
}
