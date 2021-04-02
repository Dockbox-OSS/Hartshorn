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

package org.dockbox.selene.sponge.util;

import com.boydti.fawe.object.FawePlayer;
import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import com.intellectualcrafters.plot.object.PlotBlock;
import com.intellectualcrafters.plot.object.PlotPlayer;
import com.magitechserver.magibridge.util.BridgeCommandSource;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.extension.input.ParserContext;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.function.mask.Mask;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.sponge.SpongeWorldEdit;

import org.dockbox.selene.api.Worlds;
import org.dockbox.selene.api.command.source.CommandSource;
import org.dockbox.selene.api.entities.ItemFrame;
import org.dockbox.selene.api.events.entity.SpawnSource;
import org.dockbox.selene.api.events.world.WorldCreatingProperties;
import org.dockbox.selene.api.exceptions.TypeConversionException;
import org.dockbox.selene.api.exceptions.global.UncheckedSeleneException;
import org.dockbox.selene.api.i18n.entry.DefaultResource;
import org.dockbox.selene.api.i18n.permissions.PermissionContext;
import org.dockbox.selene.api.inventory.InventoryType;
import org.dockbox.selene.api.objects.Console;
import org.dockbox.selene.api.objects.Exceptional;
import org.dockbox.selene.api.objects.bossbar.BossbarColor;
import org.dockbox.selene.api.objects.bossbar.BossbarStyle;
import org.dockbox.selene.api.objects.inventory.Slot;
import org.dockbox.selene.api.objects.item.Enchant;
import org.dockbox.selene.api.objects.item.Item;
import org.dockbox.selene.api.objects.location.Warp;
import org.dockbox.selene.api.objects.location.dimensions.Chunk;
import org.dockbox.selene.api.objects.location.position.BlockFace;
import org.dockbox.selene.api.objects.player.Gamemode;
import org.dockbox.selene.api.objects.player.Hand;
import org.dockbox.selene.api.objects.player.Player;
import org.dockbox.selene.api.objects.special.Sounds;
import org.dockbox.selene.api.objects.targets.Identifiable;
import org.dockbox.selene.api.objects.tuple.Tristate;
import org.dockbox.selene.api.objects.tuple.Vector3N;
import org.dockbox.selene.api.server.Selene;
import org.dockbox.selene.api.text.actions.ClickAction;
import org.dockbox.selene.api.text.actions.HoverAction;
import org.dockbox.selene.api.text.actions.ShiftClickAction;
import org.dockbox.selene.api.text.pagination.Pagination;
import org.dockbox.selene.api.util.SeleneUtils;
import org.dockbox.selene.common.inventory.SimpleElement;
import org.dockbox.selene.common.objects.item.ReferencedItem;
import org.dockbox.selene.sponge.entities.SpongeArmorStand;
import org.dockbox.selene.sponge.entities.SpongeGenericEntity;
import org.dockbox.selene.sponge.entities.SpongeItemFrame;
import org.dockbox.selene.sponge.external.WrappedMask;
import org.dockbox.selene.sponge.external.WrappedPattern;
import org.dockbox.selene.sponge.external.WrappedRegion;
import org.dockbox.selene.sponge.objects.discord.MagiBridgeCommandSource;
import org.dockbox.selene.sponge.objects.item.SpongeItem;
import org.dockbox.selene.sponge.objects.location.SpongeChunk;
import org.dockbox.selene.sponge.objects.location.SpongeWorld;
import org.dockbox.selene.sponge.objects.targets.SpongeConsole;
import org.dockbox.selene.sponge.objects.targets.SpongePlayer;
import org.dockbox.selene.worldedit.region.Clipboard;
import org.dockbox.selene.worldedit.region.Region;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.boss.BossBarColor;
import org.spongepowered.api.boss.BossBarColors;
import org.spongepowered.api.boss.BossBarOverlay;
import org.spongepowered.api.boss.BossBarOverlays;
import org.spongepowered.api.command.source.ConsoleSource;
import org.spongepowered.api.data.type.HandType;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.effect.sound.SoundType;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.Tamer;
import org.spongepowered.api.entity.living.ArmorStand;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.event.cause.entity.spawn.SpawnType;
import org.spongepowered.api.event.cause.entity.spawn.SpawnTypes;
import org.spongepowered.api.item.enchantment.Enchantment;
import org.spongepowered.api.item.enchantment.EnchantmentType;
import org.spongepowered.api.item.inventory.InventoryArchetype;
import org.spongepowered.api.item.inventory.InventoryArchetypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.equipment.EquipmentType;
import org.spongepowered.api.item.inventory.equipment.EquipmentTypes;
import org.spongepowered.api.service.context.Context;
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
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.util.rotation.Rotation;
import org.spongepowered.api.util.rotation.Rotations;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.storage.WorldProperties;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import dev.flashlabs.flashlibs.inventory.Element;

