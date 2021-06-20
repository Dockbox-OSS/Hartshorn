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

package org.dockbox.hartshorn.sponge.util.command.values;

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.api.i18n.common.ResourceEntry;
import org.dockbox.hartshorn.api.i18n.entry.DefaultResources;
import org.dockbox.hartshorn.commands.context.ArgumentConverter;
import org.dockbox.hartshorn.sponge.util.SpongeConversionUtil;
import org.dockbox.hartshorn.util.HartshornUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.text.Text;

import java.util.List;

@Deprecated
final class ConverterElement extends CommandElement {

    private final ArgumentConverter<?> argument;

    ConverterElement(String key, ArgumentConverter<?> argument) {
        super(Text.of(key));
        this.argument = argument;
    }

    @Nullable
    @Override
    protected Object parseValue(@NotNull CommandSource source, CommandArgs args) throws ArgumentParseException {
        String argument = args.next();
        Exceptional<?> value = this.argument.convert(SpongeConversionUtil.fromSponge(source).get(), argument);
        if (value.caught()) { // So returning null is still permitted
            ResourceEntry errorResource = DefaultResources.instance().getUnknownError(value.error().getMessage());
            throw new ArgumentParseException(SpongeConversionUtil.toSponge(errorResource.asText()), value.error(), argument, 0);
        }
        return value.orNull();
    }

    @NotNull
    @Override
    public List<String> complete(@NotNull CommandSource src, CommandArgs args, @NotNull CommandContext context) {
        try {
            return HartshornUtils.asList(this.argument.suggestions(SpongeConversionUtil.fromSponge(src).get(), args.next()));
        }
        catch (ArgumentParseException e) {
            return HartshornUtils.emptyList();
        }
    }
}
