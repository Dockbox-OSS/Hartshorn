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

package org.dockbox.selene.test.util;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.TextChannel;

import org.dockbox.selene.core.impl.DefaultDiscordUtils;
import org.dockbox.selene.core.objects.Exceptional;

/**
 Provides empty {@link Exceptional} instances, as testing with the {@link JDA} is seemingly impossible without providing
 a Discord server and bot for this purpose.
 */
public class TestDiscordUtils extends DefaultDiscordUtils {

    @Override
    public Exceptional<JDA> getJDA() {
        return Exceptional.empty();
    }

    @Override
    public Exceptional<TextChannel> getGlobalTextChannel() {
        return Exceptional.empty();
    }
}