@SuppressWarnings({
                          "ClassWithTooManyMethods",
                          "OverlyComplexClass",
                          "unchecked",
                          "OverlyStrongTypeCast"
                  })
public enum SpongeConversionUtil {
    ;

    private static final Map<TextColor, Character> textColors =
            SeleneUtils.ofEntries(
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
                    SeleneUtils.entry(TextColors.RESET, 'r'));

    @NotNull
    public static <T> Exceptional<?> autoDetectFromSponge(T object) {

        // CommandSource, Location, World, Gamemode
        if (object instanceof org.spongepowered.api.entity.living.player.Player) {
            return Exceptional.of(fromSponge((org.spongepowered.api.entity.living.player.Player) object));
        }
        else if (object instanceof org.spongepowered.api.command.CommandSource) {
            return fromSponge((org.spongepowered.api.command.CommandSource) object);
        }
        else if (object instanceof Location) {
            return Exceptional.of(fromSponge((Location<World>) object));
        }
        else if (object instanceof World) {
            return Exceptional.of(fromSponge((World) object));
        }
        else if (object instanceof GameMode) {
            return Exceptional.of(fromSponge((GameMode) object));
        }
        else if (object instanceof User) {
            return Exceptional.of(
                    new SpongePlayer(((Identifiable) object).getUniqueId(), ((Tamer) object).getName()));
        }
        else if (object instanceof ItemStack) {
            return Exceptional.of(fromSponge((ItemStack) object));
        }
        else if (object instanceof Enchantment) {
            return fromSponge((Enchantment) object);
        }
        return Exceptional.none();
    }

    @NotNull
    public static Exceptional<Enchantment> toSponge(Enchant enchantment) {
        Exceptional<EnchantmentType> type = Exceptional.of(Sponge.getRegistry().getType(EnchantmentType.class, enchantment.getEnchantment().name()));

        return type.map(enchantmentType -> Enchantment.builder().type(enchantmentType).level(enchantment.getLevel()).build());
    }

    public static BossBarColor toSponge(BossbarColor bossbarColor) {
        return Sponge.getRegistry()
                .getType(BossBarColor.class, bossbarColor.name())
                .orElse(BossBarColors.WHITE);
    }

    public static BossBarOverlay toSponge(BossbarStyle style) {
        return Sponge.getRegistry()
                .getType(BossBarOverlay.class, style.name())
                .orElse(BossBarOverlays.PROGRESS);
    }

    @NotNull
    public static Exceptional<SoundType> toSponge(Sounds sound) {
        return Exceptional.of(Sponge.getRegistry().getType(SoundType.class, sound.name()));
    }

    @NotNull
    public static Exceptional<? extends org.spongepowered.api.command.CommandSource> toSponge(CommandSource src) {
        if (src instanceof Console) return Exceptional.of(Sponge.getServer().getConsole());
        else if (src instanceof Player) return Exceptional.of(Sponge.getServer().getPlayer(((Player) src).getUniqueId()));
        return Exceptional.none();
    }

    @NotNull
    public static PaginationList toSponge(Pagination pagination) {
        PaginationList.Builder builder = PaginationList.builder();

        if (null != pagination.getTitle()) builder.title(toSponge(pagination.getTitle()));
        if (null != pagination.getHeader()) builder.header(toSponge(pagination.getHeader()));
        if (null != pagination.getFooter()) builder.footer(toSponge(pagination.getFooter()));

        builder.padding(toSponge(pagination.getPadding()));
        if (0 < pagination.getLinesPerPage()) builder.linesPerPage(pagination.getLinesPerPage());
        List<Text> convertedContent = pagination.getContent().stream()
                .map(SpongeConversionUtil::toSponge)
                .collect(Collectors.toList());
        builder.contents(convertedContent);
        return builder.build();
    }

