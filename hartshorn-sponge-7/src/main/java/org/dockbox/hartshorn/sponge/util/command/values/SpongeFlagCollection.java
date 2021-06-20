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

import org.dockbox.hartshorn.commands.values.AbstractArgumentElement;
import org.dockbox.hartshorn.commands.values.AbstractFlagCollection;
import org.dockbox.hartshorn.util.HartshornUtils;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.command.args.CommandFlags;
import org.spongepowered.api.command.args.CommandFlags.Builder;
import org.spongepowered.api.command.args.GenericArguments;

import java.util.List;

public class SpongeFlagCollection extends AbstractFlagCollection<CommandFlags.Builder> {

    public SpongeFlagCollection(Builder reference) {
        super(reference);
    }

    public SpongeFlagCollection() {
        super(GenericArguments.flags());
    }

    @Override
    public void addNamedFlag(String name) {
        this.getReference().flag(name);
    }

    @Override
    public void addNamedPermissionFlag(String name, String permission) {
        this.getReference().permissionFlag(permission, name);
    }

    @Override
    public void addValueBasedFlag(String name, org.dockbox.hartshorn.commands.beta.api.CommandElement<?> value) {

    }

    @SuppressWarnings("OverlyStrongTypeCast")
    @Override
    public List<AbstractArgumentElement<?>> buildAndCombines(AbstractArgumentElement<?> element) {
        if (element instanceof SpongeArgumentElement) {
            CommandElement commandElement = this.getReference().buildWith(((SpongeArgumentElement) element).getReference());
            return HartshornUtils.asList(new SpongeArgumentElement(commandElement));
        }
        return HartshornUtils.emptyList();
    }
}
