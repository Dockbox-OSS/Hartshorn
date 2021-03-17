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

package org.dockbox.selene.sponge.listeners;

import com.flowpowered.math.vector.Vector3d;

import org.dockbox.selene.api.events.chat.SendChatEvent;
import org.dockbox.selene.api.events.moderation.BanEvent.IpBannedEvent;
import org.dockbox.selene.api.events.moderation.BanEvent.IpUnbannedEvent;
import org.dockbox.selene.api.events.moderation.BanEvent.NameBannedEvent;
import org.dockbox.selene.api.events.moderation.BanEvent.NameUnbannedEvent;
import org.dockbox.selene.api.events.moderation.BanEvent.PlayerBannedEvent;
import org.dockbox.selene.api.events.moderation.BanEvent.PlayerUnbannedEvent;
import org.dockbox.selene.api.events.moderation.KickEvent;
import org.dockbox.selene.api.events.moderation.NoteEvent;
import org.dockbox.selene.api.events.moderation.WarnEvent;
import org.dockbox.selene.api.events.moderation.WarnEvent.PlayerWarnedEvent;
import org.dockbox.selene.api.events.parents.Cancellable;
import org.dockbox.selene.api.events.parents.Event;
import org.dockbox.selene.api.events.player.PlayerConnectionEvent.PlayerAuthEvent;
import org.dockbox.selene.api.events.player.PlayerConnectionEvent.PlayerJoinEvent;
import org.dockbox.selene.api.events.player.PlayerConnectionEvent.PlayerLeaveEvent;
import org.dockbox.selene.api.events.player.PlayerMoveEvent.PlayerPortalEvent;
import org.dockbox.selene.api.events.player.PlayerMoveEvent.PlayerSwitchWorldEvent;
import org.dockbox.selene.api.events.player.PlayerMoveEvent.PlayerTeleportEvent;
import org.dockbox.selene.api.events.player.PlayerMoveEvent.PlayerWarpEvent;
import org.dockbox.selene.api.events.player.interact.PlayerInteractEvent.PlayerInteractAirEvent;
import org.dockbox.selene.api.events.player.interact.PlayerInteractEvent.PlayerInteractBlockEvent;
import org.dockbox.selene.api.events.player.interact.PlayerInteractEvent.PlayerInteractEntityEvent;
import org.dockbox.selene.api.objects.Exceptional;
import org.dockbox.selene.api.objects.location.Location;
import org.dockbox.selene.api.objects.location.Warp;
import org.dockbox.selene.api.objects.player.ClickType;
import org.dockbox.selene.api.objects.player.Hand;
import org.dockbox.selene.api.objects.special.PortalType;
import org.dockbox.selene.api.server.Selene;
import org.dockbox.selene.api.util.SeleneUtils;
import org.dockbox.selene.sponge.entities.SpongeArmorStand;
import org.dockbox.selene.sponge.entities.SpongeItemFrame;
import org.dockbox.selene.sponge.util.SpongeConversionUtil;
import org.jetbrains.annotations.NonNls;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.entity.hanging.ItemFrame;
import org.spongepowered.api.entity.living.ArmorStand;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.entity.living.humanoid.HandInteractEvent;
import org.spongepowered.api.event.entity.living.humanoid.player.KickPlayerEvent;
import org.spongepowered.api.event.filter.Getter;
import org.spongepowered.api.event.filter.IsCancelled;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.message.MessageChannelEvent;
import org.spongepowered.api.event.network.BanIpEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.event.network.PardonIpEvent;
import org.spongepowered.api.event.user.BanUserEvent;
import org.spongepowered.api.event.user.PardonUserEvent;
import org.spongepowered.api.network.RemoteConnection;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.Tristate;
import org.spongepowered.api.util.ban.Ban;
import org.spongepowered.api.world.World;

import java.time.LocalDateTime;
import java.util.function.Consumer;

