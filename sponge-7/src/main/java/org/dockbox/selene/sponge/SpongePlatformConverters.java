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

package org.dockbox.selene.sponge;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import com.magitechserver.magibridge.util.BridgeCommandSource;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.regions.Region;

import org.dockbox.selene.core.PlatformConversionService;
import org.dockbox.selene.core.WorldStorageService;
import org.dockbox.selene.core.command.source.CommandSource;
import org.dockbox.selene.core.entities.ItemFrame;
import org.dockbox.selene.core.events.world.WorldEvent.WorldCreatingProperties;
import org.dockbox.selene.core.exceptions.TypeConversionException;
import org.dockbox.selene.core.exceptions.global.CheckedSeleneException;
import org.dockbox.selene.core.exceptions.global.UncheckedSeleneException;
import org.dockbox.selene.core.i18n.entry.IntegratedResource;
import org.dockbox.selene.core.inventory.Element;
import org.dockbox.selene.core.inventory.InventoryType;
import org.dockbox.selene.core.objects.Console;
import org.dockbox.selene.core.objects.Exceptional;
import org.dockbox.selene.core.objects.bossbar.BossbarColor;
import org.dockbox.selene.core.objects.bossbar.BossbarStyle;
import org.dockbox.selene.core.objects.inventory.Slot;
import org.dockbox.selene.core.objects.item.Enchant;
import org.dockbox.selene.core.objects.item.Item;
import org.dockbox.selene.core.objects.location.Location;
import org.dockbox.selene.core.objects.player.Gamemode;
import org.dockbox.selene.core.objects.player.Hand;
import org.dockbox.selene.core.objects.player.Player;
import org.dockbox.selene.core.objects.special.Sounds;
import org.dockbox.selene.core.objects.tuple.Vector3N;
import org.dockbox.selene.core.server.Selene;
import org.dockbox.selene.core.text.actions.ClickAction;
import org.dockbox.selene.core.text.actions.HoverAction;
import org.dockbox.selene.core.text.actions.ShiftClickAction;
import org.dockbox.selene.core.text.pagination.Pagination;
import org.dockbox.selene.core.util.SeleneUtils;
import org.dockbox.selene.sponge.external.WrappedMask;
import org.dockbox.selene.sponge.external.WrappedPattern;
import org.dockbox.selene.sponge.external.WrappedRegion;
import org.dockbox.selene.sponge.inventory.SpongeElement;
import org.dockbox.selene.sponge.objects.discord.MagiBridgeCommandSource;
import org.dockbox.selene.sponge.objects.item.SpongeItem;
import org.dockbox.selene.sponge.objects.location.SpongeWorld;
import org.dockbox.selene.sponge.objects.targets.SpongeConsole;
import org.dockbox.selene.sponge.objects.targets.SpongePlayer;
import org.dockbox.selene.worldedit.region.Mask;
import org.dockbox.selene.worldedit.region.Pattern;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.boss.BossBarColor;
import org.spongepowered.api.boss.BossBarColors;
import org.spongepowered.api.boss.BossBarOverlay;
import org.spongepowered.api.boss.BossBarOverlays;
import org.spongepowered.api.command.source.ConsoleSource;
import org.spongepowered.api.data.type.HandType;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.effect.sound.SoundType;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.item.enchantment.Enchantment;
import org.spongepowered.api.item.enchantment.EnchantmentType;
import org.spongepowered.api.item.inventory.InventoryArchetypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.equipment.EquipmentTypes;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.ClickAction.ChangePage;
import org.spongepowered.api.text.action.ClickAction.ExecuteCallback;
import org.spongepowered.api.text.action.ClickAction.OpenUrl;
import org.spongepowered.api.text.action.ClickAction.RunCommand;
import org.spongepowered.api.text.action.ClickAction.SuggestCommand;
import org.spongepowered.api.text.action.HoverAction.ShowText;
import org.spongepowered.api.text.action.ShiftClickAction.InsertText;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyle;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.util.rotation.Rotation;
import org.spongepowered.api.util.rotation.Rotations;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.storage.WorldProperties;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import io.github.nucleuspowered.nucleus.api.nucleusdata.Warp;

