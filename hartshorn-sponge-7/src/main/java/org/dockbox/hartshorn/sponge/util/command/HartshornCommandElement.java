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
import org.dockbox.hartshorn.commands.CommandGateway;
import org.dockbox.hartshorn.sponge.util.SpongeConversionUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.text.Text;

import java.util.List;

public class HartshornCommandElement extends CommandElement {

    public static final Text KEY = Text.of("HartshornCE");
    private final String command;

    protected HartshornCommandElement(String command) {
        super(KEY);
        this.command = command;
    }

    @Nullable
    @Override
    protected Object parseValue(@NotNull CommandSource source, @NotNull CommandArgs args) throws ArgumentParseException {
        args.next();
        String ret = args.getRaw().substring(args.getRawPosition());
        while (args.hasNext()) { // Consume remaining args
            args.next();
        }
        return ret;
    }

    @NotNull
    @Override
    public List<String> complete(@NotNull CommandSource src, @NotNull CommandArgs args, @NotNull CommandContext context) {
        return Hartshorn.context().get(CommandGateway.class).suggestions(
                SpongeConversionUtil.fromSponge(src).orNull(),
                this.full(args)
        );
    }

    private String full(CommandArgs args) {
        final String arguments = String.join(" ", args.getAll());
        return this.command + ' ' + arguments;
    }
}
