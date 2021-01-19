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
import com.magitechserver.magibridge.util.BridgeCommandSource;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.function.mask.Mask;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.sponge.SpongeWorldEdit;

import org.dockbox.selene.core.WorldStorageService;
import org.dockbox.selene.core.command.source.CommandSource;
import org.dockbox.selene.core.events.world.WorldEvent.WorldCreatingProperties;
import org.dockbox.selene.core.exceptions.TypeConversionException;
import org.dockbox.selene.core.exceptions.global.CheckedSeleneException;
import org.dockbox.selene.core.exceptions.global.UncheckedSeleneException;
import org.dockbox.selene.core.external.region.Clipboard;
import org.dockbox.selene.core.external.region.Region;
import org.dockbox.selene.core.i18n.entry.IntegratedResource;
import org.dockbox.selene.core.impl.objects.item.ReferencedItem;
import org.dockbox.selene.core.inventory.InventoryType;
import org.dockbox.selene.core.objects.Console;
import org.dockbox.selene.core.objects.Exceptional;
import org.dockbox.selene.core.objects.bossbar.BossbarColor;
import org.dockbox.selene.core.objects.bossbar.BossbarStyle;
import org.dockbox.selene.core.objects.inventory.Slot;
import org.dockbox.selene.core.objects.item.Enchant;
import org.dockbox.selene.core.objects.item.Item;
import org.dockbox.selene.core.objects.location.Warp;
import org.dockbox.selene.core.objects.player.Gamemode;
import org.dockbox.selene.core.objects.player.Hand;
import org.dockbox.selene.core.objects.player.Player;
import org.dockbox.selene.core.objects.special.Sounds;
import org.dockbox.selene.core.objects.targets.Identifiable;
import org.dockbox.selene.core.objects.tuple.Vector3N;
import org.dockbox.selene.core.server.Selene;
import org.dockbox.selene.core.text.actions.ClickAction;
import org.dockbox.selene.core.text.actions.HoverAction;
import org.dockbox.selene.core.text.actions.ShiftClickAction;
import org.dockbox.selene.core.text.pagination.Pagination;
import org.dockbox.selene.core.util.SeleneUtils;
import org.dockbox.selene.sponge.external.WrappedRegion;
import org.dockbox.selene.sponge.inventory.SpongeElement;
import org.dockbox.selene.sponge.objects.discord.MagiBridgeCommandSource;
import org.dockbox.selene.sponge.objects.item.SpongeItem;
import org.dockbox.selene.sponge.objects.location.SpongeWorld;
import org.dockbox.selene.sponge.objects.targets.SpongeConsole;
import org.dockbox.selene.sponge.objects.targets.SpongePlayer;
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
import org.spongepowered.api.entity.Tamer;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.item.enchantment.Enchantment;
import org.spongepowered.api.item.enchantment.EnchantmentType;
import org.spongepowered.api.item.inventory.InventoryArchetype;
import org.spongepowered.api.item.inventory.InventoryArchetypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.equipment.EquipmentType;
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
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import dev.flashlabs.flashlibs.inventory.Element;

@SuppressWarnings({"ClassWithTooManyMethods", "OverlyComplexClass", "unchecked", "OverlyStrongTypeCast"})
public enum SpongeConversionUtil {
    ;

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

    @NotNull
    public static <T> Exceptional<?> autoDetectFromSponge(T object) {
        // CommandSource, Location, World, Gamemode
        if (object instanceof org.spongepowered.api.entity.living.player.Player) {
            return Exceptional.of(fromSponge((org.spongepowered.api.entity.living.player.Player) object));
        } else if (object instanceof org.spongepowered.api.command.CommandSource) {
            return fromSponge((org.spongepowered.api.command.CommandSource) object);
        } else if (object instanceof Location) {
            return Exceptional.of(fromSponge((Location<World>) object));
        } else if (object instanceof World) {
            return Exceptional.of(fromSponge((World) object));
        } else if (object instanceof GameMode) {
            return Exceptional.of(fromSponge((GameMode) object));
        } else if (object instanceof User) {
            return Exceptional.of(new SpongePlayer(((Identifiable<?>) object).getUniqueId(), ((Tamer) object).getName()));
        } else if (object instanceof ItemStack) {
            return Exceptional.of(fromSponge((ItemStack) object));
        } else if (object instanceof Enchantment) {
            return fromSponge((Enchantment) object);
        }
        return Exceptional.empty();
    }

