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

package org.dockbox.selene.commands.registration;

import org.dockbox.selene.api.domain.Exceptional;
import org.dockbox.selene.api.domain.Identifiable;
import org.dockbox.selene.api.domain.OwnerLookup;
import org.dockbox.selene.api.domain.TypedOwner;
import org.dockbox.selene.api.i18n.common.ResourceEntry;
import org.dockbox.selene.commands.annotations.Command;
import org.dockbox.selene.commands.context.CommandContext;
import org.dockbox.selene.commands.source.CommandSource;
import org.dockbox.selene.di.Provider;
import org.dockbox.selene.util.SeleneUtils;

import java.util.List;
import java.util.UUID;

import lombok.Getter;

@Getter
public abstract class AbstractRegistrationContext {

    private final List<String> aliases = SeleneUtils.emptyConcurrentList();
    private final Command command;
    private final TypedOwner owner;

    protected AbstractRegistrationContext(Command command, Class<?> owner) {
        this.command = command;
        for (String alias : command.value()) this.addAlias(alias);
        this.owner = Provider.provide(OwnerLookup.class).lookup(owner);
    }

    public void addAlias(String alias) {
        if (null != alias) this.aliases.add(alias);
    }

    public static String getRegistrationId(Identifiable sender, CommandContext ctx) {
        UUID uuid = sender.getUniqueId();
        String alias = ctx.alias();
        return uuid + "$" + alias;
    }

    public abstract Exceptional<ResourceEntry> call(CommandSource source, CommandContext context);

    public String getPrimaryAlias() {
        return this.getAliases().get(0);
    }
}
