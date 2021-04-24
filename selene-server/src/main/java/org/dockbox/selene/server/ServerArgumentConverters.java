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

package org.dockbox.selene.server;

import org.dockbox.selene.api.Selene;
import org.dockbox.selene.api.module.ModuleContainer;
import org.dockbox.selene.api.module.ModuleManager;
import org.dockbox.selene.commands.annotations.ArgumentProvider;
import org.dockbox.selene.commands.context.ArgumentConverter;
import org.dockbox.selene.commands.convert.CommandValueConverter;
import org.dockbox.selene.di.Provider;
import org.dockbox.selene.di.properties.InjectableType;
import org.dockbox.selene.di.properties.InjectorProperty;

import java.util.stream.Collectors;

@SuppressWarnings({ "unused", "ClassWithTooManyFields" })
@ArgumentProvider
public class ServerArgumentConverters  implements InjectableType {

    public static final ArgumentConverter<ModuleContainer> MODULE = new CommandValueConverter<>(ModuleContainer.class, in -> Provider.provide(ModuleManager.class)
            .getContainer(in), in ->
            Provider.provide(ModuleManager.class).getRegisteredModuleIds().stream()
                    .filter(id -> id.toLowerCase().contains(in.toLowerCase()))
                    .collect(Collectors.toList()),
            "module");

    @Override
    public void stateEnabling(InjectorProperty<?>... properties) {
        Selene.log().info("Registered server module command argument converters.");
    }
}
