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

import com.google.inject.Inject;

import org.dockbox.selene.api.annotations.command.Command;
import org.dockbox.selene.api.annotations.event.Listener;
import org.dockbox.selene.api.annotations.module.Module;
import org.dockbox.selene.api.command.context.CommandContext;
import org.dockbox.selene.api.events.server.ServerReloadEvent;
import org.dockbox.selene.api.events.server.ServerStartedEvent;
import org.dockbox.selene.api.files.FileManager;
import org.dockbox.selene.api.files.FileType;
import org.dockbox.selene.api.files.FileTypeProperty;
import org.dockbox.selene.api.objects.Exceptional;
import org.dockbox.selene.api.objects.location.position.Location;
import org.dockbox.selene.api.objects.player.Player;
import org.dockbox.selene.api.server.Selene;
import org.dockbox.selene.api.text.Text;
import org.dockbox.selene.api.text.actions.ClickAction;
import org.dockbox.selene.api.text.actions.HoverAction;
import org.dockbox.selene.api.text.pagination.PaginationBuilder;
import org.dockbox.selene.api.util.SeleneUtils;
import org.dockbox.selene.database.SQLMan;
import org.dockbox.selene.database.dialects.sqlite.SQLitePathProperty;
import org.dockbox.selene.database.exceptions.InvalidConnectionException;
import org.dockbox.selene.database.properties.SQLColumnProperty;
import org.dockbox.selene.structures.table.Table;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.List;

@Module(
        id = "oldplots",
        name = "OldPlots",
        description = "Provides a easy way to interact with old plot worlds and registrations",
        authors = "GuusLieben",
        dependencies = "org.dockbox.selene.database")
public class OldPlotsModule {

    @Inject
    private FileManager fileManager;

    // Avoid null
    private PlotWorldModelList modelList = new PlotWorldModelList();

    @Listener
    public void onLoad(ServerStartedEvent serverStartedEvent, ServerReloadEvent reloadEvent) {
        Path worldConfig = this.fileManager.getConfigFile(OldPlotsModule.class, "worlds");
        this.fileManager.copyDefaultFile("oldplots_worlds.yml", worldConfig);
        Exceptional<PlotWorldModelList> exceptionalList = this.fileManager.read(worldConfig, PlotWorldModelList.class);
        exceptionalList.present(modelList -> this.modelList = modelList);
    }

    @Command(aliases = "oldplots", usage = "oldplots <player{Player}>", permission = "selene.oldplots.list")
    public void oldPlotsCommand(Player source, CommandContext ctx) throws InvalidConnectionException {
        if (!ctx.has("player")) {
            source.sendWithPrefix(OldPlotsResources.ERROR_NO_PLAYER);
        }
        Player player = ctx.get("player");

        SQLMan<?> man = OldPlotsModule.getSQLMan();
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
                        Text.of(OldPlotsResources.SINGLE_PLOT.format(world, idX, idZ).translate(player));
                plotLine.onClick(ClickAction.runCommand("/optp " + id));
                plotLine.onHover(
                        HoverAction.showText(
                                Text.of(
                                        OldPlotsResources.PLOT_HOVER.format(world, idX, idZ).translate(player))));
                plotContent.add(plotLine);
            }
        });

        Selene.provide(PaginationBuilder.class)
                .content(plotContent)
                .title(Text.of(OldPlotsResources.LIST_TITLE.format(player.getName()).translate(player)))
                .build()
                .send(source);
    }

    private static SQLMan<?> getSQLMan() {
        Path dataDirectory = Selene.provide(FileManager.class).getDataDir(OldPlotsModule.class);
        Path path = dataDirectory.resolve("oldplots.db");

        return Selene.provide(SQLMan.class,
                FileTypeProperty.of(FileType.SQLITE),
                new SQLitePathProperty(path),
                new SQLColumnProperty("id", OldPlotsIdentifiers.PLOT_ID),
                new SQLColumnProperty("plot_id_x", OldPlotsIdentifiers.PLOT_X),
                new SQLColumnProperty("plot_id_z", OldPlotsIdentifiers.PLOT_Z),
                new SQLColumnProperty("owner", OldPlotsIdentifiers.UUID),
                new SQLColumnProperty("world", OldPlotsIdentifiers.WORLD));
    }

    @Command(aliases = "optp", usage = "teleport <id{Int}>", permission = "selene.oldplots.teleport")
    public void teleportCommand(Player source, CommandContext context)
            throws InvalidConnectionException {
        Integer id = context.get("id");
        SQLMan<?> man = OldPlotsModule.getSQLMan();
        Table plots = man.getTable("plot");
        plots = plots.where(OldPlotsIdentifiers.PLOT_ID, id);
        plots.first().present(plot -> {
            @NotNull Integer idX = plot.getValue(OldPlotsIdentifiers.PLOT_X).get();
            @NotNull Integer idZ = plot.getValue(OldPlotsIdentifiers.PLOT_Z).get();
            @NonNls
            @NotNull
            String world = plot.getValue(OldPlotsIdentifiers.WORLD).get();

            if ("*".equals(world)) source.send(OldPlotsResources.ERROR_WORLDS);
            else {
                Exceptional<PlotWorldModel> model = this.modelList.getWorld(world);
                model.present(worldModel -> {
                    Exceptional<Location> location = worldModel.getLocation(idX, idZ);
                    location.present(source::setLocation)
                            .absent(() -> source.send(OldPlotsResources.ERROR_CALCULATION));
                }).absent(() -> source.send(OldPlotsResources.ERROR_NO_LOCATION.format(world)));
            }
        }).absent(() -> source.send(OldPlotsResources.ERROR_NO_PLOT));
    }
}
