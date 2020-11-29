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

package org.dockbox.selene.coulis;

import com.boydti.fawe.object.FawePlayer;
import com.sk89q.worldedit.regions.Region;

import org.dockbox.selene.core.annotations.Command;
import org.dockbox.selene.core.annotations.Placeholder;
import org.dockbox.selene.core.command.context.CommandContext;
import org.dockbox.selene.core.impl.command.convert.TypeArgumentParsers.ListParser;
import org.dockbox.selene.core.objects.optional.Exceptional;
import org.dockbox.selene.core.objects.user.Player;

import java.util.List;

@Placeholder(description = "Gradient command extension", by = "GuusLieben", assignee = "simbolduc/coulis")
public class GradientExtension {

    @Command(aliases = "/gradient", usage = "/gradient <blocks{String}> [<from{String}> <to{String}>]")
    public void run(Player src, CommandContext ctx) {
        if (!ctx.hasArgument("blocks")) return;

        List<? extends Integer> blocks = ctx.getArgumentAndParse("blocks",
                new ListParser<>(Integer::parseInt))
                .get();

        Exceptional<FawePlayer<?>> ofp = src.getFawePlayer();
        Region region = ofp.map(FawePlayer::getSelection).orNull();

        if (null == region || null == region.getWorld()) {
            src.send("$4Invalid region");
            return;
        }

        //,,,

    }

}
