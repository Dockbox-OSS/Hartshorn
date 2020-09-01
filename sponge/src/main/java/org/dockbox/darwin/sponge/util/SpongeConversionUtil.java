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

package org.dockbox.darwin.sponge.util;

import com.flowpowered.math.vector.Vector3d;

import org.dockbox.darwin.core.objects.targets.CommandSource;
import org.dockbox.darwin.core.objects.tuple.Vector3D;
import org.dockbox.darwin.core.objects.user.Gamemode;
import org.dockbox.darwin.core.text.actions.ClickAction;
import org.dockbox.darwin.core.text.actions.HoverAction;
import org.dockbox.darwin.core.text.actions.ShiftClickAction;
import org.dockbox.darwin.sponge.exceptions.TypeConversionException;
import org.dockbox.darwin.sponge.objects.location.SpongeLocation;
import org.dockbox.darwin.sponge.objects.location.SpongeWorld;
import org.dockbox.darwin.sponge.objects.targets.SpongeConsole;
import org.dockbox.darwin.sponge.objects.targets.SpongePlayer;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.source.ConsoleSource;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.net.URL;
import java.util.function.Consumer;

public class SpongeConversionUtil {

    @NotNull
    public static Text toSponge(org.dockbox.darwin.core.text.Text message) {
        Iterable<org.dockbox.darwin.core.text.Text> parts = message.getParts();
        Text.Builder b = Text.builder();
        parts.forEach(part -> {

            if (part.getParts().size() > 1) {
                b.append(toSponge(part));

            } else {
                Text.Builder pb = Text.builder();
                pb.append(TextSerializers.FORMATTING_CODE.deserialize(part.toLegacy()));

                org.spongepowered.api.text.action.ClickAction<?> clickAction = toSponge(part.getClickAction());
                if (clickAction != null) pb.onClick(clickAction);

                org.spongepowered.api.text.action.HoverAction<?> hoverAction = toSponge(part.getHoverAction());
                if (hoverAction != null) pb.onHover(hoverAction);

                org.spongepowered.api.text.action.ShiftClickAction<?> shiftClickAction = toSponge(part.getShiftClickAction());
                if (shiftClickAction != null) pb.onShiftClick(shiftClickAction);

                b.append(pb.build());
            }
        });

        return b.build();
    }

    private static org.spongepowered.api.text.action.ShiftClickAction<?> toSponge(ShiftClickAction<?> action) {
        if (action == null) return null;
        Object result = action.getResult();
        if (action instanceof ShiftClickAction.InsertText) {
            return TextActions.insertText(((org.dockbox.darwin.core.text.Text) result).toPlain());
        }
        return null;
    }

    private static org.spongepowered.api.text.action.HoverAction<?> toSponge(HoverAction<?> action) {
        if (action == null) return null;
        Object result = action.getResult();
        if (action instanceof HoverAction.ShowText) {
            return TextActions.showText(TextSerializers.FORMATTING_CODE.deserialize(((org.dockbox.darwin.core.text.Text) result).toLegacy()));
        }
        // TODO: Once implemented; ShowItem, ShowEntity
        return null;
    }

    private static org.spongepowered.api.text.action.ClickAction<?> toSponge(ClickAction<?> action) {
        if (action == null) return null;
        Object result = action.getResult();
        if (action instanceof ClickAction.OpenUrl) {
            return TextActions.openUrl((URL) result);
        } else if (action instanceof ClickAction.RunCommand) {
            return TextActions.runCommand((String) result);
        } else if (action instanceof ClickAction.ChangePage) {
            return TextActions.changePage((int) result);
        } else if (action instanceof ClickAction.SuggestCommand) {
            return TextActions.suggestCommand((String) result);
        } else if (action instanceof ClickAction.ExecuteCallback) {
            return TextActions.executeCallback(commandSource -> {
                Consumer<CommandSource> consumer = ((ClickAction.ExecuteCallback) action).getResult();
                consumer.accept(fromSponge(commandSource));
            });
        }
        return null;
    }

    private static CommandSource fromSponge(org.spongepowered.api.command.CommandSource commandSource) {
        if (commandSource instanceof ConsoleSource) return SpongeConsole.Companion.getInstance();
        else if (commandSource instanceof org.spongepowered.api.entity.living.player.Player)
            return new SpongePlayer(((org.spongepowered.api.entity.living.player.Player) commandSource).getUniqueId(), commandSource.getName());
        throw new TypeConversionException("Could not convert CommandSource type '" + commandSource.getClass().getCanonicalName() + "'");
    }

    @NotNull
    public static Location<World> toSponge(org.dockbox.darwin.core.objects.location.Location location) {
        World world = toSponge(location.getWorld());
        Vector3d vector3d = new Vector3d(location.getX().doubleValue(), location.getY().doubleValue(), location.getZ().doubleValue());
        return new Location<>(world, vector3d);
    }

    @NotNull
    public static org.dockbox.darwin.core.objects.location.Location fromSponge(Location<World> location) {
        org.dockbox.darwin.core.objects.location.World world = fromSponge(location.getExtent());
        Vector3D vector3D = new Vector3D(location.getX(), location.getY(), location.getZ());
        return new SpongeLocation(vector3D, world);
    }

    @NotNull
    public static World toSponge(org.dockbox.darwin.core.objects.location.World world) {
        if (world instanceof SpongeWorld) {
            World wref = ((SpongeWorld) world).getReference();
            if (wref != null) return wref;
        }

        return Sponge.getServer().getWorld(world.getWorldUniqueId())
                .orElseThrow(() -> new RuntimeException("World reference not present on server"));
    }

    @NotNull
    public static org.dockbox.darwin.core.objects.location.World fromSponge(World world) {
        return new SpongeWorld(world.getUniqueId(), world.getName());
    }

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

    public static Gamemode fromSponge(GameMode gamemode) {
        try {
            return Enum.valueOf(Gamemode.class, gamemode.toString());
        } catch (IllegalArgumentException | NullPointerException e) {
            return Gamemode.OTHER;
        }
    }
}