    @NotNull
    public static Text toSponge(org.dockbox.selene.api.text.Text message) {
        Iterable<org.dockbox.selene.api.text.Text> parts = message.getParts();
        Text.Builder b = Text.builder();
        parts.forEach(part -> {
            Text.Builder pb = Text.builder();
            // Wrapping in parseColors first so internal color codes are parsed as well. Technically
            // the FormattingCode
            // from TextSerializers won't be needed, but to ensure no trailing codes are left we use
            // it here anyway.
            pb.append(TextSerializers.FORMATTING_CODE.deserialize(
                    DefaultResource.parse(part.toLegacy())));

            Exceptional<org.spongepowered.api.text.action.ClickAction<?>> clickAction = toSponge(part.getClickAction());
            clickAction.present(pb::onClick);

            Exceptional<org.spongepowered.api.text.action.HoverAction<?>> hoverAction = toSponge(part.getHoverAction());
            hoverAction.present(pb::onHover);

            Exceptional<org.spongepowered.api.text.action.ShiftClickAction<?>> shiftClickAction = toSponge(part.getShiftClickAction());
            shiftClickAction.present(pb::onShiftClick);

            b.append(pb.build());
        });

        return b.build();
    }

    @NotNull
    private static Exceptional<org.spongepowered.api.text.action.ShiftClickAction<?>> toSponge(ShiftClickAction<?> action) {
        if (null == action) return Exceptional.none();
        Object result = action.getResult();
        if (action instanceof ShiftClickAction.InsertText) {
            return Exceptional.of(TextActions.insertText(((org.dockbox.selene.api.text.Text) result).toPlain()));
        }
        return Exceptional.none();
    }

    @NotNull
    private static Exceptional<org.spongepowered.api.text.action.HoverAction<?>> toSponge(HoverAction<?> action) {
        if (null == action) return Exceptional.none();
        Object result = action.getResult();
        if (action instanceof HoverAction.ShowText) {
            return Exceptional.of(TextActions.showText(toSponge(((org.dockbox.selene.api.text.Text) result))));
        }
        return Exceptional.none();
    }

    @NotNull
    private static Exceptional<org.spongepowered.api.text.action.ClickAction<?>> toSponge(ClickAction<?> action) {
        if (null == action) return Exceptional.none();
        Object result = action.getResult();
        if (action instanceof ClickAction.OpenUrl) {
            return Exceptional.of(TextActions.openUrl((URL) result));
        }
        else if (action instanceof ClickAction.RunCommand) {
            return Exceptional.of(TextActions.runCommand((String) result));
        }
        else if (action instanceof ClickAction.ChangePage) {
            return Exceptional.of(TextActions.changePage((int) result));
        }
        else if (action instanceof ClickAction.SuggestCommand) {
            return Exceptional.of(TextActions.suggestCommand((String) result));
        }
        else if (action instanceof ClickAction.ExecuteCallback) {
            return Exceptional.of(TextActions.executeCallback(commandSource -> {
                Consumer<CommandSource> consumer = ((ClickAction.ExecuteCallback) action).getResult();
                try {
                    fromSponge(commandSource).present(consumer).rethrow();
                }
                catch (UncheckedSeleneException throwable) {
                    commandSource.sendMessage(Text.of(DefaultResource.UNKNOWN_ERROR.format(throwable.getMessage())));
                }
            }));
        }
        return Exceptional.none();
    }

    @NotNull
    public static Exceptional<Location<World>> toSponge(org.dockbox.selene.api.objects.location.position.Location location) {
        Exceptional<World> world = toSponge(location.getWorld());
        if (world.caught()) return Exceptional.of(world.error());
        if (!world.present()) return Exceptional.none();
        Vector3d vector3d = new Vector3d(
                location.getVectorLoc().getXd(),
                location.getVectorLoc().getYd(),
                location.getVectorLoc().getZd()
        );
        return Exceptional.of(new Location<>(world.get(), vector3d));
    }