public final class SpongePlatformConverters {

    private static final Map<TextColor, Character> textColors = SeleneUtils.ofEntries(
            SeleneUtils.entry(TextColors.BLACK, '0'),
            SeleneUtils.entry(TextColors.DARK_BLUE, '1'),
            SeleneUtils.entry(TextColors.DARK_GREEN, '2'),
            SeleneUtils.entry(TextColors.DARK_AQUA, '3'),
            SeleneUtils.entry(TextColors.DARK_RED, '4'),
            SeleneUtils.entry(TextColors.DARK_PURPLE, '5'),
            SeleneUtils.entry(TextColors.GOLD, '6'),
            SeleneUtils.entry(TextColors.GRAY, '7'),
            SeleneUtils.entry(TextColors.DARK_GRAY, '8'),
            SeleneUtils.entry(TextColors.BLUE, '9'),
            SeleneUtils.entry(TextColors.GREEN, 'a'),
            SeleneUtils.entry(TextColors.AQUA, 'b'),
            SeleneUtils.entry(TextColors.RED, 'c'),
            SeleneUtils.entry(TextColors.LIGHT_PURPLE, 'd'),
            SeleneUtils.entry(TextColors.YELLOW, 'e'),
            SeleneUtils.entry(TextColors.WHITE, 'f'),
            SeleneUtils.entry(TextColors.RESET, 'r')
    );

    private SpongePlatformConverters() {}

    public static void init() {
        SpongePlatformConverters.registerEnchantments();
        SpongePlatformConverters.registerBossbarTypes();
        SpongePlatformConverters.registerEnums();
        SpongePlatformConverters.registerCommandSources();
        SpongePlatformConverters.registerText();
        SpongePlatformConverters.registerItems();
        SpongePlatformConverters.registerLocations();
        SpongePlatformConverters.registerInventories();
        SpongePlatformConverters.registerWorldEdit();
        SpongePlatformConverters.registerEntityData();
    }

    private static void registerEnchantments() {
        PlatformConversionService.register(Enchant.class, enchantment -> {
            Exceptional<EnchantmentType> type = Exceptional.of(Sponge.getRegistry()
                    .getType(EnchantmentType.class, enchantment.getEnchantment().name()));

            return type.map(enchantmentType -> Enchantment.builder()
                    .type(enchantmentType)
                    .level(enchantment.getLevel())
                    .build()
            );
        });
        PlatformConversionService.register(Enchantment.class, enchantment -> {
            try {
                String id = enchantment.getType().getId();
                int level = enchantment.getLevel();
                Enchant enchant = new Enchant(org.dockbox.selene.core.objects.item.Enchantment.valueOf(id.toUpperCase()), level);
                return Exceptional.of(enchant);
            } catch (IllegalArgumentException | NullPointerException e) {
                return Exceptional.of(e);
            }
        });
    }

    private static void registerBossbarTypes() {
        PlatformConversionService.register(BossbarColor.class, bossBarColor -> Sponge.getRegistry()
                .getType(BossBarColor.class, bossBarColor.name())
                .orElse(BossBarColors.WHITE));
        PlatformConversionService.register(BossbarStyle.class, style -> Sponge.getRegistry()
                .getType(BossBarOverlay.class, style.name())
                .orElse(BossBarOverlays.PROGRESS));
    }

