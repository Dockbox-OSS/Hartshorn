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

package org.dockbox.hartshorn.sponge.util.command;

import org.dockbox.hartshorn.api.Hartshorn;
import org.dockbox.hartshorn.api.events.annotations.Listener;
import org.dockbox.hartshorn.api.exceptions.Except;
import org.dockbox.hartshorn.commands.CommandGateway;
import org.dockbox.hartshorn.commands.SimpleCommandGateway;
import org.dockbox.hartshorn.commands.events.RegisteredCommandsEvent;
import org.dockbox.hartshorn.commands.exceptions.ParsingException;
import org.dockbox.hartshorn.di.annotations.Service;
import org.dockbox.hartshorn.sponge.Sponge7Application;
import org.dockbox.hartshorn.sponge.util.SpongeConversionUtil;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

import java.util.Collection;

@Service
public class SpongeCommandService {

    @Listener
    public void on(RegisteredCommandsEvent event) {
        for (String alias : SimpleCommandGateway.contexts().keySet()) {
            CommandSpec spec = CommandSpec.builder()
                    .arguments(new HartshornCommandElement(alias))
                    .executor((src, args) -> {
                        final Collection<String> all = args.getAll(HartshornCommandElement.KEY);
                        final String arguments = String.join(" ", all);
                        final String full = alias + ' ' + arguments;

                        try {
                            Hartshorn.context().get(CommandGateway.class).accept(SpongeConversionUtil.fromSponge(src).orNull(), full);
                        }
                        catch (ParsingException e) {
                            Except.handle(e);
                            throw new CommandException(Text.of("Could not finish command"), e);
                        }
                        return CommandResult.success();
                    })
                    .build();

            Sponge.getCommandManager().register(Sponge7Application.container(), spec, alias);
            Hartshorn.log().info("Registered /" + alias);
        }
    }

}
