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

package org.dockbox.selene.sponge.util.command.values;

import org.dockbox.selene.core.command.context.ArgumentConverter;
import org.dockbox.selene.core.impl.command.convert.ArgumentConverterRegistry;
import org.dockbox.selene.core.impl.command.values.AbstractArgumentValue;
import org.dockbox.selene.core.server.Selene;
import org.jetbrains.annotations.NonNls;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.text.Text;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class SpongeArgumentValue extends AbstractArgumentValue<CommandElement>
{

    public SpongeArgumentValue(String type, String permission, String key)
            throws IllegalArgumentException
    {
        super(permission, key, type);
        if (!ArgumentConverterRegistry.hasConverter(type.toLowerCase()))
        {
            try
            {
                Class<?> clazz = Class.forName(type);
                if (clazz.isEnum())
                {
                    Class<? extends Enum> enumType = (Class<? extends Enum>) clazz;
                    this.setValue(GenericArguments.enumValue(Text.of(key), enumType));
                }
                else //noinspection ThrowCaughtLocally
                    throw new IllegalArgumentException("Type '" + type.toLowerCase() + "' is not supported");
            }
            catch (Exception e)
            {
                Selene.handle("No argument of type `" + type + "` can be read", e);
            }
        }
    }

    @Override
    protected CommandElement parseValue(ArgumentConverter<?> converter, String key, @NonNls String type)
    {
        if ("remaining".equals(type) || "remainingstring".equals(type))
        {
            return GenericArguments.remainingJoinedStrings(Text.of(key));
        }
        return new SeleneConverterElement(key, converter);
    }

    @Override
    public SpongeArgumentElement getElement()
    {
        return new SpongeArgumentElement(super.getValue());
    }
}
