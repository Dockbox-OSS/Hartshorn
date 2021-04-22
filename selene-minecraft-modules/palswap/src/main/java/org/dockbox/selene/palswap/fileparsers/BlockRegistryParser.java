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

package org.dockbox.selene.palswap.fileparsers;

import org.dockbox.selene.api.Selene;
import org.dockbox.selene.api.domain.Exceptional;
import org.dockbox.selene.minecraft.item.Item;
import org.dockbox.selene.minecraft.item.storage.MinecraftItems;
import org.dockbox.selene.palswap.BlockRegistryModule;
import org.dockbox.selene.persistence.FileManager;
import org.dockbox.selene.server.minecraft.MinecraftVersion;
import org.dockbox.selene.util.SeleneUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class BlockRegistryParser {

    //TODO: add command updates ItemData
    protected Map<String, String> itemRegistry;
    protected Map<String, String> blockIdentifierIDs;

    @NotNull
    protected List<String> parseFile(String filename) {
        try {
            Exceptional<Path> ePath = Selene.getResourceFile(filename);
            if (ePath.absent()) {
                Selene.log().info("Can't find " + filename);
            } else {
                return Files.lines(ePath.get()).collect(Collectors.toList());
            }
        } catch (IOException e) {
            Selene.handle(e);
        }
        return new ArrayList<>();
    }

    public List<String> loadItemRawIDs(String minecraftFilename, String conquestFilename) {
        List<String> items = this.loadMinecraftItemRawIDs(minecraftFilename);
        items.addAll(this.loadConquestItemRawIDs(conquestFilename));

        return items;
    }

    public List<String> generateBlockIdentifiers(List<String> rawIDs) {
        List<String> blockIdentifiers = SeleneUtils.emptyList();

        for (String rawID : rawIDs) {
            Item item = this.getItemFromRawID(rawID);

            Exceptional<String> eBlockIdentifier = this.determineBlockIdentifier(item);
            if (eBlockIdentifier.absent()) continue;

            String id = this.getItemID(item);
            String blockIdentifier = eBlockIdentifier.get();
            this.blockIdentifierIDs.put(id, blockIdentifier);

            if (!blockIdentifiers.contains(blockIdentifier)) {
                blockIdentifiers.add(blockIdentifier);
            }
            this.registerItem(rawID, item);
        }
        return blockIdentifiers;
    }

    public String generateBlockIdentifierEnumFile(List<String> blockIdentifiers) {
        StringBuilder blockIdentifierEnum = new StringBuilder();
        for (String blockIdentifier : blockIdentifiers) {
            blockIdentifierEnum.append(blockIdentifier).append(",\n");
        }
        return blockIdentifierEnum.toString();
    }

    public String generateBlockIdentifierEnumFile(String minecraftFilename, String conquestFilename) {
        return this.generateBlockIdentifierEnumFile(
                this.generateBlockIdentifiers(
                        this.loadItemRawIDs(minecraftFilename, conquestFilename))
        );
    }

    public void SaveItemData(String filename) {
        FileManager fm = Selene.provide(FileManager.class);
        Path file = fm.getDataFile(BlockRegistryModule.class, filename);
        fm.write(file, ItemData.of(this.itemRegistry, this.blockIdentifierIDs));
    }

    public void LoadItemData(String filename) {
        if (null == this.itemRegistry || null == this.blockIdentifierIDs) {
            FileManager fm = Selene.provide(FileManager.class);
            Path file = fm.getDataFile(BlockRegistryModule.class, filename);

            ItemData itemData = fm.read(file, ItemData.class).or(new ItemData());
            this.itemRegistry = itemData.getItemRegistry();
            this.blockIdentifierIDs = itemData.getBlockIdentifierIDs();
        }

        for (String rawID : this.itemRegistry.keySet()) {
            Item item = this.getItemFromRawID(rawID);
            String id = this.itemRegistry.get(rawID);

            MinecraftItems.getInstance().registerCustom(id, () -> item);
        }
    }

    /**
     * Registers custom ID for item to Selene and stores it in the itemRegistry so that its
     * automatically registered whenever the server starts from then on.
     *
     * @param rawID
     *         The raw id of the {@link Item} being registered
     * @param item
     *         The {@link Item} to be registered to Selene
     */
    public void registerItem(String rawID, Item item) {
        String id = this.getItemID(item);
        this.itemRegistry.put(rawID, id);

        MinecraftItems.getInstance().registerCustom(id, () -> item);
    }

    public abstract MinecraftVersion getMinecraftVersion();

    protected abstract List<String> loadMinecraftItemRawIDs(String filename);

    protected abstract List<String> loadConquestItemRawIDs(String filename);

    public abstract Exceptional<String> determineBlockIdentifier(Item item);

    public abstract String getItemID(Item item);

    public abstract Item getItemFromRawID(String rawID);
}
