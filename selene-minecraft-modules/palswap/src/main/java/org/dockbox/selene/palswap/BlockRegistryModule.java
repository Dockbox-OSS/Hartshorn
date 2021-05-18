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

package org.dockbox.selene.palswap;

import org.dockbox.selene.api.Selene;
import org.dockbox.selene.api.SeleneInformation;
import org.dockbox.selene.api.domain.Exceptional;
import org.dockbox.selene.api.events.annotations.Listener;
import org.dockbox.selene.api.exceptions.Except;
import org.dockbox.selene.api.i18n.common.Language;
import org.dockbox.selene.api.module.annotations.Disabled;
import org.dockbox.selene.api.module.annotations.Module;
import org.dockbox.selene.commands.annotations.Command;
import org.dockbox.selene.commands.context.CommandContext;
import org.dockbox.selene.commands.source.CommandSource;
import org.dockbox.selene.di.Provider;
import org.dockbox.selene.di.annotations.Wired;
import org.dockbox.selene.domain.registry.Registry;
import org.dockbox.selene.palswap.fileparsers.BlockRegistryParser;
import org.dockbox.selene.persistence.FileManager;
import org.dockbox.selene.persistence.FileType;
import org.dockbox.selene.persistence.FileTypeProperty;
import org.dockbox.selene.server.events.ServerStartedEvent;
import org.dockbox.selene.server.events.ServerStoppingEvent;
import org.dockbox.selene.server.minecraft.inventory.Slot;
import org.dockbox.selene.server.minecraft.item.Item;
import org.dockbox.selene.server.minecraft.players.Player;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

@Disabled(reason = "Under development")
@Command("registry")
@Module
public class BlockRegistryModule {

    private static Registry<Registry<Item>> blockRegistry = new Registry<>();
    private final String itemRegistryFile = "itemdata";
    @Wired
    private Logger logger;
    private BlockRegistryParser blockRegistryParser;

    @Listener
    public void on(ServerStartedEvent event) {
        Selene.getServer().getInjector().bind(BlockRegistryParser.class, BlockRegistryUtil.getBlockRegistryParserClass());

        this.blockRegistryParser = Provider.provide(BlockRegistryParser.class);
        this.blockRegistryParser.LoadItemData(this.itemRegistryFile);

        blockRegistry = loadBlockRegistry();
        if (blockRegistry.isEmpty())
            this.logger.info("No block registry to load.");
        else this.logger.info("Block registry loaded.");
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static @Nullable Registry<Registry<Item>> loadBlockRegistry() {
        FileManager fm = Provider.provide(FileManager.class, FileTypeProperty.of(FileType.XML));
        Path path = fm.getDataFile(BlockRegistryModule.class, "blockregistry");

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

        FileManager fm = Provider.provide(FileManager.class, FileTypeProperty.of(FileType.XML));
        Path path = fm.getDataFile(BlockRegistryModule.class, "blockregistry");
        fm.write(path, blockRegistry);
    }

    @Command(value = "generateblockidentifiers", permission = SeleneInformation.GLOBAL_BYPASS)
    public void generateBlockIdentifiers(CommandSource src) {
        try {
            FileManager fileManager = Provider.provide(FileManager.class);
            Path path = fileManager.getDataFile(BlockRegistryModule.class, "blockidentifiers");
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

    @Command(value = "generate", permission = SeleneInformation.GLOBAL_BYPASS)
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

    @Command(value = "save", permission = SeleneInformation.GLOBAL_BYPASS)
    public void serializeBlockRegistry(CommandSource src) {
        saveBlockRegistry();
    }

    @Command(value = "load", permission = SeleneInformation.GLOBAL_BYPASS)
    public void deserializeBlockRegistry(CommandSource src) {
        blockRegistry = loadBlockRegistry();
    }

    @Command(value = "add", arguments = "<item>", permission = SeleneInformation.GLOBAL_BYPASS)
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
            FileManager fm = Provider.provide(FileManager.class);
            Path path = fm.getDataFile(BlockRegistryModule.class, "unaddedblocks");
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

    @Command(value = "add", permission = SeleneInformation.GLOBAL_BYPASS)
    public void addItemInHand(Player src, CommandSource context) {
        addItem(src.getInventory().getSlot(Slot.MAIN_HAND));
    }
}