    @NotNull
    public static Exceptional<Enchantment> toSponge(Enchant enchantment) {
        Exceptional<EnchantmentType> type = Exceptional.of(Sponge.getRegistry()
                .getType(EnchantmentType.class, enchantment.getEnchantment().name()));

        return type.map(enchantmentType -> Enchantment.builder()
                .type(enchantmentType)
                .level(enchantment.getLevel())
                .build()
        );
    }

    public static BossBarColor toSponge(BossbarColor bossbarColor) {
        return Sponge.getRegistry().getType(BossBarColor.class, bossbarColor.name())
                .orElse(BossBarColors.WHITE);
    }

    public static BossBarOverlay toSponge(BossbarStyle style) {
        return Sponge.getRegistry().getType(BossBarOverlay.class, style.name())
                .orElse(BossBarOverlays.PROGRESS);
    }

    @NotNull
    public static Exceptional<SoundType> toSponge(Sounds sound) {
        return Exceptional.of(Sponge.getRegistry().getType(SoundType.class, sound.name()));
    }

    @NotNull
    public static Exceptional<? extends org.spongepowered.api.command.CommandSource> toSponge(CommandSource src) {
        if (src instanceof Console) return Exceptional.of(Sponge.getServer().getConsole());
        else if (src instanceof Player)
            return Exceptional.of(Sponge.getServer().getPlayer(((Player) src).getUniqueId()));
        return Exceptional.empty();
    }

    public static Exceptional<org.spongepowered.api.entity.living.player.Player> toSponge(Player player) {
        if (player instanceof SpongePlayer) {
            return ((SpongePlayer) player).getSpongePlayer();
        }
        return Exceptional.empty();
    }

    @NotNull
    public static PaginationList toSponge(Pagination pagination) {
        PaginationList.Builder builder = PaginationList.builder();

        if (null != pagination.getTitle()) builder.title(toSponge(pagination.getTitle()));
        if (null != pagination.getHeader()) builder.header(toSponge(pagination.getHeader()));
        if (null != pagination.getFooter()) builder.footer(toSponge(pagination.getFooter()));

        builder.padding(toSponge(pagination.getPadding()));
        if (0 < pagination.getLinesPerPage())
            builder.linesPerPage(pagination.getLinesPerPage());
        List<Text> convertedContent = pagination.getContent().stream().map(SpongeConversionUtil::toSponge).collect(Collectors.toList());
        builder.contents(convertedContent);
        return builder.build();
    }

    @NotNull
    public static ItemStack toSponge(Item item) {
        if (item instanceof SpongeItem)
            // Create a copy of the ItemStack so Sponge doesn't modify the Item reference
            return ((SpongeItem) item).getReference().orElse(ItemStack.empty()).copy();
        return ItemStack.empty();
    }

    @NotNull
    public static Text toSponge(org.dockbox.selene.core.text.Text message) {
        Iterable<org.dockbox.selene.core.text.Text> parts = message.getParts();
        Text.Builder b = Text.builder();
        parts.forEach(part -> {
            Text.Builder pb = Text.builder();
            // Wrapping in parseColors first so internal color codes are parsed as well. Technically the FormattingCode
            // from TextSerializers won't be needed, but to ensure no trailing codes are left we use it here anyway.
            pb.append(TextSerializers.FORMATTING_CODE.deserialize(IntegratedResource.parse(part.toLegacy())));

            Exceptional<org.spongepowered.api.text.action.ClickAction<?>> clickAction = toSponge(part.getClickAction());
            clickAction.ifPresent(pb::onClick);

            Exceptional<org.spongepowered.api.text.action.HoverAction<?>> hoverAction = toSponge(part.getHoverAction());
            hoverAction.ifPresent(pb::onHover);

            Exceptional<org.spongepowered.api.text.action.ShiftClickAction<?>> shiftClickAction = toSponge(part.getShiftClickAction());
            shiftClickAction.ifPresent(pb::onShiftClick);

            b.append(pb.build());
        });

        return b.build();
    }

