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

import org.dockbox.hartshorn.commands.context.ArgumentConverter;
import org.dockbox.hartshorn.commands.values.AbstractArgumentValue;
import org.jetbrains.annotations.NonNls;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.text.Text;

public class SpongeArgumentValue extends AbstractArgumentValue<CommandElement> {

    public SpongeArgumentValue(String type, String permission, String key) throws IllegalArgumentException {
        super(permission, key, type);
    }

    @Override
    protected CommandElement parseValue(ArgumentConverter<?> converter, String key, @NonNls String type) {
        if ("remaining".equals(type) || "remainingstring".equals(type)) {
            return GenericArguments.remainingJoinedStrings(Text.of(key));
        }
        return new ConverterElement(key, converter);
    }

    @Override
    protected <E extends Enum<E>> void setEnumType(Class<E> enumType, String key) {
        this.setValue(GenericArguments.enumValue(Text.of(key), enumType));
    }

    @Override
    public SpongeArgumentElement getElement() {
        return new SpongeArgumentElement(super.getValue());
    }
}