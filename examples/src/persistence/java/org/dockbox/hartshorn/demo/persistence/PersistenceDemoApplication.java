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