import io.github.nucleuspowered.nucleus.api.events.NucleusMuteEvent;
import io.github.nucleuspowered.nucleus.api.events.NucleusNameBanEvent;
import io.github.nucleuspowered.nucleus.api.events.NucleusNoteEvent;
import io.github.nucleuspowered.nucleus.api.events.NucleusWarnEvent;
import io.github.nucleuspowered.nucleus.api.events.NucleusWarpEvent;

public class SpongePlayerListener {

    @Listener
    public void onPlayerConnected(ClientConnectionEvent.Join joinEvent, @First Player sp) {
        new PlayerJoinEvent(SpongeConversionUtil.fromSponge(sp)).post();
    }

    @Listener
    public void onPlayerDisconnected(
            ClientConnectionEvent.Disconnect disconnectEvent, @First Player sp) {
        new PlayerLeaveEvent(SpongeConversionUtil.fromSponge(sp)).post();
    }

    @Listener
    public void onPlayerAuthenticating(
            ClientConnectionEvent.Auth authEvent, @Getter("getConnection") RemoteConnection connection) {
        new PlayerAuthEvent(connection.getAddress(), connection.getVirtualHost()).post();
    }

    @Listener
    public void onPlayerWarp(NucleusWarpEvent.Use warpEvent, @Getter("getTargetUser") User user) {
        Warp warp = SpongeConversionUtil.fromSponge(warpEvent.getWarp());
        Cancellable event = new PlayerWarpEvent(SpongeConversionUtil.fromSponge(user), warp).post();
        warpEvent.setCancelled(event.isCancelled());
    }

    @Listener
    public void onPlayerTeleport(
            MoveEntityEvent.Teleport teleportEvent,
            @First Player player,
            @Getter("getFromTransform") Transform<World> from,
            @Getter("getToTransform") Transform<World> to) {
        Location fromLocation = SpongeConversionUtil.fromSponge(from.getLocation());
        Location toLocation = SpongeConversionUtil.fromSponge(to.getLocation());

        PlayerTeleportEvent event =
                new PlayerTeleportEvent(SpongeConversionUtil.fromSponge(player), fromLocation, toLocation);
        event.post();
        teleportEvent.setCancelled(event.isCancelled());

        if (!fromLocation.getWorld().equals(toLocation.getWorld()) && !event.isCancelled()) {
            Cancellable worldEvent =
                    new PlayerSwitchWorldEvent(
                            SpongeConversionUtil.fromSponge(player),
                            fromLocation.getWorld(),
                            toLocation.getWorld())
                            .post();

            teleportEvent.setCancelled(worldEvent.isCancelled());
        }

        if (!event.getNewLocation().equals(toLocation)) {
            teleportEvent.getToTransform().setExtent(SpongeConversionUtil.toSponge(toLocation.getWorld()).orNull());
            teleportEvent.getToTransform().setPosition(SpongeConversionUtil.toSponge(toLocation.getVectorLoc()));
        }
    }

    @Listener(order = Order.EARLY, beforeModifications = true)
    @IsCancelled(Tristate.UNDEFINED)
    public void onPlayerPortalUse(MoveEntityEvent.Teleport.Portal portalEvent,
                                  @First Player player,
                                  @Getter("getUsePortalAgent") boolean usePortalAgent,
                                  @Getter("getFromTransform") Transform<World> from,
                                  @Getter("getToTransform") Transform<World> to) {
        Location fromLocation = SpongeConversionUtil.fromSponge(from.getLocation());
        Location toLocation = SpongeConversionUtil.fromSponge(to.getLocation());
        BlockType blockType = portalEvent.getFromTransform().getExtent().getBlockType(player.getPosition().toInt());

        // A bit of a hack, but Sponge does not expose the portal type through their events natively. Here we use the initial position of the player
        // to get the block they are in/against, if either is a portal type (Nether or End) we pass it into our event. If neither is detected the
        // portal is either placed oddly or not a native portal type in which case Unknown is used instead.
        PortalType portalType = getPortalType(blockType);

        if (portalType == PortalType.UNKOWN) {
            portalType = getPortalType(portalEvent.getFromTransform()
                    .getExtent()
                    .getBlockType(player.getPosition().toInt().add(0, 0, 1))
            );
        }

        PlayerPortalEvent event = new PlayerPortalEvent(SpongeConversionUtil.fromSponge(player), fromLocation, toLocation,
                usePortalAgent, portalType);
        event.post();
        portalEvent.setCancelled(event.isCancelled());
        portalEvent.setUsePortalAgent(portalEvent.getPortalAgent() != null && event.usesPortal());

        if (!event.getNewLocation().equals(toLocation)) {
            portalEvent.getToTransform().setExtent(SpongeConversionUtil.toSponge(toLocation.getWorld()).orNull());
            portalEvent.getToTransform().setPosition(SpongeConversionUtil.toSponge(toLocation.getVectorLoc()));
        }
    }