    @NotNull
    public static Exceptional<World> toSponge(org.dockbox.selene.api.objects.location.dimensions.World world) {
        if (world instanceof SpongeWorld) {
            World wref = ((SpongeWorld) world).getReferenceWorld();
            if (null != wref) return Exceptional.of(wref);
        }

        return Exceptional.of(() -> Sponge.getServer()
                .getWorld(world.getWorldUniqueId())
                .orElseThrow(() -> new RuntimeException("World reference not present on server"))
        );
    }

    @NotNull
    public static Exceptional<Enchant> fromSponge(Enchantment enchantment) {
        try {
            String id = enchantment.getType().getId();
            int level = enchantment.getLevel();
            Enchant enchant = new Enchant(org.dockbox.selene.api.objects.item.Enchantment.valueOf(id.toUpperCase()), level);
            return Exceptional.of(enchant);
        }
        catch (IllegalArgumentException | NullPointerException e) {
            return Exceptional.of(e);
        }
    }

    @NotNull
    public static GameMode toSponge(Gamemode gamemode) {
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
    }

    public static HandType toSponge(Hand hand) {
        if (Hand.MAIN_HAND == hand) return HandTypes.MAIN_HAND;
        if (Hand.OFF_HAND == hand) return HandTypes.OFF_HAND;
        throw new UncheckedSeleneException("Invalid value in context '" + hand + "'");
    }

    @NotNull
    public static org.dockbox.selene.api.text.Text fromSponge(Text text) {
        String style = fromSponge(text.getFormat().getStyle());
        String color = fromSponge(text.getFormat().getColor());
        String value = text.toPlainSingle();

        org.dockbox.selene.api.text.Text t = org.dockbox.selene.api.text.Text.of(color + style + value);

        text.getClickAction()
                .map(SpongeConversionUtil::fromSponge)
                .ifPresent(action -> action.present(t::onClick));
        text.getHoverAction()
                .map(SpongeConversionUtil::fromSponge)
                .ifPresent(action -> action.present(t::onHover));
        text.getShiftClickAction()
                .map(SpongeConversionUtil::fromSponge)
                .ifPresent(action -> action.present(t::onShiftClick));

        // Last step
        text.getChildren().stream().map(SpongeConversionUtil::fromSponge).forEach(t::append);
        return t;
    }

    private static Exceptional<ShiftClickAction<?>> fromSponge(org.spongepowered.api.text.action.ShiftClickAction<?> shiftClickAction) {
        if (shiftClickAction instanceof InsertText) {
            return Exceptional.of(ShiftClickAction.insertText(org.dockbox.selene.api.text.Text.of(((InsertText) shiftClickAction).getResult())));
        }
        else return Exceptional.none();
    }

    private static Exceptional<HoverAction<?>> fromSponge(org.spongepowered.api.text.action.HoverAction<?> hoverAction) {
        if (hoverAction instanceof ShowText) {
            return Exceptional.of(HoverAction.showText(fromSponge(((ShowText) hoverAction).getResult())));
        }
        else return Exceptional.none();
    }

    private static Exceptional<ClickAction<?>> fromSponge(org.spongepowered.api.text.action.ClickAction<?> clickAction) {
        if (clickAction instanceof OpenUrl) {
            return Exceptional.of(ClickAction.openUrl(((OpenUrl) clickAction).getResult()));
        }
        else if (clickAction instanceof RunCommand) {
            return Exceptional.of(ClickAction.runCommand((((RunCommand) clickAction).getResult())));
        }
        else if (clickAction instanceof ChangePage) {
            return Exceptional.of(ClickAction.changePage((((ChangePage) clickAction).getResult())));
        }
        else if (clickAction instanceof SuggestCommand) {
            return Exceptional.of(ClickAction.suggestCommand((((SuggestCommand) clickAction).getResult())));
        }
        else if (clickAction instanceof ExecuteCallback) {
            return Exceptional.of(ClickAction.executeCallback(src ->
                    toSponge(src)
                            .present(ssrc -> ((ExecuteCallback) clickAction).getResult().accept(ssrc))
                            .absent(() ->
                                    Selene.log().warn("Attempted to execute callback with unknown source type '" + src + "', is it convertable?")))
            );

        }
        else return Exceptional.none();
    }

    private static String fromSponge(TextColor color) {
        return org.dockbox.selene.api.text.Text.sectionSymbol + textColors.getOrDefault(color, 'f') + "";
    }

