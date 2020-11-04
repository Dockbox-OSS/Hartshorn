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

package org.dockbox.selene.sponge.util.command;

import org.dockbox.selene.core.impl.command.AbstractArgumentValue;
import org.dockbox.selene.core.impl.command.convert.ArgumentConverter;
import org.dockbox.selene.core.impl.command.convert.impl.DefaultArgumentConverters;
import org.dockbox.selene.core.server.Selene;
import org.dockbox.selene.core.util.SeleneUtils;
import org.dockbox.selene.sponge.util.SpongeConversionUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.text.Text;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"unchecked", "rawtypes"})
public class SpongeArgumentTypeValue extends AbstractArgumentValue<CommandElement> {

    public SpongeArgumentTypeValue(String type, String permission, String key) throws IllegalArgumentException {
        super(DefaultArgumentConverters.getConverter(type.toLowerCase()), permission, key);
        if (!DefaultArgumentConverters.hasConverter(type.toLowerCase())) {
            try {
                Class<?> clazz = Class.forName(type);
                if (clazz.isEnum()) {
                    Class<? extends Enum> enumType = (Class<? extends Enum>) clazz;
                    this.setElement(GenericArguments.enumValue(Text.of(key), enumType));
                } else throw new IllegalArgumentException("Type '" + type.toLowerCase() + "' is not supported");
            } catch (Exception e) {
                Selene.getServer().except("No argument of type `" + type + "` can be read", e);
            }
        }
    }

    @Nullable
    @Override
    protected CommandElement parseArgument(ArgumentConverter<?> argument, String key) {
        return new SeleneConverterElement(key, argument);
    }

    public CommandElement getArgument() {
        return null == this.getPermission() ?
                super.getElement()
                : GenericArguments.requiringPermission(this.getElement(), this.getPermission());
    }

    private static final class SeleneConverterElement extends CommandElement {

        private final ArgumentConverter<?> argument;

        private SeleneConverterElement(String key, ArgumentConverter<?> argument) {
            super(Text.of(key));
            this.argument = argument;
        }

        @Nullable
        @Override
        protected Object parseValue(@NotNull CommandSource source, CommandArgs args) throws ArgumentParseException {
            return this.argument.convert(
                    SpongeConversionUtil.fromSponge(source).get(),
                    args.next()
            ).orElse(null);
        }

        @Override
        public List<String> complete(@NotNull CommandSource src, CommandArgs args, @NotNull CommandContext context) {
            try {
                return new ArrayList<>(this.argument.getSuggestions(
                        SpongeConversionUtil.fromSponge(src).get(),
                        args.next()
                ));
            } catch (ArgumentParseException e) {
                return SeleneUtils.emptyList();
            }
        }
    }
}
