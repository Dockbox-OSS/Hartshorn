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

package org.dockbox.selene.dave;

import net.dv8tion.jda.api.entities.TextChannel;

import org.dockbox.selene.core.DiscordUtils;
import org.dockbox.selene.core.annotations.entity.Metadata;
import org.dockbox.selene.core.server.Selene;
import org.dockbox.selene.core.text.Text;

@Metadata(alias = "dave-config")
public class DaveConfig {

    private String channelId = "622795938699673600";
    private Text prefix = Text.of("&6Dave&e: &f");

    public DaveConfig() {
    }

    public TextChannel getChannel() {
        DiscordUtils du = Selene.provide(DiscordUtils.class);
        return du.getJDA().get().getTextChannelById(this.channelId);
    }

    public Text getPrefix() {
        return this.prefix;
    }
}
