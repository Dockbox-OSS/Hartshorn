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

package org.dockbox.selene.oldplots;

import org.dockbox.selene.core.ConstructionUtil;
import org.dockbox.selene.core.SeleneUtils;
import org.dockbox.selene.core.annotations.command.Command;
import org.dockbox.selene.core.annotations.extension.Extension;
import org.dockbox.selene.core.command.context.CommandContext;
import org.dockbox.selene.core.files.ConfigurateManager;
import org.dockbox.selene.core.objects.player.Player;
import org.dockbox.selene.core.server.Selene;
import org.dockbox.selene.core.text.Text;
import org.dockbox.selene.core.text.actions.ClickAction;
import org.dockbox.selene.core.text.actions.HoverAction;
import org.dockbox.selene.integrated.data.table.Table;
import org.dockbox.selene.integrated.sql.dialects.sqlite.SQLiteMan;
import org.dockbox.selene.integrated.sql.dialects.sqlite.SQLitePathProperty;
import org.dockbox.selene.integrated.sql.properties.SQLColumnProperty;

import java.nio.file.Path;
import java.util.List;

@Extension(id = "oldplots", name = "OldPlots",
           description = "Provides a easy way to interact with old plot worlds and registrations",
           authors = "GuusLieben", uniqueId = "aa4a7056-8cb3-48f0-b196-a4601eceeb5b")
public class OldPlotsExtension {

    @Command(aliases = {"oldplots", "olp"}, usage = "oldplots <player{Player}>", permission = "selene.oldplots.list")
    public void oldPlotsCommand(Player source, CommandContext ctx) throws Throwable {
        if (!ctx.hasArgument("player")) source.sendWithPrefix("$4No valid player provided!");
        Player player = ctx.getArgument("player", Player.class).rethrow().get().getValue();
        SQLiteMan man = new SQLiteMan();
        Path path = Selene.getInstance(ConfigurateManager.class)
                .getDataDir(SeleneUtils.getExtension(OldPlotsExtension.class));

        if (man.canEnable()) {
            man.stateEnabling(
                    new SQLitePathProperty(path),
                    new SQLColumnProperty("id", OldPlotsIdentifiers.PLOT_ID),
                    new SQLColumnProperty("plot_id_x", OldPlotsIdentifiers.PLOT_X),
                    new SQLColumnProperty("plot_id_z", OldPlotsIdentifiers.PLOT_Z),
                    new SQLColumnProperty("owner", OldPlotsIdentifiers.UUID),
                    new SQLColumnProperty("world", OldPlotsIdentifiers.WORLD)
            );
        }

        Table plots = man.getTable("plot");
        plots = plots.where(OldPlotsIdentifiers.UUID, source.getUniqueId().toString());

        List<Text> plotContent = SeleneUtils.emptyList();
        plots.forEach(row -> {
            // ID, world, x,y
            Text plotLine = Text.of("$3 - $2#{0} : $1{1}$2, $1{2},{3}");
            // ID
            plotLine.onClick(ClickAction.runCommand("/oldplots teleport {0}"));
            // World, x,y
            plotLine.onHover(HoverAction.showText(Text.of("$1Teleport to $2{0}, {1};{2}")));
            plotContent.add(plotLine);
        });

        Selene.getInstance(ConstructionUtil.class)
                .paginationBuilder()
                .content(plotContent)
                // Player name
                .title(Text.of("$1OldPlots for {0}"))
                .build()
                .send(source);
    }

}