    private static String fromSponge(TextStyle style) {
        final char styleChar = org.dockbox.selene.api.text.Text.sectionSymbol;
        String styleString = styleChar + "r";
        if (SeleneUtils.unwrap(style.isBold())) styleString += styleChar + 'l';
        if (SeleneUtils.unwrap(style.isItalic())) styleString += styleChar + 'o';
        if (SeleneUtils.unwrap(style.isObfuscated())) styleString += styleChar + 'k';
        if (SeleneUtils.unwrap(style.hasUnderline())) styleString += styleChar + 'n';
        if (SeleneUtils.unwrap(style.hasStrikethrough())) styleString += styleChar + 'm';
        return styleString;
    }

    @NotNull
    public static Exceptional<CommandSource> fromSponge(org.spongepowered.api.command.CommandSource commandSource) {
        if (commandSource instanceof ConsoleSource) {
            return Exceptional.of(SpongeConsole.getInstance());
        }
        else if (commandSource instanceof org.spongepowered.api.entity.living.player.Player) {
            return Exceptional.of(fromSponge((org.spongepowered.api.entity.living.player.Player) commandSource));
        }
        else if (commandSource instanceof BridgeCommandSource) {
            return Exceptional.of(new MagiBridgeCommandSource((BridgeCommandSource) commandSource));
        }
        return Exceptional.of(new TypeConversionException(commandSource.getClass(), CommandSource.class));
    }

    @NotNull
    public static WorldCreatingProperties fromSpongeCreating(org.spongepowered.api.world.storage.WorldProperties worldProperties) {
        Vector3i vector3i = worldProperties.getSpawnPosition();
        Vector3N spawnLocation = Vector3N.of(vector3i.getX(), vector3i.getY(), vector3i.getZ());

        return new WorldCreatingProperties(
                worldProperties.getWorldName(),
                worldProperties.getUniqueId(),
                worldProperties.loadOnStartup(),
                spawnLocation,
                worldProperties.getSeed(),
                fromSponge(worldProperties.getGameMode()),
                worldProperties.getGameRules()
        );
    }

    @NotNull
    public static Gamemode fromSponge(GameMode gamemode) {
        try {
            return Enum.valueOf(Gamemode.class, gamemode.toString());
        }
        catch (IllegalArgumentException | NullPointerException e) {
            return Gamemode.OTHER;
        }
    }

    public static Warp fromSponge(io.github.nucleuspowered.nucleus.api.nucleusdata.Warp warp) {
        org.dockbox.selene.api.objects.location.position.Location location = warp.getLocation()
                .map(SpongeConversionUtil::fromSponge)
                .orElse(org.dockbox.selene.api.objects.location.position.Location.empty());

        return new Warp(
                Exceptional.of(warp.getDescription().map(Text::toString)),
                Exceptional.of(warp.getCategory()),
                location,
                warp.getName()
        );
    }

    @NotNull
    public static org.dockbox.selene.api.objects.location.position.Location fromSponge(Location<World> location) {
        org.dockbox.selene.api.objects.location.dimensions.World world = fromSponge(location.getExtent());
        Vector3N vector3N = Vector3N.of(location.getX(), location.getY(), location.getZ());
        return new org.dockbox.selene.api.objects.location.position.Location(vector3N, world);
    }

    @NotNull
    public static org.dockbox.selene.api.objects.location.dimensions.World fromSponge(World world) {
        return fromSponge(world.getProperties());
    }

    public static org.dockbox.selene.api.objects.location.dimensions.World fromSponge(WorldProperties properties) {
        Vector3i vector3i = properties.getSpawnPosition();
        Vector3N spawnLocation = Vector3N.of(vector3i.getX(), vector3i.getY(), vector3i.getZ());
        org.dockbox.selene.api.objects.location.dimensions.World spongeWorld = new SpongeWorld(
                properties.getUniqueId(),
                properties.getWorldName(),
                properties.loadOnStartup(),
                spawnLocation,
                properties.getSeed(),
                fromSponge(properties.getGameMode())
        );
        properties.getGameRules().forEach(spongeWorld::setGamerule);
        return spongeWorld;
    }