    private PortalType getPortalType(BlockType blockType) {
        if (blockType.equals(BlockTypes.PORTAL)) return PortalType.NETHER;
        else if (blockType.equals(BlockTypes.END_PORTAL)) return PortalType.END;
        return PortalType.UNKOWN;
    }

    @Listener
    public void onPlayerChat(
            MessageChannelEvent.Chat chatEvent,
            @First Player player,
            @Getter("getMessage") Text message) {
        Cancellable event =
                new SendChatEvent(
                        SpongeConversionUtil.fromSponge(player), SpongeConversionUtil.fromSponge(message))
                        .post();
        chatEvent.setCancelled(event.isCancelled());
    }

    @Listener
    public void onPlayerBanned(
            BanUserEvent banEvent,
            @First Player player,
            @Getter("getBan") Ban.Profile profile,
            @Getter("getSource") Object source) {
        SpongePlayerListener.postIfCommandSource(
                source,
                convertedSource -> {
                    Cancellable event =
                            new PlayerBannedEvent(
                                    SpongeConversionUtil.fromSponge(player),
                                    convertedSource,
                                    Exceptional.of(profile.getReason().map(Text::toPlain)),
                                    SeleneUtils.toLocalDateTime(profile.getExpirationDate()),
                                    SeleneUtils.toLocalDateTime(profile.getCreationDate()))
                                    .post();
                    if (event.isCancelled()) SpongePlayerListener.logUnsupportedCancel(event);
                });
    }

    private static void postIfCommandSource(
            Object source, Consumer<org.dockbox.selene.api.command.source.CommandSource> consumer) {
        if (source instanceof CommandSource) {
            SpongeConversionUtil.fromSponge((CommandSource) source).ifPresent(consumer);
        }
    }

    private static void logUnsupportedCancel(Cancellable event) {
        Selene.log()
                .warn(
                        "Attempted to cancel event of type '"
                                + event.getClass().getSimpleName()
                                + "', but this is not supported on this platform!");
    }

    @Listener
    public void onIPBanned(
            BanIpEvent banEvent, @Getter("getBan") Ban.Ip profile, @Getter("getSource") Object source) {
        SpongePlayerListener.postIfCommandSource(
                source,
                convertedSource -> {
                    Cancellable event =
                            new IpBannedEvent(
                                    profile.getAddress(),
                                    convertedSource,
                                    Exceptional.of(profile.getReason().map(Text::toPlain)),
                                    SeleneUtils.toLocalDateTime(profile.getExpirationDate()),
                                    SeleneUtils.toLocalDateTime(profile.getCreationDate()))
                                    .post();
                    banEvent.setCancelled(event.isCancelled());
                });
    }

