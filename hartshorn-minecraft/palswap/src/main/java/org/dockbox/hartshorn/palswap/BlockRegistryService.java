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

package org.dockbox.hartshorn.palswap;

import org.dockbox.hartshorn.api.Hartshorn;
import org.dockbox.hartshorn.api.HartshornInformation;
import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.api.events.annotations.Listener;
import org.dockbox.hartshorn.api.exceptions.Except;
import org.dockbox.hartshorn.api.i18n.common.Language;
import org.dockbox.hartshorn.commands.annotations.Command;
import org.dockbox.hartshorn.commands.context.CommandContext;
import org.dockbox.hartshorn.commands.source.CommandSource;
import org.dockbox.hartshorn.di.annotations.Service;
import org.dockbox.hartshorn.di.annotations.Wired;
import org.dockbox.hartshorn.di.context.ApplicationContext;
import org.dockbox.hartshorn.persistence.registry.Registry;
import org.dockbox.hartshorn.palswap.fileparsers.BlockRegistryParser;
import org.dockbox.hartshorn.persistence.FileManager;
import org.dockbox.hartshorn.persistence.FileType;
import org.dockbox.hartshorn.persistence.FileTypeProperty;
import org.dockbox.hartshorn.server.minecraft.events.server.ServerStartedEvent;
import org.dockbox.hartshorn.server.minecraft.events.server.ServerStoppingEvent;
import org.dockbox.hartshorn.server.minecraft.inventory.Slot;
import org.dockbox.hartshorn.server.minecraft.item.Item;
import org.dockbox.hartshorn.server.minecraft.players.Player;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

@Command("registry")
@Service(disabled = true)
public class BlockRegistryService {

    private static Registry<Registry<Item>> blockRegistry = new Registry<>();
    private final String itemRegistryFile = "itemdata";
    @Wired
    private Logger logger;
    @Wired
    private ApplicationContext context;
    private BlockRegistryParser blockRegistryParser;

    @Listener
    public void on(ServerStartedEvent event) {
        Hartshorn.context().bind(BlockRegistryParser.class, BlockRegistryUtil.getBlockRegistryParserClass());

        this.blockRegistryParser = this.context.get(BlockRegistryParser.class);
        this.blockRegistryParser.LoadItemData(this.itemRegistryFile);

        blockRegistry = loadBlockRegistry();
        if (blockRegistry.isEmpty())
            this.logger.info("No block registry to load.");
        else this.logger.info("Block registry loaded.");
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static @Nullable Registry<Registry<Item>> loadBlockRegistry() {
        FileManager fm = Hartshorn.context().get(FileManager.class, FileTypeProperty.of(FileType.XML));
        Path path = fm.getDataFile(BlockRegistryService.class, "blockregistry");

        Exceptional<Registry> eRegistry = fm.read(path, Registry.class);
        return (Registry<Registry<Item>>) eRegistry.or(new Registry<Registry<Item>>());
    }

    @Listener
    public void on(ServerStoppingEvent event) {
        this.blockRegistryParser.SaveItemData(this.itemRegistryFile);
        saveBlockRegistry();
    }

    public static void saveBlockRegistry() {
        if (null == blockRegistry) return;

        FileManager fm = Hartshorn.context().get(FileManager.class, FileTypeProperty.of(FileType.XML));
        Path path = fm.getDataFile(BlockRegistryService.class, "blockregistry");
        fm.write(path, blockRegistry);
    }

    @Command(value = "generateblockidentifiers", permission = HartshornInformation.GLOBAL_BYPASS)
    public void generateBlockIdentifiers(CommandSource src) {
        try {
            FileManager fileManager = this.context.get(FileManager.class);
            Path path = fileManager.getDataFile(BlockRegistryService.class, "blockidentifiers");
            FileWriter writer = new FileWriter(path.toFile());

            writer.write(this.blockRegistryParser.generateBlockIdentifierEnumFile(
                    "mcblocks.txt", "raw.dump"
            ));

            writer.close();
        }
        catch (IOException e) {
            Except.handle(e);
        }
    }

    @Command(value = "generate", permission = HartshornInformation.GLOBAL_BYPASS)
    public void generateBlockRegistry(CommandSource src) {
        for (BlockIdentifier blockIdentifier : BlockIdentifier.values()) {
            blockRegistry.addColumn(blockIdentifier, new Registry<>());

            Registry<Item> blockVariants = new Registry<>();
            for (String id : blockIdentifier.getIds()) {
                Exceptional<VariantIdentifier> eVariantIdentifier = VariantIdentifier.ofID(id);

                if (eVariantIdentifier.absent()) {
                    this.logger.info(String.format("Couldn't find variant for id %s : %s",
                            id, eVariantIdentifier));
                    continue;
                }
                blockVariants.addData(eVariantIdentifier.get(), Item.of(id));
            }
            blockRegistry.addColumn(blockIdentifier, blockVariants);
        }
        this.logger.info(blockRegistry.toString());
    }

    @Command(value = "save", permission = HartshornInformation.GLOBAL_BYPASS)
    public void serializeBlockRegistry(CommandSource src) {
        saveBlockRegistry();
    }

    @Command(value = "load", permission = HartshornInformation.GLOBAL_BYPASS)
    public void deserializeBlockRegistry(CommandSource src) {
        blockRegistry = loadBlockRegistry();
    }

    @Command(value = "add", arguments = "<item>", permission = HartshornInformation.GLOBAL_BYPASS)
    public void addItem(CommandSource src, CommandContext context) {
        String baseBlock = context.get("item");
        addItem(Item.of(baseBlock));
    }

    public static void addItem(Item item) {
        BlockIdentifier blockIdentifier = BlockIdentifier.ofItem(item);
        if (!blockIdentifier.isAir() && blockRegistry.containsColumns(blockIdentifier)) {
            Exceptional<VariantIdentifier> eVariantIdentifier = VariantIdentifier.ofItem(item);
            if (eVariantIdentifier.present()) {
                VariantIdentifier variantIdentifier = eVariantIdentifier.get();

                Registry<Item> variants = blockRegistry.getMatchingColumns(blockIdentifier).first().get();
                if (variants.containsColumns(variantIdentifier)) {
                    if (variants.getMatchingColumns(variantIdentifier).contains(item))
                        return;
                    else variants.addData(variantIdentifier, item);
                }
            }
        }

        //Unless the item already exists, write the item name to 'unaddedblocks.yml' so it can be added at a later point.
        try {
            FileManager fm = Hartshorn.context().get(FileManager.class);
            Path path = fm.getDataFile(BlockRegistryService.class, "unaddedblocks");
            FileWriter writer = new FileWriter(path.toFile());

            writer.append("Name: ")
                    .append(item.getDisplayName(Language.EN_US).toStringValue())
                    .append(" | ID: ")
                    .append(item.getId());
            writer.close();
        }
        catch (IOException e) {
            Except.handle(e);
        }
    }

    @Command(value = "add", permission = HartshornInformation.GLOBAL_BYPASS)
    public void addItemInHand(Player src, CommandSource context) {
        addItem(src.getInventory().getSlot(Slot.MAIN_HAND));
    }
}
