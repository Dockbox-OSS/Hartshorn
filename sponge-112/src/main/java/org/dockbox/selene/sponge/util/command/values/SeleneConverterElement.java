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

package org.dockbox.selene.sponge.util.command.values;

import org.dockbox.selene.core.SeleneUtils;
import org.dockbox.selene.core.impl.command.convert.ArgumentConverter;
import org.dockbox.selene.sponge.util.SpongeConversionUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.text.Text;

import java.util.List;

final class SeleneConverterElement extends CommandElement {

    private final ArgumentConverter<?> argument;

    SeleneConverterElement(String key, ArgumentConverter<?> argument) {
        super(Text.of(key));
        this.argument = argument;
    }

    @Nullable
    @Override
    protected Object parseValue(@NotNull CommandSource source, CommandArgs args) throws ArgumentParseException {
        return this.argument.convert(
                SpongeConversionUtil.fromSponge(source).get(),
                args.next()
        ).orNull();
    }

    @NotNull
    @Override
    public List<String> complete(@NotNull CommandSource src, CommandArgs args, @NotNull CommandContext context) {
        try {
            return SeleneUtils.asList(this.argument.getSuggestions(
                    SpongeConversionUtil.fromSponge(src).get(),
                    args.next()
            ));
        } catch (ArgumentParseException e) {
            return SeleneUtils.emptyList();
        }
    }
}