    @Listener
    public void onNameBanned(
            NucleusNameBanEvent.Banned banEvent,
            @Getter("getEntry") String name,
            @Getter("getReason") String reason,
            @Getter("getSource") Object source) {
        SpongePlayerListener.postIfCommandSource(
                source,
                convertedSource -> {
                    Cancellable event =
                            new NameBannedEvent(
                                    name,
                                    convertedSource,
                                    Exceptional.ofNullable(reason),
                                    Exceptional.empty(),
                                    LocalDateTime.now())
                                    .post();
                    if (event.isCancelled()) SpongePlayerListener.logUnsupportedCancel(event);
                });
    }

    @Listener
    public void onPlayerWarned(
            NucleusWarnEvent.Warned warnEvent,
            @Getter("getTargetUser") User user,
            @NonNls @Getter("getReason") String reason,
            @Getter("getSource") Object source) {
        SpongePlayerListener.postIfCommandSource(
                source,
                convertedSource -> {
                    PlayerWarnedEvent event =
                            new WarnEvent.PlayerWarnedEvent(
                                    SpongeConversionUtil.fromSponge(user),
                                    convertedSource,
                                    reason,
                                    LocalDateTime.now());
                    event.post();
                    if (!event.getReason().equals(reason)) {
                        SpongePlayerListener.logUnsupportedModification(event);
                    }
                });
    }

    private static void logUnsupportedModification(Event event) {
        Selene.log()
                .warn(
                        "Attempted to modify value 'reason' event of type '"
                                + event.getClass().getSimpleName()
                                + "', but this is not supported on this platform!");
    }

    @Listener
    public void onPlayerNoted(
            NucleusNoteEvent.Created event,
            @Getter("getTargetUser") User user,
            @Getter("getNote") String note,
            @Getter("getSource") Object source) {
        SpongePlayerListener.postIfCommandSource(
                source,
                convertedSource ->
                        new NoteEvent.PlayerNotedEvent(
                                SpongeConversionUtil.fromSponge(user), convertedSource, note)
                                .post());
    }

    @Listener
    public void onPlayerMuted(
            NucleusMuteEvent.Muted event,
            @Getter("getTargetUser") User user,
            @Getter("getReason") Text reason,
            @Getter("getSource") Object source) {
        // TODO GuusLieben, MultiChat replaces this event. Look into Bungee hooking if possible
    }

    @Listener
    public void onPlayerUnbanned(
            PardonUserEvent pardonEvent,
            @First Player player,
            @Getter("getBan") Ban.Profile profile,
            @Getter("getSource") Object source) {
        SpongePlayerListener.postIfCommandSource(
                source,
                convertedSource -> {
                    Cancellable event =
                            new PlayerUnbannedEvent(
                                    SpongeConversionUtil.fromSponge(player),
                                    convertedSource,
                                    Exceptional.of(profile.getReason().map(Text::toPlain)),
                                    SeleneUtils.toLocalDateTime(profile.getCreationDate()))
                                    .post();
                    if (event.isCancelled()) SpongePlayerListener.logUnsupportedCancel(event);
                });
    }

    @Listener
    public void onIPUnbanned(
            PardonIpEvent pardonEvent,
            @Getter("getBan") Ban.Ip profile,
            @Getter("getSource") Object source) {
        SpongePlayerListener.postIfCommandSource(
                source,
                convertedSource -> {
                    Cancellable event =
                            new IpUnbannedEvent(
                                    profile.getAddress(),
                                    convertedSource,
                                    Exceptional.of(profile.getReason().map(Text::toPlain)),
                                    SeleneUtils.toLocalDateTime(profile.getCreationDate()))
                                    .post();
                    pardonEvent.setCancelled(event.isCancelled());
                });
    }

    @Listener
    public void onNameUnbanned(
            NucleusNameBanEvent.Unbanned pardonEvent,
            @Getter("getEntry") String name,
            @Getter("getReason") String reason,
            @Getter("getSource") Object source) {
        SpongePlayerListener.postIfCommandSource(
                source,
                convertedSource -> {
                    Cancellable event =
                            new NameUnbannedEvent(
                                    name, convertedSource, Exceptional.of(reason), LocalDateTime.now())
                                    .post();
                    if (event.isCancelled()) SpongePlayerListener.logUnsupportedCancel(event);
                });
    }

