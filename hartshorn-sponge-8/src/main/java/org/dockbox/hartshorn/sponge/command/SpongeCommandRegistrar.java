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

package org.dockbox.hartshorn.sponge.command;

import net.kyori.adventure.text.Component;

import org.dockbox.hartshorn.api.Hartshorn;
import org.dockbox.hartshorn.api.exceptions.Except;
import org.dockbox.hartshorn.commands.CommandGateway;
import org.dockbox.hartshorn.commands.CommandGatewayImpl;
import org.dockbox.hartshorn.commands.exceptions.ParsingException;
import org.dockbox.hartshorn.sponge.Sponge8Application;
import org.dockbox.hartshorn.sponge.event.EventBridge;
import org.dockbox.hartshorn.sponge.util.SpongeConvert;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.Command.Parameterized;
import org.spongepowered.api.command.CommandCompletion;
import org.spongepowered.api.command.CommandExecutor;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.command.parameter.Parameter.Value;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.lifecycle.RegisterCommandEvent;

import java.util.Optional;

public class SpongeCommandRegistrar implements EventBridge {

    @Listener
    public void onRegisterCommand(final RegisterCommandEvent<Parameterized> event) {
        for (final String alias : CommandGatewayImpl.contexts().keySet()) {
            final Value<String> parameter = this.parameter(alias);

            final Parameterized command = Command.builder()
                    .addParameter(parameter)
                    .executor(this.executor(alias, parameter))
                    .build();

            event.register(Sponge8Application.container(), command, alias);
        }
    }

    private Value<String> parameter(String alias) {
        return Parameter.remainingJoinedStrings().key(alias + "-arguments")
                .completer((ctx, input) -> Hartshorn.context()
                        .get(CommandGateway.class)
                        .suggestions(SpongeConvert.fromSponge(ctx.cause().subject()).orNull(), alias + ' ' + input)
                        .stream()
                        .map(CommandCompletion::of)
                        .toList()
                ).terminal().optional().build();
    }

    private CommandExecutor executor(String alias, Value<String> parameter) {
        return ctx -> {
            final Optional<String> arguments = ctx.one(parameter);
            try {
                Hartshorn.context().get(CommandGateway.class).accept(
                        SpongeConvert.fromSponge(ctx.cause().subject()).orNull(),
                        alias + arguments.map(a -> ' ' + a).orElse("")
                );
            }
            catch (ParsingException e) {
                Except.handle(e);
                throw new CommandException(Component.text(e.getMessage()), e);
            }
            return CommandResult.success();
        };
    }
}
