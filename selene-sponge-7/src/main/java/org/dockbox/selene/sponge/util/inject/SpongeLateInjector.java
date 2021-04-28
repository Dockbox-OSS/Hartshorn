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

package org.dockbox.selene.sponge.util.inject;

import org.dockbox.selene.commands.CommandBus;
import org.dockbox.selene.di.InjectConfiguration;
import org.dockbox.selene.discord.DiscordUtils;
import org.dockbox.selene.sponge.util.SpongeDiscordUtils;
import org.dockbox.selene.sponge.util.command.SpongeCommandBus;

public class SpongeLateInjector extends InjectConfiguration {

    @SuppressWarnings("OverlyCoupledMethod")
    @Override
    public final void collect() {
        // Internal services
        // Event- and command bus keep static references, and can thus be recreated
        this.bind(CommandBus.class, new SpongeCommandBus());
        // Discord
        this.bind(DiscordUtils.class, SpongeDiscordUtils.class);
    }
}