    @Listener
    public void onWarnExpired(
            NucleusWarnEvent.Expired warnEvent,
            @Getter("getTargetUser") User user,
            @Getter("getReason") String reason,
            @Getter("getSource") Object source) {
        SpongePlayerListener.postIfCommandSource(
                source,
                convertedSource ->
                        new WarnEvent.PlayerWarningExpired(
                                SpongeConversionUtil.fromSponge(user), convertedSource, reason)
                                .post());
    }

    @Listener
    public void onMuteExpired(
            NucleusMuteEvent.Unmuted event,
            @Getter("getTargetUser") User user,
            @Getter("getSource") Object source) {
        // TODO GuusLieben, MultiChat replaces this event. Look into Bungee hooking if possible
    }

    @Listener
    public void onPlayerKicked(
            KickPlayerEvent event,
            @Getter("getTargetEntity") Player player,
            @Getter("getSource") Object source) {
        SpongePlayerListener.postIfCommandSource(
                source,
                convertedSource ->
                        new KickEvent(
                                SpongeConversionUtil.fromSponge(player), convertedSource, Exceptional.empty())
                                .post());
    }

    @Listener
    public void onPlayerInteractedWithBlock(
            InteractBlockEvent event, @Getter("getSource") Player player) {
        Exceptional<Location> location =
                Exceptional.of(event.getTargetBlock().getLocation().map(SpongeConversionUtil::fromSponge));
        if (location.isAbsent()) return;
        Location blockLocation = location.get();
        ClickType type;
        Hand hand;

        if (event instanceof HandInteractEvent) {
            hand = SpongeConversionUtil.fromSponge(((HandInteractEvent) event).getHandType());
            String canonical = event.getClass().getCanonicalName();
            if (canonical.toLowerCase().contains("primary")) type = ClickType.PRIMARY;
            else if (canonical.toLowerCase().contains("secondary")) type = ClickType.SECONDARY;
            else return;
        }
        else return;

        Cancellable cancellable =
                new PlayerInteractBlockEvent(
                        SpongeConversionUtil.fromSponge(player), hand, type, blockLocation)
                        .post();
        event.setCancelled(cancellable.isCancelled());
    }

    @Listener
    public void onPlayerInteractedAir(HandInteractEvent event, @Getter("getSource") Player player) {
        if (event instanceof InteractBlockEvent || event instanceof InteractEntityEvent) return;
        if (event.getInteractionPoint().isPresent()) return;

        Hand hand = SpongeConversionUtil.fromSponge((event.getHandType()));
        String canonical = event.getClass().getCanonicalName();

        ClickType type;
        if (canonical.toLowerCase().contains("primary")) type = ClickType.PRIMARY;
        else if (canonical.toLowerCase().contains("secondary")) type = ClickType.SECONDARY;
        else return;

        Cancellable cancellable =
                new PlayerInteractAirEvent(SpongeConversionUtil.fromSponge(player), hand, type).post();
        event.setCancelled(cancellable.isCancelled());
    }

    @Listener
    public void onPlayerInteractedEntity(
            InteractEntityEvent event,
            @Getter("getSource") Player player,
            @Getter("getTargetEntity") Entity entity) {
        org.dockbox.selene.api.entities.Entity<?> targetEntity;
        if (entity instanceof ArmorStand) targetEntity = new SpongeArmorStand((ArmorStand) entity);
        else if (entity instanceof ItemFrame) targetEntity = new SpongeItemFrame((ItemFrame) entity);
        else return;

        Cancellable cancellable =
                new PlayerInteractEntityEvent<>(
                        SpongeConversionUtil.fromSponge(player),
                        targetEntity,
                        SpongeConversionUtil.fromSponge(event.getInteractionPoint().orElse(Vector3d.ZERO)));
        event.setCancelled(cancellable.isCancelled());
    }
}