    private static void registerEnums() {
        PlatformConversionService.register(Sounds.class, sound -> Exceptional.of(Sponge.getRegistry()
                .getType(SoundType.class, sound.name())
        ));
        PlatformConversionService.register(Gamemode.class, gamemode -> {
            switch (gamemode) {
                case SURVIVAL:
                    return GameModes.SURVIVAL;
                case CREATIVE:
                    return GameModes.CREATIVE;
                case ADVENTURE:
                    return GameModes.ADVENTURE;
                case SPECTATOR:
                    return GameModes.SPECTATOR;
                case OTHER:
                default:
                    return GameModes.NOT_SET;
            }
        });
        PlatformConversionService.register(Hand.class, hand -> {
            if (Hand.MAIN_HAND == hand) return HandTypes.MAIN_HAND;
            if (Hand.OFF_HAND == hand) return HandTypes.OFF_HAND;
            throw new UncheckedSeleneException("Invalid value in context '" + hand + "'");
        });
        PlatformConversionService.register(GameMode.class, gamemode -> {
            try {
                return Enum.valueOf(Gamemode.class, gamemode.toString());
            } catch (IllegalArgumentException | NullPointerException e) {
                return Gamemode.OTHER;
            }
        });
        PlatformConversionService.register(HandType.class, handType -> {
            if (handType == HandTypes.MAIN_HAND) return Hand.MAIN_HAND;
            else if (handType == HandTypes.OFF_HAND) return Hand.OFF_HAND;
            throw new UncheckedSeleneException("Invalid value in context '" + handType + "'");
        });
        PlatformConversionService.register(InventoryType.class, inventoryType -> {
            switch (inventoryType) {
                case DOUBLE_CHEST:
                    return InventoryArchetypes.DOUBLE_CHEST;
                case HOPPER:
                    return InventoryArchetypes.HOPPER;
                case DISPENSER:
                    return InventoryArchetypes.DISPENSER;
                case CHEST:
                default:
                    return InventoryArchetypes.CHEST;
            }
        });
        PlatformConversionService.register(Slot.class, slot -> {
            switch (slot) {
                case HELMET:
                    return EquipmentTypes.HEADWEAR;
                case CHESTPLATE:
                    return EquipmentTypes.CHESTPLATE;
                case LEGGINGS:
                    return EquipmentTypes.LEGGINGS;
                case BOOTS:
                    return EquipmentTypes.BOOTS;
                case MAIN_HAND:
                    return EquipmentTypes.MAIN_HAND;
                case OFF_HAND:
                    return EquipmentTypes.OFF_HAND;
            }
            return EquipmentTypes.ANY;
        });
    }

    @SuppressWarnings("OverlyStrongTypeCast")
    private static void registerCommandSources() {
        PlatformConversionService.register(CommandSource.class, src -> {
            if (src instanceof Console) return Exceptional.of(Sponge.getServer().getConsole());
            else if (src instanceof Player)
                return Exceptional.of(Sponge.getServer().getPlayer(((Player) src).getUniqueId()));
            return Exceptional.empty();
        });
        PlatformConversionService.register(Player.class, player -> {
            if (player instanceof SpongePlayer) {
                return ((SpongePlayer) player).getSpongePlayer();
            }
            return Exceptional.empty();
        });
        PlatformConversionService.register(org.spongepowered.api.entity.living.player.Player.class, player -> new SpongePlayer(player.getUniqueId(), player.getName()));
        PlatformConversionService.register(org.spongepowered.api.entity.living.player.User.class, user -> new SpongePlayer(user.getUniqueId(), user.getName()));
        PlatformConversionService.register(org.spongepowered.api.command.CommandSource.class, commandSource -> {
            if (commandSource instanceof ConsoleSource) return Exceptional.of(SpongeConsole.getInstance());
            else if (commandSource instanceof org.spongepowered.api.entity.living.player.Player)
                return Exceptional.of(PlatformConversionService.<org.spongepowered.api.entity.living.player.Player, Player>map((org.spongepowered.api.entity.living.player.Player) commandSource));
            else if (commandSource instanceof BridgeCommandSource)
                return Exceptional.of(new MagiBridgeCommandSource((BridgeCommandSource) commandSource));
            return Exceptional.of(new TypeConversionException(commandSource.getClass(), CommandSource.class));
        });
    }

