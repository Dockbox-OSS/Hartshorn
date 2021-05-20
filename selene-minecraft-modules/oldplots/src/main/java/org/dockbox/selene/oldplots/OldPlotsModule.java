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

package org.dockbox.selene.oldplots;

import org.dockbox.selene.api.domain.Exceptional;
import org.dockbox.selene.api.events.annotations.Listener;
import org.dockbox.selene.api.i18n.text.Text;
import org.dockbox.selene.api.i18n.text.actions.HoverAction;
import org.dockbox.selene.api.i18n.text.pagination.PaginationBuilder;
import org.dockbox.selene.api.module.annotations.Module;
import org.dockbox.selene.commands.RunCommandAction;
import org.dockbox.selene.commands.annotations.Command;
import org.dockbox.selene.commands.context.CommandContext;
import org.dockbox.selene.database.SQLMan;
import org.dockbox.selene.database.dialects.sqlite.SQLitePathProperty;
import org.dockbox.selene.database.exceptions.InvalidConnectionException;
import org.dockbox.selene.database.properties.SQLColumnProperty;
import org.dockbox.selene.di.annotations.Wired;
import org.dockbox.selene.di.context.ApplicationContext;
import org.dockbox.selene.domain.table.Table;
import org.dockbox.selene.persistence.FileManager;
import org.dockbox.selene.persistence.FileType;
import org.dockbox.selene.persistence.FileTypeProperty;
import org.dockbox.selene.server.events.ServerUpdateEvent;
import org.dockbox.selene.server.minecraft.dimension.position.Location;
import org.dockbox.selene.server.minecraft.players.Player;
import org.dockbox.selene.util.SeleneUtils;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.List;

@Module(dependencies = "org.dockbox.selene.database")
public class OldPlotsModule {

    @Wired
    private FileManager fileManager;
    @Wired
    private OldPlotsResources resources;
    @Wired
    private ApplicationContext context;

    // Avoid null
    private PlotWorldModelList modelList = new PlotWorldModelList();

    @Listener
    public void on(ServerUpdateEvent event) {
        Path worldConfig = this.fileManager.getConfigFile(OldPlotsModule.class, "worlds");
        this.fileManager.copyDefaultFile("oldplots_worlds.yml", worldConfig);
        Exceptional<PlotWorldModelList> exceptionalList = this.fileManager.read(worldConfig, PlotWorldModelList.class);
        exceptionalList.present(modelList -> this.modelList = modelList);
    }

    @Command(value = "oldplots", arguments = "<player{Player}>", permission = "selene.oldplots.list")
    public void oldPlotsCommand(Player source, CommandContext ctx) throws InvalidConnectionException {
        if (!ctx.has("player")) {
            source.sendWithPrefix(this.resources.getPlayerError());
        }
        Player player = ctx.get("player");

        SQLMan<?> man = this.getSQLMan();
        Table plots = man.getTable("plot");
        plots = plots.where(OldPlotsIdentifiers.UUID, player.getUniqueId().toString());

        List<Text> plotContent = SeleneUtils.emptyList();
        plots.forEach(row -> {
            @NotNull Integer id = row.getValue(OldPlotsIdentifiers.PLOT_ID).get();
            @NotNull Integer idX = row.getValue(OldPlotsIdentifiers.PLOT_X).get();
            @NotNull Integer idZ = row.getValue(OldPlotsIdentifiers.PLOT_Z).get();
            @NonNls
            @NotNull
            String world = row.getValue(OldPlotsIdentifiers.WORLD).get();

            // Only show worlds we can access
            if (this.modelList.getWorld(world).present()) {
                Text plotLine =
                        Text.of(this.resources.getSinglePlotListItem(world, idX, idZ).translate(player));
                plotLine.onClick(RunCommandAction.runCommand("/optp " + id));
                plotLine.onHover(
                        HoverAction.showText(
                                Text.of(
                                        this.resources.getSinglePlotListItemHover(world, idX, idZ).translate(player))));
                plotContent.add(plotLine);
            }
        });

        this.context.get(PaginationBuilder.class)
                .content(plotContent)
                .title(Text.of(this.resources.getListTitle(player.getName()).translate(player)))
                .build()
                .send(source);
    }

    private SQLMan<?> getSQLMan() {
        Path dataDirectory = this.context.get(FileManager.class).getDataDir(OldPlotsModule.class);
        Path path = dataDirectory.resolve("oldplots.db");

        return this.context.get(SQLMan.class,
                FileTypeProperty.of(FileType.SQLITE),
                new SQLitePathProperty(path),
                new SQLColumnProperty("id", OldPlotsIdentifiers.PLOT_ID),
                new SQLColumnProperty("plot_id_x", OldPlotsIdentifiers.PLOT_X),
                new SQLColumnProperty("plot_id_z", OldPlotsIdentifiers.PLOT_Z),
                new SQLColumnProperty("owner", OldPlotsIdentifiers.UUID),
                new SQLColumnProperty("world", OldPlotsIdentifiers.WORLD));
    }

    @Command(value = "optp", arguments = "<id{Int}>", permission = "selene.oldplots.teleport")
    public void teleportCommand(Player source, CommandContext context)
            throws InvalidConnectionException {
        Integer id = context.get("id");
        SQLMan<?> man = this.getSQLMan();
        Table plots = man.getTable("plot");
        plots = plots.where(OldPlotsIdentifiers.PLOT_ID, id);
        plots.first().present(plot -> {
            @NotNull Integer idX = plot.getValue(OldPlotsIdentifiers.PLOT_X).get();
            @NotNull Integer idZ = plot.getValue(OldPlotsIdentifiers.PLOT_Z).get();
            @NonNls
            @NotNull
            String world = plot.getValue(OldPlotsIdentifiers.WORLD).get();

            if ("*".equals(world)) source.send(this.resources.getCaughtError());
            else {
                Exceptional<PlotWorldModel> model = this.modelList.getWorld(world);
                model.present(worldModel -> {
                    Exceptional<Location> location = worldModel.getLocation(idX, idZ);
                    location.present(source::setLocation)
                            .absent(() -> source.send(this.resources.getCalculationError()));
                }).absent(() -> source.send(this.resources.getLocationError(world)));
            }
        }).absent(() -> source.send(this.resources.getPlotError()));
    }
}
