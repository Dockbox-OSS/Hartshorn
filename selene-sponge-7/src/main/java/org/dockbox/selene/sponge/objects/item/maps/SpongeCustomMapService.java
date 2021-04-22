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

package org.dockbox.selene.sponge.objects.item.maps;

import org.dockbox.selene.api.Players;
import org.dockbox.selene.api.files.FileManager;
import org.dockbox.selene.api.files.FileType;
import org.dockbox.selene.common.files.FileTypeProperty;
import org.dockbox.selene.api.objects.Console;
import org.dockbox.selene.api.domain.Exceptional;
import org.dockbox.selene.server.minecraft.item.Item;
import org.dockbox.selene.minecraft.item.maps.CustomMap;
import org.dockbox.selene.api.objects.targets.Identifiable;
import org.dockbox.selene.api.server.Selene;
import org.dockbox.selene.server.minecraft.item.DefaultCustomMapService;
import org.dockbox.selene.database.SQLMan;
import org.dockbox.selene.database.dialects.sqlite.SQLitePathProperty;
import org.dockbox.selene.database.exceptions.InvalidConnectionException;
import org.dockbox.selene.database.exceptions.NoSuchTableException;
import org.dockbox.selene.nms.maps.NMSMapUtils;
import org.dockbox.selene.api.domain.table.Table;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;

import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;

public class SpongeCustomMapService extends DefaultCustomMapService {

    private static final String TABLE = "uploaders";
    private static final int MAP_COLOR = 16744576;

    public SpongeCustomMapService() {}

    @Override
    public CustomMap create(BufferedImage image, Identifiable source) {
        int mapId = NMSMapUtils.populateColoredMap(image);
        registerOwner(mapId, source.getUniqueId());
        return createCombinedMap(mapId, source);
    }

    @Override
    public CustomMap create(byte[] image, Identifiable source) {
        int mapId = NMSMapUtils.populateColoredMap(image);
        registerOwner(mapId, source.getUniqueId());
        return createCombinedMap(mapId, source);
    }

    @Override
    public CustomMap getById(int id) {
        return createCombinedMap(id, lookupSource(id));
    }

    @Override
    public Collection<CustomMap> getFrom(Identifiable source) {
        return getHistoryTable().where(MapIdentifiers.SOURCE, source.getUniqueId().toString()).getRows()
                .stream()
                .map(row -> row.getValue(MapIdentifiers.MAP))
                .filter(Exceptional::present)
                .map(Exceptional::get)
                .map(mapId -> createCombinedMap(mapId, source))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public Exceptional<CustomMap> derive(Item item) {
        if (item instanceof CustomMap) {
            return Exceptional.of((CustomMap) item);
        }
        else if (item.getId().equals(Selene.getItems().getFilledMap().getId())) {
            return Exceptional.of(this.getById(item.getMeta()));
        }
        return Exceptional.none();
    }

    private static Identifiable lookupSource(int mapId) {
        return getHistoryTable()
                .where(MapIdentifiers.MAP, mapId)
                .first()
                .map(row -> row.getValue(MapIdentifiers.SOURCE).or(Console.UNIQUE_ID.toString()))
                .map(uniqueId ->
                        Selene.provide(Players.class)
                                .getPlayer(UUID.fromString(uniqueId))
                                .orNull())
                .map(Identifiable.class::cast)
                .or(Console.getInstance());
    }

    private static void registerOwner(int mapId, UUID owner) {
        Table history = getHistoryTable();
        history.addRow(mapId, owner.toString());
        store(history);
    }

    private static CustomMap createCombinedMap(int mapId, Identifiable source) {
        ItemStack stack = createItemStack(mapId);
        return new SpongeCustomMap(stack, source, mapId);
    }

    private static Table getHistoryTable() {
        try {
            SQLMan<?> sql = Selene.provide(SQLMan.class, FileTypeProperty.of(FileType.SQLITE),
                    new SQLitePathProperty(getHistoryStorePath()));
            return sql.getOrCreateTable(TABLE, SpongeCustomMapService.getEmptyTable());
        }
        catch (InvalidConnectionException | NoSuchTableException e) {
            Selene.handle(e);
            return SpongeCustomMapService.getEmptyTable();
        }
    }

    private static void store(Table table) {
        try {
            SQLMan<?> sql = Selene.provide(SQLMan.class, FileTypeProperty.of(FileType.SQLITE),
                    new SQLitePathProperty(getHistoryStorePath()));
            sql.store(TABLE, table);
        }
        catch (InvalidConnectionException e) {
            Selene.handle(e);
        }
    }

    private static ItemStack createItemStack(int mapId) {
        ItemStack stack = ItemStack.builder().itemType(ItemTypes.FILLED_MAP).quantity(1).build();
        DataView rawData = stack.toContainer();
        rawData.set(DataQuery.of("UnsafeDamage"), mapId);
        rawData.set(DataQuery.of("UnsafeData", "display", "LocName"), "item.painting.name");
        rawData.set(DataQuery.of("UnsafeData", "display", "MapColor"), MAP_COLOR);

        stack = ItemStack.builder().fromContainer(rawData).build();
        return stack;
    }

    private static Path getHistoryStorePath() {
        FileManager fileManager = Selene.provide(FileManager.class);
        return fileManager.getDataFile(Selene.class, "maps.db");
    }

    private static Table getEmptyTable() {
        return new Table(MapIdentifiers.MAP, MapIdentifiers.SOURCE);
    }
}