    @SuppressWarnings("unchecked")
    private static void registerText() {
        PlatformConversionService.register(Pagination.class, pagination -> {
            PaginationList.Builder builder = PaginationList.builder();

            if (null != pagination.getTitle()) builder.title(PlatformConversionService.map(pagination.getTitle()));
            if (null != pagination.getHeader()) builder.header(PlatformConversionService.map(pagination.getHeader()));
            if (null != pagination.getFooter()) builder.footer(PlatformConversionService.map(pagination.getFooter()));

            builder.padding(PlatformConversionService.map(pagination.getPadding()));
            if (0 < pagination.getLinesPerPage())
                builder.linesPerPage(pagination.getLinesPerPage());
            List<Text> convertedContent = pagination.getContent().stream()
                    .map(PlatformConversionService::map)
                    .map(Text.class::cast)
                    .collect(Collectors.toList());
            builder.contents(convertedContent);
            return builder.build();
        });
        PlatformConversionService.register(org.dockbox.selene.core.text.Text.class, message -> {
            Iterable<org.dockbox.selene.core.text.Text> parts = message.getParts();
            Text.Builder b = Text.builder();
            parts.forEach(part -> {
                Text.Builder pb = Text.builder();
                // Wrapping in parseColors first so internal color codes are parsed as well. Technically the FormattingCode
                // from TextSerializers won't be needed, but to ensure no trailing codes are left we use it here anyway.
                pb.append(TextSerializers.FORMATTING_CODE.deserialize(IntegratedResource.parse(part.toLegacy())));

                Exceptional<org.spongepowered.api.text.action.ClickAction<?>> clickAction = PlatformConversionService.map(part.getClickAction());
                clickAction.ifPresent(pb::onClick);

                Exceptional<org.spongepowered.api.text.action.HoverAction<?>> hoverAction = PlatformConversionService.map(part.getHoverAction());
                hoverAction.ifPresent(pb::onHover);

                Exceptional<org.spongepowered.api.text.action.ShiftClickAction<?>> shiftClickAction = PlatformConversionService.map(part.getShiftClickAction());
                shiftClickAction.ifPresent(pb::onShiftClick);

                b.append(pb.build());
            });

            return b.build();
        });
        PlatformConversionService.register(ShiftClickAction.class, action -> {
            if (null == action) return Exceptional.empty();
            Object result = action.getResult();
            if (action instanceof ShiftClickAction.InsertText) {
                return Exceptional.of(TextActions.insertText(((org.dockbox.selene.core.text.Text) result).toPlain()));
            }
            return Exceptional.empty();
        });
        PlatformConversionService.register(HoverAction.class, action -> {
            if (null == action) return Exceptional.empty();
            Object result = action.getResult();
            if (action instanceof HoverAction.ShowText) {
                return Exceptional.of(TextActions.showText(PlatformConversionService.map(((org.dockbox.selene.core.text.Text) result))));
            }
            return Exceptional.empty();
        });
        PlatformConversionService.register(ClickAction.class, action -> {
            if (null == action) return Exceptional.empty();
            Object result = action.getResult();
            if (action instanceof ClickAction.OpenUrl) {
                return Exceptional.of(TextActions.openUrl((URL) result));
            } else if (action instanceof ClickAction.RunCommand) {
                return Exceptional.of(TextActions.runCommand((String) result));
            } else if (action instanceof ClickAction.ChangePage) {
                return Exceptional.of(TextActions.changePage((int) result));
            } else if (action instanceof ClickAction.SuggestCommand) {
                return Exceptional.of(TextActions.suggestCommand((String) result));
            } else if (action instanceof ClickAction.ExecuteCallback) {
                return Exceptional.of(TextActions.executeCallback(commandSource -> {
                    Consumer<CommandSource> consumer = ((ClickAction.ExecuteCallback) action).getResult();
                    try {
                        PlatformConversionService.<org.spongepowered.api.command.CommandSource, CommandSource>mapSafely(commandSource).ifPresent(consumer).rethrow();
                    } catch (CheckedSeleneException throwable) {
                        commandSource.sendMessage(Text.of(IntegratedResource.UNKNOWN_ERROR.format(throwable.getMessage())));
                    }
                }));
            }
            return Exceptional.empty();
        });
        PlatformConversionService.register(Text.class, text -> {
            String style = PlatformConversionService.map(text.getFormat().getStyle());
            String color = PlatformConversionService.map(text.getFormat().getColor());
            String value = text.toPlainSingle();

            org.dockbox.selene.core.text.Text t = org.dockbox.selene.core.text.Text.of(color + style + value);

            text.getClickAction().map(PlatformConversionService::map)
                    .map(action -> (Exceptional<ClickAction<?>>) action)
                    .ifPresent(action -> action.ifPresent(t::onClick));
            text.getHoverAction().map(PlatformConversionService::map)
                    .map(action -> (Exceptional<HoverAction<?>>) action)
                    .ifPresent(action -> action.ifPresent(t::onHover));
            text.getShiftClickAction().map(PlatformConversionService::map)
                    .map(action -> (Exceptional<ShiftClickAction<?>>) action)
                    .ifPresent(action -> action.ifPresent(t::onShiftClick));

            // Last step
            text.getChildren().stream()
                    .map(PlatformConversionService::map)
                    .map(org.dockbox.selene.core.text.Text.class::cast)
                    .forEach(t::append);
            return t;
        });
        PlatformConversionService.register(org.spongepowered.api.text.action.ShiftClickAction.class, shiftClickAction -> {
            if (shiftClickAction instanceof org.spongepowered.api.text.action.ShiftClickAction.InsertText) {
                return Exceptional.of(ShiftClickAction.insertText(org.dockbox.selene.core.text.Text.of(
                        ((InsertText) shiftClickAction).getResult()))
                );
            } else return Exceptional.empty();
        });
        PlatformConversionService.register(org.spongepowered.api.text.action.HoverAction.class, hoverAction -> {
            if (hoverAction instanceof org.spongepowered.api.text.action.HoverAction.ShowText) {
                return Exceptional.of(HoverAction.showText(PlatformConversionService.map(((ShowText) hoverAction).getResult())));
            } else return Exceptional.empty();
        });
        PlatformConversionService.register(org.spongepowered.api.text.action.ClickAction.class, clickAction -> {
            if (clickAction instanceof org.spongepowered.api.text.action.ClickAction.OpenUrl) {
                return Exceptional.of(ClickAction.openUrl(((OpenUrl) clickAction).getResult()));

            } else if (clickAction instanceof org.spongepowered.api.text.action.ClickAction.RunCommand) {
                return Exceptional.of(ClickAction.runCommand((((RunCommand) clickAction).getResult())));

            } else if (clickAction instanceof org.spongepowered.api.text.action.ClickAction.ChangePage) {
                return Exceptional.of(ClickAction.changePage((((ChangePage) clickAction).getResult())));

            } else if (clickAction instanceof org.spongepowered.api.text.action.ClickAction.SuggestCommand) {
                return Exceptional.of(ClickAction.suggestCommand((((SuggestCommand) clickAction).getResult())));

            } else if (clickAction instanceof org.spongepowered.api.text.action.ClickAction.ExecuteCallback) {
                return Exceptional.of(ClickAction.executeCallback(src -> PlatformConversionService.<CommandSource, org.spongepowered.api.command.CommandSource>mapSafely(src)
                        .ifPresent(ssrc -> ((ExecuteCallback) clickAction).getResult().accept(ssrc))
                        .ifAbsent(() -> Selene.log().warn("Attempted to execute callback with unknown source type '" + src + "', is it convertable?"))
                ));

            } else return Exceptional.empty();
        });
        PlatformConversionService.register(TextColor.class, color -> org.dockbox.selene.core.text.Text.sectionSymbol + textColors.getOrDefault(color, 'f') + "");
        PlatformConversionService.register(TextStyle.class, style -> {
            final char styleChar = org.dockbox.selene.core.text.Text.sectionSymbol;
            String styleString = styleChar + "r";
            if (SeleneUtils.unwrap(style.isBold())) styleString += styleChar + 'l';
            if (SeleneUtils.unwrap(style.isItalic())) styleString += styleChar + 'o';
            if (SeleneUtils.unwrap(style.isObfuscated())) styleString += styleChar + 'k';
            if (SeleneUtils.unwrap(style.hasUnderline())) styleString += styleChar + 'n';
            if (SeleneUtils.unwrap(style.hasStrikethrough())) styleString += styleChar + 'm';
            return styleString;
        });
    }