    public static Chunk fromSponge(org.spongepowered.api.world.Chunk chunk) {
        return new SpongeChunk(chunk);
    }

    public static Hand fromSponge(HandType handType) {
        if (handType == HandTypes.MAIN_HAND) return Hand.MAIN_HAND;
        else if (handType == HandTypes.OFF_HAND) return Hand.OFF_HAND;
        throw new UncheckedSeleneException("Invalid value in context '" + handType + "'");
    }

    public static Element toSponge(org.dockbox.selene.api.inventory.Element element) {
        if (element instanceof SimpleElement) {
            return Element.of(toSponge(element.getItem()),
                    a -> ((SimpleElement) element).perform(fromSponge(a.getPlayer()))
            );
        }
        return Element.EMPTY;
    }

    @NotNull
    public static ItemStack toSponge(Item item) {
        if (item instanceof SpongeItem)
            // Create a copy of the ItemStack so Sponge doesn't modify the Item reference
            return ((SpongeItem) item).getReference().or(ItemStack.empty()).copy();
        return ItemStack.empty();
    }

    @NotNull
    public static Player fromSponge(org.spongepowered.api.entity.living.player.Player player) {
        return fromSponge((User) player);
    }

    @NotNull
    public static Player fromSponge(org.spongepowered.api.entity.living.player.User player) {
        return new SpongePlayer(player.getUniqueId(), player.getName());
    }

    public static org.dockbox.selene.api.inventory.Element fromSponge(Element element) {
        Item item = fromSponge(element.getItem().createStack());
        return org.dockbox.selene.api.inventory.Element.of(item); // Action is skipped here
    }

    @NotNull
    public static ReferencedItem<ItemStack> fromSponge(ItemStack item) {
        // Create a copy of the ItemStack so Sponge doesn't modify the Item reference
        return new SpongeItem(item.copy());
    }

    public static Clipboard fromSponge(com.sk89q.worldedit.extent.clipboard.Clipboard clipboard) {
        Region region = fromWorldEdit(clipboard.getRegion());
        Vector origin = clipboard.getOrigin();
        return new Clipboard(region, Vector3N.of(origin.getX(), origin.getY(), origin.getZ()));
    }

    public static Region fromWorldEdit(com.sk89q.worldedit.regions.Region region) {
        return new WrappedRegion(region);
    }

    public static com.sk89q.worldedit.extent.clipboard.Clipboard toWorldEdit(Clipboard clipboard) {
        com.sk89q.worldedit.regions.Region region = toWorldEdit(clipboard.getRegion());
        Vector3N origin = clipboard.getOrigin();
        com.sk89q.worldedit.extent.clipboard.Clipboard worldEditClipboard = new BlockArrayClipboard(region);
        worldEditClipboard.setOrigin(new Vector(origin.getXd(), origin.getYd(), origin.getZd()));
        return worldEditClipboard;
    }

    public static com.sk89q.worldedit.regions.Region toWorldEdit(Region region) {
        if (region instanceof WrappedRegion) {
            return ((WrappedRegion) region).getReference().orNull();
        }
        else {
            com.sk89q.worldedit.world.World world = toWorldEdit(region.getWorld());
            Vector3N min = region.getMinimumPoint();
            Vector3N max = region.getMaximumPoint();

            return new com.sk89q.worldedit.regions.CuboidRegion(
                    world,
                    new Vector(min.getXd(), min.getYd(), min.getZd()),
                    new Vector(max.getXd(), max.getYd(), max.getZd())
            );
        }
    }

    public static com.sk89q.worldedit.world.World toWorldEdit(org.dockbox.selene.api.objects.location.dimensions.World world) {
        return SpongeWorldEdit.inst().getAdapter().getWorld(toSponge(world).orNull());
    }

    public static FawePlayer<?> toWorldEdit(Player player) {
        return FawePlayer.wrap(toSponge(player).orNull());
    }

    public static Exceptional<org.spongepowered.api.entity.living.player.Player> toSponge(Player player) {
        if (player instanceof SpongePlayer) {
            return ((SpongePlayer) player).getSpongePlayer();
        }
        return Exceptional.none();
    }

    public static Vector3N fromWorldEdit(Vector vector) {
        return Vector3N.of(vector.getX(), vector.getY(), vector.getZ());
    }

