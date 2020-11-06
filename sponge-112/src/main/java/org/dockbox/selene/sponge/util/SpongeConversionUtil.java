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

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;

import org.dockbox.selene.core.events.world.WorldEvent.WorldCreatingProperties;
import org.dockbox.selene.core.i18n.entry.IntegratedResource;
import org.dockbox.selene.core.objects.item.Enchant;
import org.dockbox.selene.core.objects.item.Item;
import org.dockbox.selene.core.objects.location.Warp;
import org.dockbox.selene.core.objects.optional.Exceptional;
import org.dockbox.selene.core.objects.targets.CommandSource;
import org.dockbox.selene.core.objects.tuple.Vector3N;
import org.dockbox.selene.core.objects.user.Gamemode;
import org.dockbox.selene.core.objects.user.Player;
import org.dockbox.selene.core.text.actions.ClickAction;
import org.dockbox.selene.core.text.actions.HoverAction;
import org.dockbox.selene.core.text.actions.ShiftClickAction;
import org.dockbox.selene.core.text.navigation.Pagination;
import org.dockbox.selene.sponge.exceptions.TypeConversionException;
import org.dockbox.selene.sponge.object.item.SpongeItem;
import org.dockbox.selene.sponge.objects.location.SpongeWorld;
import org.dockbox.selene.sponge.objects.targets.SpongeConsole;
import org.dockbox.selene.sponge.objects.targets.SpongePlayer;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.source.ConsoleSource;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.item.enchantment.Enchantment;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public enum SpongeConversionUtil {
    ;

    @NotNull
    public static <T> Optional<?> autoDetectFromSponge(T object) {
        // CommandSource, Location, World, Gamemode
        if (object instanceof org.spongepowered.api.entity.living.player.Player) {
            return Optional.of(fromSponge((org.spongepowered.api.entity.living.player.Player) object));
        } else if (object instanceof org.spongepowered.api.command.CommandSource) {
            return fromSponge((org.spongepowered.api.command.CommandSource) object).toOptional();
        } else if (object instanceof Location) {
            return Optional.of(fromSponge((Location) object));
        } else if (object instanceof World) {
            return Optional.of(fromSponge((World) object));
        } else if (object instanceof GameMode) {
            return Optional.of(fromSponge((GameMode) object));
        } else if (object instanceof User) {
            return Optional.of(fromSponge((User) object));
        } else if (object instanceof ItemStack) {
            return Optional.of(fromSponge((ItemStack) object));
        } else if (object instanceof Enchantment) {
            return fromSponge((Enchantment) object).toOptional();
        }
        return Optional.empty();
    }

    @NotNull
    public static Optional<Enchantment> toSponge(Enchant enchantment) {
        // TODO GuusLieben, enchantment conversion (also update ItemStack conversions)
        return Optional.empty();
    }

    @NotNull
    public static ItemStack toSponge(Item<ItemStack> item) {
        // Create a copy of the ItemStack so Sponge doesn't modify the Item reference
        return item.getReference().orElse(ItemStack.empty()).copy();
    }

    @NotNull
    public static Text toSponge(org.dockbox.selene.core.text.Text message) {
        Iterable<org.dockbox.selene.core.text.Text> parts = message.getParts();
        Text.Builder b = Text.builder();
        AtomicBoolean isFirst = new AtomicBoolean(true);
        parts.forEach(part -> {
            // If we check the extra's of the first part, it'll endlessly loop here
            if (!isFirst.get() && !part.getExtra().isEmpty()) {
                b.append(toSponge(part));

            } else {
                Text.Builder pb = Text.builder();
                // Wrapping in parseColors first so internal color codes are parsed as well. Technically the FormattingCode
                // from TextSerializers won't be needed, but to ensure no trailing codes are left we use it here anyway.
                pb.append(TextSerializers.FORMATTING_CODE.deserialize(IntegratedResource.Companion.parseColors(part.toLegacy())));

                Optional<org.spongepowered.api.text.action.ClickAction<?>> clickAction = toSponge(part.getClickAction());
                clickAction.ifPresent(pb::onClick);

                Optional<org.spongepowered.api.text.action.HoverAction<?>> hoverAction = toSponge(part.getHoverAction());
                hoverAction.ifPresent(pb::onHover);

                Optional<org.spongepowered.api.text.action.ShiftClickAction<?>> shiftClickAction = toSponge(part.getShiftClickAction());
                shiftClickAction.ifPresent(pb::onShiftClick);

                b.append(pb.build());
            }
            isFirst.set(false);
        });

        return b.build();
    }

    @NotNull
    private static Optional<org.spongepowered.api.text.action.ShiftClickAction<?>> toSponge(ShiftClickAction<?> action) {
        if (null == action) return Optional.empty();
        Object result = action.getResult();
        if (action instanceof ShiftClickAction.InsertText) {
            return Optional.of(TextActions.insertText(((org.dockbox.selene.core.text.Text) result).toPlain()));
        }
        return Optional.empty();
    }

    @NotNull
    private static Optional<org.spongepowered.api.text.action.HoverAction<?>> toSponge(HoverAction<?> action) {
        if (null == action) return Optional.empty();
        Object result = action.getResult();
        if (action instanceof HoverAction.ShowText) {
            return Optional.of(TextActions.showText(toSponge(((org.dockbox.selene.core.text.Text) result))));
        }
        // TODO: Once implemented; ShowItem, ShowEntity
        return Optional.empty();
    }

    @NotNull
    private static Optional<org.spongepowered.api.text.action.ClickAction<?>> toSponge(ClickAction<?> action) {
        if (null == action) return Optional.empty();
        Object result = action.getResult();
        if (action instanceof ClickAction.OpenUrl) {
            return Optional.of(TextActions.openUrl((URL) result));
        } else if (action instanceof ClickAction.RunCommand) {
            return Optional.of(TextActions.runCommand((String) result));
        } else if (action instanceof ClickAction.ChangePage) {
            return Optional.of(TextActions.changePage((int) result));
        } else if (action instanceof ClickAction.SuggestCommand) {
            return Optional.of(TextActions.suggestCommand((String) result));
        } else if (action instanceof ClickAction.ExecuteCallback) {
            return Optional.of(TextActions.executeCallback(commandSource -> {
                Consumer<CommandSource> consumer = ((ClickAction.ExecuteCallback) action).getResult();
                try {
                    fromSponge(commandSource).ifPresent(consumer).rethrow();
                } catch (Throwable throwable) {
                    commandSource.sendMessage(Text.of(IntegratedResource.UNKNOWN_ERROR.format(throwable.getMessage())));
                }
            }));
        }
        return Optional.empty();
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
    public static Item<ItemStack> fromSponge(ItemStack item) {
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
            World wref = ((SpongeWorld) world).getReference();
            if (null != wref) return Exceptional.of(wref);
        }

        return Exceptional.ofSupplier(() -> Sponge.getServer().getWorld(world.getWorldUniqueId())
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

    @NotNull
    public static org.dockbox.selene.core.text.Text fromSponge(Text text) {
        // TODO: Scheduled in S42
        return org.dockbox.selene.core.text.Text.of();
    }

    @NotNull
    public static PaginationList toSponge(Pagination pagination) {
        PaginationList.Builder builder = PaginationList.builder();

        if (null != pagination.getTitle()) builder.title(toSponge(pagination.getTitle()));
        if (null != pagination.getHeader()) builder.header(toSponge(pagination.getHeader()));
        if (null != pagination.getFooter()) builder.footer(toSponge(pagination.getFooter()));

        builder.padding(toSponge(pagination.getPadding()));
        builder.linesPerPage(pagination.getLinesPerPage().intValue());
        List<Text> convertedContent = pagination.getContent().stream().map(SpongeConversionUtil::toSponge).collect(Collectors.toList());
        builder.contents(convertedContent);
        return builder.build();
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
        if (commandSource instanceof ConsoleSource) return Exceptional.of(SpongeConsole.Companion.getInstance());
        else if (commandSource instanceof org.spongepowered.api.entity.living.player.Player)
            return Exceptional.of(fromSponge((org.spongepowered.api.entity.living.player.Player) commandSource));
        return Exceptional.of(new TypeConversionException("Could not convert CommandSource type '" + commandSource.getClass().getCanonicalName() + "'"));
    }

    @NotNull
    public static org.dockbox.selene.core.objects.location.World fromSponge(World world) {
        Vector3i vector3i = world.getProperties().getSpawnPosition();
        Vector3N spawnLocation = new Vector3N(vector3i.getX(), vector3i.getY(), vector3i.getZ());

        return new SpongeWorld(
                world.getUniqueId(),
                world.getName(),
                world.getProperties().loadOnStartup(),
                spawnLocation,
                world.getProperties().getSeed(),
                fromSponge(world.getProperties().getGameMode()),
                world.getProperties().getGameRules()
        );
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
                .orElse(org.dockbox.selene.core.objects.location.Location.Companion.getEMPTY());

        return new Warp(
                warp.getDescription().map(Text::toString),
                warp.getCategory(),
                location,
                warp.getName()
        );
    }
}