    @SuppressWarnings("OverlyStrongTypeCast")
    public static void registerItems() {
        PlatformConversionService.register(Item.class, item -> {
            if (item instanceof SpongeItem)
                // Create a copy of the ItemStack so Sponge doesn't modify the Item reference
                return ((SpongeItem) item).getReference().orElse(ItemStack.empty()).copy();
            return ItemStack.empty();
        });
        PlatformConversionService.register(ItemStack.class, item -> new SpongeItem(item.copy()));
    }

    public static void registerLocations() {
        PlatformConversionService.register(Location.class, location -> {
            Exceptional<World> world = PlatformConversionService.mapSafely(location.getWorld());
            if (world.errorPresent()) return Exceptional.of(world.getError());
            if (!world.isPresent()) return Exceptional.empty();
            Vector3d vector3d = new Vector3d(location.getVectorLoc().getXd(), location.getVectorLoc().getYd(), location.getVectorLoc().getZd());
            return Exceptional.of(new org.spongepowered.api.world.Location<>(world.get(), vector3d));
        });
        PlatformConversionService.register(org.spongepowered.api.world.Location.class, location -> {
            org.dockbox.selene.core.objects.location.World world = PlatformConversionService.map(location.getExtent());
            Vector3N vector3N = new Vector3N(location.getX(), location.getY(), location.getZ());
            return new org.dockbox.selene.core.objects.location.Location(vector3N, world);
        });
        PlatformConversionService.register(org.dockbox.selene.core.objects.location.World.class, world -> {
            if (world instanceof SpongeWorld) {
                World wref = ((SpongeWorld) world).getReferenceWorld();
                if (null != wref) return Exceptional.of(wref);
            }

            return Exceptional.of(() -> Sponge.getServer().getWorld(world.getWorldUniqueId())
                    .orElseThrow(() -> new RuntimeException("World reference not present on server")));
        });
        PlatformConversionService.register(World.class, world -> {
            Vector3i vector3i = world.getProperties().getSpawnPosition();
            Vector3N spawnLocation = new Vector3N(vector3i.getX(), vector3i.getY(), vector3i.getZ());

            org.dockbox.selene.core.objects.location.World spongeWorld = new SpongeWorld(
                    world.getUniqueId(),
                    world.getName(),
                    world.getProperties().loadOnStartup(),
                    spawnLocation,
                    world.getProperties().getSeed(),
                    PlatformConversionService.map(world.getProperties().getGameMode())
            );
            world.getProperties().getGameRules().forEach(spongeWorld::setGamerule);
            return spongeWorld;
        });
        PlatformConversionService.register(WorldProperties.class, worldProperties -> {
            Vector3i vector3i = worldProperties.getSpawnPosition();
            Vector3N spawnLocation = new Vector3N(vector3i.getX(), vector3i.getY(), vector3i.getZ());

            return new WorldCreatingProperties(
                    worldProperties.getWorldName(),
                    worldProperties.getUniqueId(),
                    worldProperties.loadOnStartup(),
                    spawnLocation,
                    worldProperties.getSeed(),
                    PlatformConversionService.map(worldProperties.getGameMode()),
                    worldProperties.getGameRules()
            );
        });
        PlatformConversionService.register(Warp.class, warp -> {
            org.dockbox.selene.core.objects.location.Location location = warp.getLocation()
                    .map(PlatformConversionService::map)
                    .map(org.dockbox.selene.core.objects.location.Location.class::cast)
                    .orElse(org.dockbox.selene.core.objects.location.Location.empty());

            return new org.dockbox.selene.core.objects.location.Warp(
                    Exceptional.of(warp.getDescription().map(Text::toString)),
                    Exceptional.of(warp.getCategory()),
                    location,
                    warp.getName()
            );
        });
    }