    @NotNull
    private static Exceptional<org.spongepowered.api.text.action.ShiftClickAction<?>> toSponge(ShiftClickAction<?> action) {
        if (null == action) return Exceptional.empty();
        Object result = action.getResult();
        if (action instanceof ShiftClickAction.InsertText) {
            return Exceptional.of(TextActions.insertText(((org.dockbox.selene.core.text.Text) result).toPlain()));
        }
        return Exceptional.empty();
    }

    @NotNull
    private static Exceptional<org.spongepowered.api.text.action.HoverAction<?>> toSponge(HoverAction<?> action) {
        if (null == action) return Exceptional.empty();
        Object result = action.getResult();
        if (action instanceof HoverAction.ShowText) {
            return Exceptional.of(TextActions.showText(toSponge(((org.dockbox.selene.core.text.Text) result))));
        }
        return Exceptional.empty();
    }

    @NotNull
    private static Exceptional<org.spongepowered.api.text.action.ClickAction<?>> toSponge(ClickAction<?> action) {
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
                    fromSponge(commandSource).ifPresent(consumer).rethrow();
                } catch (CheckedSeleneException throwable) {
                    commandSource.sendMessage(Text.of(IntegratedResource.UNKNOWN_ERROR.format(throwable.getMessage())));
                }
            }));
        }
        return Exceptional.empty();
    }

    @NotNull
    public static Exceptional<Location<World>> toSponge(org.dockbox.selene.core.objects.location.Location location) {
        Exceptional<World> world = toSponge(location.getWorld());
        if (world.errorPresent()) return Exceptional.of(world.getError());
        if (!world.isPresent()) return Exceptional.empty();
        Vector3d vector3d = new Vector3d(location.getVectorLoc().getXd(), location.getVectorLoc().getYd(), location.getVectorLoc().getZd());
        return Exceptional.of(new Location<>(world.get(), vector3d));
    }

    @NotNull
    public static Exceptional<Enchant> fromSponge(Enchantment enchantment) {
        try {
            String id = enchantment.getType().getId();
            int level = enchantment.getLevel();
            Enchant enchant = new Enchant(org.dockbox.selene.core.objects.item.Enchantment.valueOf(id.toUpperCase()), level);
            return Exceptional.of(enchant);
        } catch (IllegalArgumentException | NullPointerException e) {
            return Exceptional.of(e);
        }
    }

    @NotNull
    public static ReferencedItem<ItemStack> fromSponge(ItemStack item) {
        // Create a copy of the ItemStack so Sponge doesn't modify the Item reference
        return new SpongeItem(item.copy());
    }

    @NotNull
    public static org.dockbox.selene.core.objects.location.Location fromSponge(Location<World> location) {
        org.dockbox.selene.core.objects.location.World world = fromSponge(location.getExtent());
        Vector3N vector3N = new Vector3N(location.getX(), location.getY(), location.getZ());
        return new org.dockbox.selene.core.objects.location.Location(vector3N, world);
    }

    @NotNull
    public static Exceptional<World> toSponge(org.dockbox.selene.core.objects.location.World world) {
        if (world instanceof SpongeWorld) {
            World wref = ((SpongeWorld) world).getReferenceWorld();
            if (null != wref) return Exceptional.of(wref);
        }

        return Exceptional.of(() -> Sponge.getServer().getWorld(world.getWorldUniqueId())
                .orElseThrow(() -> new RuntimeException("World reference not present on server")));
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

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @NotNull
    public static org.dockbox.selene.core.text.Text fromSponge(Text text) {
        String style = fromSponge(text.getFormat().getStyle());
        String color = fromSponge(text.getFormat().getColor());
        String value = text.toPlainSingle();

        org.dockbox.selene.core.text.Text t = org.dockbox.selene.core.text.Text.of(color + style + value);

        text.getClickAction().map(SpongeConversionUtil::fromSponge)
                .ifPresent(action -> action.ifPresent(t::onClick));
        text.getHoverAction().map(SpongeConversionUtil::fromSponge)
                .ifPresent(action -> action.ifPresent(t::onHover));
        text.getShiftClickAction().map(SpongeConversionUtil::fromSponge)
                .ifPresent(action -> action.ifPresent(t::onShiftClick));

        // Last step
        text.getChildren().stream().map(SpongeConversionUtil::fromSponge).forEach(t::append);
        return t;
    }

    private static Exceptional<ShiftClickAction<?>> fromSponge(org.spongepowered.api.text.action.ShiftClickAction<?> shiftClickAction) {
        if (shiftClickAction instanceof InsertText) {
            return Exceptional.of(ShiftClickAction.insertText(org.dockbox.selene.core.text.Text.of(
                    ((InsertText) shiftClickAction).getResult()))
            );
        } else return Exceptional.empty();
    }

    private static Exceptional<HoverAction<?>> fromSponge(org.spongepowered.api.text.action.HoverAction<?> hoverAction) {
        if (hoverAction instanceof ShowText) {
            return Exceptional.of(HoverAction.showText(fromSponge(((ShowText) hoverAction).getResult())));
        } else return Exceptional.empty();
    }

    private static Exceptional<ClickAction<?>> fromSponge(org.spongepowered.api.text.action.ClickAction<?> clickAction) {
        if (clickAction instanceof OpenUrl) {
            return Exceptional.of(ClickAction.openUrl(((OpenUrl) clickAction).getResult()));

        } else if (clickAction instanceof RunCommand) {
            return Exceptional.of(ClickAction.runCommand((((RunCommand) clickAction).getResult())));

        } else if (clickAction instanceof ChangePage) {
            return Exceptional.of(ClickAction.changePage((((ChangePage) clickAction).getResult())));

        } else if (clickAction instanceof SuggestCommand) {
            return Exceptional.of(ClickAction.suggestCommand((((SuggestCommand) clickAction).getResult())));

        } else if (clickAction instanceof ExecuteCallback) {
            return Exceptional.of(ClickAction.executeCallback(src -> toSponge(src)
                    .ifPresent(ssrc -> ((ExecuteCallback) clickAction).getResult().accept(ssrc))
                    .ifAbsent(() -> Selene.log().warn("Attempted to execute callback with unknown source type '" + src + "', is it convertable?"))
            ));

        } else return Exceptional.empty();
    }

    private static String fromSponge(TextColor color) {
        return org.dockbox.selene.core.text.Text.sectionSymbol + textColors.getOrDefault(color, 'f') + "";
    }

    private static String fromSponge(TextStyle style) {
        final char styleChar = org.dockbox.selene.core.text.Text.sectionSymbol;
        String styleString = styleChar + "r";
        if (SeleneUtils.unwrap(style.isBold())) styleString += styleChar + 'l';
        if (SeleneUtils.unwrap(style.isItalic())) styleString += styleChar + 'o';
        if (SeleneUtils.unwrap(style.isObfuscated())) styleString += styleChar + 'k';
        if (SeleneUtils.unwrap(style.hasUnderline())) styleString += styleChar + 'n';
        if (SeleneUtils.unwrap(style.hasStrikethrough())) styleString += styleChar + 'm';
        return styleString;
    }


    @NotNull
    public static Player fromSponge(org.spongepowered.api.entity.living.player.Player player) {
        return fromSponge((User) player);
    }

    @NotNull
    public static Player fromSponge(org.spongepowered.api.entity.living.player.User player) {
        return new SpongePlayer(player.getUniqueId(), player.getName());
    }

    @NotNull
    public static Exceptional<CommandSource> fromSponge(org.spongepowered.api.command.CommandSource commandSource) {
        if (commandSource instanceof ConsoleSource) return Exceptional.of(SpongeConsole.getInstance());
        else if (commandSource instanceof org.spongepowered.api.entity.living.player.Player)
            return Exceptional.of(fromSponge((org.spongepowered.api.entity.living.player.Player) commandSource));
        else if (commandSource instanceof BridgeCommandSource)
            return Exceptional.of(new MagiBridgeCommandSource((BridgeCommandSource) commandSource));
        return Exceptional.of(new TypeConversionException(commandSource.getClass(), CommandSource.class));
    }

    @NotNull
    public static org.dockbox.selene.core.objects.location.World fromSponge(World world) {
        Vector3i vector3i = world.getProperties().getSpawnPosition();
        Vector3N spawnLocation = new Vector3N(vector3i.getX(), vector3i.getY(), vector3i.getZ());

        org.dockbox.selene.core.objects.location.World spongeWorld = new SpongeWorld(
                world.getUniqueId(),
                world.getName(),
                world.getProperties().loadOnStartup(),
                spawnLocation,
                world.getProperties().getSeed(),
                fromSponge(world.getProperties().getGameMode())
        );
        world.getProperties().getGameRules().forEach(spongeWorld::setGamerule);
        return spongeWorld;
    }

    @NotNull
    public static Gamemode fromSponge(GameMode gamemode) {
        try {
            return Enum.valueOf(Gamemode.class, gamemode.toString());
        } catch (IllegalArgumentException | NullPointerException e) {
            return Gamemode.OTHER;
        }
    }

    @NotNull
    public static WorldCreatingProperties fromSponge(org.spongepowered.api.world.storage.WorldProperties worldProperties) {
        Vector3i vector3i = worldProperties.getSpawnPosition();
        Vector3N spawnLocation = new Vector3N(vector3i.getX(), vector3i.getY(), vector3i.getZ());

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

    public static Warp fromSponge(io.github.nucleuspowered.nucleus.api.nucleusdata.Warp warp) {
        org.dockbox.selene.core.objects.location.Location location = warp.getLocation()
                .map(SpongeConversionUtil::fromSponge)
                .orElse(org.dockbox.selene.core.objects.location.Location.empty());

        return new Warp(
                Exceptional.of(warp.getDescription().map(Text::toString)),
                Exceptional.of(warp.getCategory()),
                location,
                warp.getName()
        );
    }

    public static Hand fromSponge(HandType handType) {
        if (handType == HandTypes.MAIN_HAND) return Hand.MAIN_HAND;
        else if (handType == HandTypes.OFF_HAND) return Hand.OFF_HAND;
        throw new UncheckedSeleneException("Invalid value in context '" + handType + "'");
    }

    public static Element toSponge(org.dockbox.selene.core.inventory.Element element) {
        if (element instanceof SpongeElement) {
            return Element.of(toSponge(element.getItem()), a -> ((SpongeElement) element).perform(fromSponge(a.getPlayer())));
        }
        return Element.EMPTY;
    }

    public static org.dockbox.selene.core.inventory.Element fromSponge(Element element) {
        Item item = fromSponge(element.getItem().createStack());
        return org.dockbox.selene.core.inventory.Element.of(item); // Action is skipped here
    }

    public static Region fromWorldEdit(com.sk89q.worldedit.regions.Region region) {
        return new WrappedRegion(region);
    }

    public static com.sk89q.worldedit.regions.Region toWorldEdit(Region region) {
        if (region instanceof WrappedRegion) {
            return ((WrappedRegion) region).getReference().orNull();
        } else {
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

    public static Clipboard fromSponge(com.sk89q.worldedit.extent.clipboard.Clipboard clipboard) {
        Region region = fromWorldEdit(clipboard.getRegion());
        Vector origin = clipboard.getOrigin();
        return new Clipboard(region, new Vector3N(origin.getX(), origin.getY(), origin.getZ()));
    }

    public static com.sk89q.worldedit.extent.clipboard.Clipboard toWorldEdit(Clipboard clipboard) {
        com.sk89q.worldedit.regions.Region region = toWorldEdit(clipboard.getRegion());
        Vector3N origin = clipboard.getOrigin();
        com.sk89q.worldedit.extent.clipboard.Clipboard worldEditClipboard = new BlockArrayClipboard(region);
        worldEditClipboard.setOrigin(new Vector(origin.getXd(), origin.getYd(), origin.getZd()));
        return worldEditClipboard;
    }

    public static com.sk89q.worldedit.world.World toWorldEdit(org.dockbox.selene.core.objects.location.World world) {
        return SpongeWorldEdit.inst().getAdapter().getWorld(toSponge(world).orNull());
    }

    public static FawePlayer<?> toWorldEdit(Player player) {
        return FawePlayer.wrap(toSponge(player));
    }

    public static Vector3N fromWorldEdit(Vector vector) {
        return new Vector3N(vector.getX(), vector.getY(), vector.getZ());
    }

    public static org.dockbox.selene.core.objects.location.World fromWorldEdit(com.sk89q.worldedit.world.World world) {
        return Selene.provide(WorldStorageService.class).getWorld(world.getName()).orNull();
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

    public static Mask toWorldEdit(org.dockbox.selene.core.external.pattern.Mask mask) {
        return null;
    }

    public static Pattern toWorldEdit(org.dockbox.selene.core.external.pattern.Pattern pattern) {
        return null;
    }
}