    public static org.dockbox.selene.api.objects.location.dimensions.World fromWorldEdit(com.sk89q.worldedit.world.World world) {
        return Selene.provide(Worlds.class).getWorld(world.getName()).orNull();
    }

    public static Exceptional<BaseBlock> toWorldEdit(Item item, ParserContext context) {
        if (!item.isBlock())
            return Exceptional.of(new IllegalArgumentException("Cannot derive BaseBlock from non-block item"));
        return Exceptional.of(() -> WorldEdit.getInstance()
                .getBlockFactory()
                .parseFromInput(item.getId() + ':' + item.getMeta(), context)
        );
    }

    public static InventoryArchetype toSponge(InventoryType inventoryType) {
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
    }

    public static EquipmentType toSponge(Slot slot) {
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
    }

    public static Mask toWorldEdit(org.dockbox.selene.worldedit.region.Mask mask) {
        if (mask instanceof WrappedMask) {
            return ((WrappedMask) mask).getReference().orNull();
        }
        throw new IllegalStateException("Unknown implementation for Mask: [" + mask.getClass() + "]");
    }

    public static Pattern toWorldEdit(org.dockbox.selene.worldedit.region.Pattern pattern) {
        if (pattern instanceof WrappedPattern) {
            return ((WrappedPattern) pattern).getReference().orNull();
        }
        throw new IllegalStateException("Unknown implementation for Pattern: [" + pattern.getClass() + "]");
    }

    public static Vector3N fromSponge(Vector3d v3d) {
        return Vector3N.of(v3d.getX(), v3d.getY(), v3d.getZ());
    }

    public static Vector3d toSponge(Vector3N v3n) {
        return new Vector3d(v3n.getXd(), v3n.getYd(), v3n.getZd());
    }

    public static ItemFrame.Rotation fromSponge(Rotation rotation) {
        try {
            return ItemFrame.Rotation.valueOf(rotation.getName());
        }
        catch (IllegalArgumentException | NullPointerException e) {
            return ItemFrame.Rotation.TOP;
        }
    }

    public static Rotation toSponge(ItemFrame.Rotation rotation) {
        return Sponge.getRegistry().getType(Rotation.class, rotation.name()).orElse(Rotations.TOP);
    }

    public static Direction toSponge(BlockFace blockFace) {
        try {
            return Direction.valueOf(blockFace.name());
        }
        catch (IllegalArgumentException | NullPointerException e) {
            return Direction.NONE;
        }
    }

    public static BlockFace fromSponge(Direction direction) {
        try {
            return BlockFace.valueOf(direction.name());
        }
        catch (IllegalArgumentException | NullPointerException e) {
            return BlockFace.NONE;
        }
    }

    public static com.intellectualcrafters.plot.object.Location toPlotSquared(org.dockbox.selene.api.objects.location.position.Location location) {
        return new com.intellectualcrafters.plot.object.Location(
                location.getWorld().getName(),
                (int) location.getX(),
                (int) location.getY(),
                (int) location.getZ(),
                0, 0
        );
    }

    public static org.dockbox.selene.api.objects.location.position.Location fromPlotSquared(com.intellectualcrafters.plot.object.Location location) {
        org.dockbox.selene.api.objects.location.dimensions.World world = Selene.provide(Worlds.class).getWorld(location.getWorld()).orNull();
        return new org.dockbox.selene.api.objects.location.position.Location(
                location.getX(), location.getY(), location.getZ(), world
        );
    }

    public static Player fromPlotSquared(PlotPlayer player) {
        return new SpongePlayer(player.getUUID(), player.getName());
    }

    public static PlotPlayer toPlotSquared(Player player) {
        if (player instanceof SpongePlayer) {
            return PlotPlayer.wrap(((SpongePlayer) player).getSpongePlayer().orNull());
        }
        return PlotPlayer.get(player.getName());
    }

    public static Exceptional<PlotBlock> toPlotSquared(Item item) {
        if (!item.isBlock()) return Exceptional.none();
        int id = item.getIdNumeric();
        int meta = item.getMeta();
        // Casting is safe in this use-case, as this is the same approach used by PlotSquared (legacy) in PlotBlock itself
        return Exceptional.of(new PlotBlock((short) id, (byte) meta));
    }

