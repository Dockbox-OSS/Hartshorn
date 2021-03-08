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

import com.google.inject.Inject;

import org.dockbox.selene.api.annotations.command.Command;
import org.dockbox.selene.api.annotations.event.Listener;
import org.dockbox.selene.api.annotations.files.Bulk;
import org.dockbox.selene.api.annotations.module.Module;
import org.dockbox.selene.api.command.context.CommandContext;
import org.dockbox.selene.api.command.source.CommandSource;
import org.dockbox.selene.api.events.server.ServerEvent.ServerStartedEvent;
import org.dockbox.selene.api.events.server.ServerEvent.ServerStoppingEvent;
import org.dockbox.selene.api.files.FileManager;
import org.dockbox.selene.api.i18n.common.Language;
import org.dockbox.selene.api.objects.Exceptional;
import org.dockbox.selene.api.objects.inventory.Slot;
import org.dockbox.selene.api.objects.item.Item;
import org.dockbox.selene.api.objects.player.Player;
import org.dockbox.selene.api.server.Selene;
import org.dockbox.selene.api.server.properties.AnnotationProperty;
import org.dockbox.selene.palswap.fileparsers.BlockRegistryParser;
import org.dockbox.selene.structures.registry.Registry;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

@Command(aliases = "registry", usage = "registry")
@Module(id = "blockregistrygenerator", name = "Block Registry Generator",
        description = "Generates the block identifiers and a registry of all the blocks",
        authors = "pumbas600")
public class BlockRegistryExtension {

    @Inject
    private Logger logger;
    private static Registry<Registry<Item>> blockRegistry = new Registry<>();

    private BlockRegistryParser blockRegistryParser;
    private final String itemRegistryFile = "itemdata";

    @Listener
    public void OnServerStartedEvent(ServerStartedEvent event) {
        Selene.getServer().bindUtility(
                BlockRegistryParser.class, BlockRegistryUtil.getBlockRegistryParserClass());

        this.blockRegistryParser = Selene.provide(BlockRegistryParser.class);
        this.blockRegistryParser.LoadItemData(this.itemRegistryFile);

        blockRegistry = loadBlockRegistry();
        if (blockRegistry.isEmpty())
            this.logger.info("No block registry to load.");
        else this.logger.info("Block registry loaded.");
    }

    @Listener
    public void OnServerStoppingEvent(ServerStoppingEvent event) {
        this.blockRegistryParser.SaveItemData(this.itemRegistryFile);
        saveBlockRegistry();
    }


    @Command(aliases = "generateblockidentifiers", usage = "generateblockidentifiers")
    public void generateBlockIdentifiers(CommandSource src) {
        try {
            FileManager fileManager = Selene.provide(FileManager.class);
            Path path = fileManager.getDataFile(BlockRegistryExtension.class, "blockidentifiers");
            FileWriter writer = new FileWriter(path.toFile());

            writer.write(this.blockRegistryParser.generateBlockIdentifierEnumFile(
                    "mcblocks.txt", "raw.dump"
            ));

            writer.close();
        } catch (IOException e) {
            Selene.handle(e);
        }
    }

    @Command(aliases = "generate", usage = "generate")
    public void generateBlockRegistry(CommandSource src) {
        for (BlockIdentifier blockIdentifier : BlockIdentifier.values()) {
            blockRegistry.addColumn(blockIdentifier, new Registry<>());

            Registry<Item> blockVariants = new Registry<>();
            for (String id : blockIdentifier.getIds()) {
                Exceptional<VariantIdentifier> eVariantIdentifier = VariantIdentifier.ofID(id);

                if (eVariantIdentifier.isAbsent()) {
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

    @Command(aliases = "save", usage = "save")
    public void serializeBlockRegistry(CommandSource src) {
        saveBlockRegistry();
    }

    @Command(aliases = "load", usage = "load")
    public void deserializeBlockRegistry(CommandSource src) {
        blockRegistry = loadBlockRegistry();
    }

    @Command(aliases = "add", usage = "add <item>")
    public void addItem(CommandSource src, CommandContext context) {
        String baseBlock = context.get("item");
        addItem(Item.of(baseBlock));
    }

    @Command(aliases = "add", usage = "add")
    public void addItemInHand(Player src, CommandSource context) {
        addItem(src.getInventory().getSlot(Slot.MAIN_HAND));
    }

    public static void addItem(Item item) {
        BlockIdentifier blockIdentifier = BlockIdentifier.ofItem(item);
        if (!blockIdentifier.isAir() && blockRegistry.containsColumns(blockIdentifier)) {
            Exceptional<VariantIdentifier> eVariantIdentifier = VariantIdentifier.ofItem(item);
            if (eVariantIdentifier.isPresent()) {
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
            FileManager fm = Selene.provide(FileManager.class);
            Path path = fm.getDataFile(BlockRegistryExtension.class, "unaddedblocks");
            FileWriter writer = new FileWriter(path.toFile());

            writer.append("Name: ")
                    .append(item.getDisplayName(Language.EN_US).toStringValue())
                    .append(" | ID: ")
                    .append(item.getId());
            writer.close();
        } catch (IOException e) {
            Selene.handle(e);
        }
    }

    public static void saveBlockRegistry() {
        if (null == blockRegistry) return;

        FileManager fm = Selene.provide(FileManager.class, AnnotationProperty.of(Bulk.class));
        Path path = fm.getDataFile(BlockRegistryExtension.class, "blockregistry");
        fm.write(path, blockRegistry);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static @Nullable Registry<Registry<Item>> loadBlockRegistry() {
        FileManager fm = Selene.provide(FileManager.class, AnnotationProperty.of(Bulk.class));
        Path path = fm.getDataFile(BlockRegistryExtension.class, "blockregistry");

        Exceptional<Registry> eRegistry = fm.read(path, Registry.class);
        return (Registry<Registry<Item>>) eRegistry.orElse(new Registry<Registry<Item>>());
    }
}