    private static void registerInventories() {
        PlatformConversionService.register(Element.class, element -> {
            if (element instanceof SpongeElement) {
                return dev.flashlabs.flashlibs.inventory.Element.of(PlatformConversionService.map(element.getItem()), a -> ((SpongeElement) element).perform(PlatformConversionService.map(a.getPlayer())));
            }
            return dev.flashlabs.flashlibs.inventory.Element.EMPTY;
        });
        PlatformConversionService.register(dev.flashlabs.flashlibs.inventory.Element.class, element -> {
            Item item = PlatformConversionService.map(element.getItem().createStack());
            return org.dockbox.selene.core.inventory.Element.of(item); // Action is skipped here
        });
    }

    private static void registerWorldEdit() {
        PlatformConversionService.register(Region.class, WrappedRegion::new);
        PlatformConversionService.register(org.dockbox.selene.worldedit.region.Region.class, region -> {
            if (region instanceof WrappedRegion) {
                return ((WrappedRegion) region).getReference().orNull();
            } else {
                com.sk89q.worldedit.world.World world = PlatformConversionService.map(region.getWorld());
                Vector3N min = region.getMinimumPoint();
                Vector3N max = region.getMaximumPoint();

                return new com.sk89q.worldedit.regions.CuboidRegion(
                        world,
                        new Vector(min.getXd(), min.getYd(), min.getZd()),
                        new Vector(max.getXd(), max.getYd(), max.getZd())
                );
            }
        });
        PlatformConversionService.register(Clipboard.class, clipboard -> {
            org.dockbox.selene.worldedit.region.Region region = PlatformConversionService.map(clipboard.getRegion());
            Vector origin = clipboard.getOrigin();
            return new org.dockbox.selene.worldedit.region.Clipboard(region, new Vector3N(origin.getX(), origin.getY(), origin.getZ()));
        });
        PlatformConversionService.register(org.dockbox.selene.worldedit.region.Clipboard.class, clipboard -> {
            com.sk89q.worldedit.regions.Region region = PlatformConversionService.map(clipboard.getRegion());
            Vector3N origin = clipboard.getOrigin();
            com.sk89q.worldedit.extent.clipboard.Clipboard worldEditClipboard = new BlockArrayClipboard(region);
            worldEditClipboard.setOrigin(new Vector(origin.getXd(), origin.getYd(), origin.getZd()));
            return worldEditClipboard;
        });
        PlatformConversionService.register(Vector.class, vector -> new Vector3N(vector.getX(), vector.getY(), vector.getZ()));
        PlatformConversionService.register(com.sk89q.worldedit.world.World.class, world -> Selene.provide(WorldStorageService.class).getWorld(world.getName()).orNull());
        PlatformConversionService.register(Mask.class, mask -> {
            if (mask instanceof WrappedMask) {
                return ((WrappedMask) mask).getReference().orNull();
            }
            throw new IllegalStateException("Unknown implementation for Mask: [" + mask.getClass() + "]");
        });
        PlatformConversionService.register(Pattern.class, pattern -> {
            if (pattern instanceof WrappedPattern) {
                return ((WrappedPattern) pattern).getReference().orNull();
            }
            throw new IllegalStateException("Unknown implementation for Pattern: [" + pattern.getClass() + "]");
        });
    }

    private static void registerEntityData() {
        PlatformConversionService.register(Rotation.class, rotation -> {
            try {
                return ItemFrame.Rotation.valueOf(rotation.getName());
            } catch (IllegalArgumentException | NullPointerException e) {
                return ItemFrame.Rotation.TOP;
            }
        });
        PlatformConversionService.register(ItemFrame.Rotation.class, rotation -> Sponge.getRegistry()
                .getType(Rotation.class, rotation.name())
                .orElse(Rotations.TOP));
    }

}