    public static org.dockbox.selene.api.entities.Entity fromSponge(Entity entity) {
        EntityType type = entity.getType();
        if (type == EntityTypes.ARMOR_STAND) {
            return new SpongeArmorStand((ArmorStand) entity);
        }
        else if (type == EntityTypes.ITEM_FRAME) {
            return new SpongeItemFrame((org.spongepowered.api.entity.hanging.ItemFrame) entity);
        }
        else if (type == EntityTypes.PLAYER) {
            return new SpongePlayer(entity.getUniqueId(), ((org.spongepowered.api.entity.living.player.Player) entity).getName());
        }
        else {
            return new SpongeGenericEntity(entity);
        }
    }

    public static Set<Context> toSponge(PermissionContext context) {
        Set<Context> contexts = SeleneUtils.emptySet();

        if (!SeleneUtils.isEmpty(context.getDimension()))
            contexts.add(new Context(Context.DIMENSION_KEY, context.getDimension()));

        if (!SeleneUtils.isEmpty(context.getLocalHost()))
            contexts.add(new Context(Context.LOCAL_HOST_KEY, context.getLocalHost()));

        if (!SeleneUtils.isEmpty(context.getLocalIp()))
            contexts.add(new Context(Context.LOCAL_IP_KEY, context.getLocalIp()));

        if (!SeleneUtils.isEmpty(context.getRemoteIp()))
            contexts.add(new Context(Context.REMOTE_IP_KEY, context.getRemoteIp()));

        if (!SeleneUtils.isEmpty(context.getUser()))
            contexts.add(new Context(Context.USER_KEY, context.getUser()));

        if (!SeleneUtils.isEmpty(context.getWorld()))
            contexts.add(new Context(Context.WORLD_KEY, context.getWorld()));

        return contexts;
    }

    public static org.spongepowered.api.util.Tristate toSponge(Tristate state) {
        switch (state) {
            case TRUE:
                return org.spongepowered.api.util.Tristate.TRUE;
            case FALSE:
                return org.spongepowered.api.util.Tristate.FALSE;
            case UNDEFINED:
            default:
                return org.spongepowered.api.util.Tristate.UNDEFINED;
        }
    }

    public static SpawnSource fromSponge(SpawnType spawnType) {
        if (spawnType == SpawnTypes.BLOCK_SPAWNING) return SpawnSource.BLOCK;
        else if (spawnType == SpawnTypes.BREEDING) return SpawnSource.BREEDING;
        else if (spawnType == SpawnTypes.CHUNK_LOAD) return SpawnSource.CHUNK;
        else if (spawnType == SpawnTypes.CUSTOM) return SpawnSource.PLACEMENT;
        else if (spawnType == SpawnTypes.DISPENSE) return SpawnSource.DISPENSE;
        else if (spawnType == SpawnTypes.DROPPED_ITEM) return SpawnSource.DROP;
        else if (spawnType == SpawnTypes.EXPERIENCE) return SpawnSource.EXPERIENCE;
        else if (spawnType == SpawnTypes.FALLING_BLOCK) return SpawnSource.FALLING_BLOCK;
        else if (spawnType == SpawnTypes.MOB_SPAWNER) return SpawnSource.SPAWNER;
        else if (spawnType == SpawnTypes.PASSIVE) return SpawnSource.PLACEMENT;
        else if (spawnType == SpawnTypes.PLACEMENT) return SpawnSource.PLACEMENT;
        else if (spawnType == SpawnTypes.PLUGIN) return SpawnSource.PLACEMENT;
        else if (spawnType == SpawnTypes.PROJECTILE) return SpawnSource.PROJECTILE;
        else if (spawnType == SpawnTypes.SPAWN_EGG) return SpawnSource.SPAWN_EGG;
        else if (spawnType == SpawnTypes.STRUCTURE) return SpawnSource.STRUCTURE;
        else if (spawnType == SpawnTypes.TNT_IGNITE) return SpawnSource.TNT;
        else if (spawnType == SpawnTypes.WEATHER) return SpawnSource.WEATHER;
        else if (spawnType == SpawnTypes.WORLD_SPAWNER) return SpawnSource.WORLD;
        else return SpawnSource.PLACEMENT;
    }
}
