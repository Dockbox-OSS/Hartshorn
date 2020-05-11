package com.darwinreforged.server.sponge;

import com.darwinreforged.server.core.entities.living.DarwinPlayer;
import com.darwinreforged.server.core.events.CancellableEvent;
import com.darwinreforged.server.core.events.internal.player.InventoryInteractionEvent;
import com.darwinreforged.server.core.events.internal.player.PlayerLoggedInEvent;
import com.darwinreforged.server.core.events.internal.player.PlayerMoveEvent;
import com.darwinreforged.server.core.events.internal.player.PlayerTeleportEvent;
import com.darwinreforged.server.core.events.internal.server.ServerReloadEvent;
import com.darwinreforged.server.core.init.DarwinServer;
import com.magitechserver.magibridge.chat.MessageBuilder;
import com.magitechserver.magibridge.discord.DiscordMessageBuilder;
import com.magitechserver.magibridge.events.DiscordMessageEvent;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;

public class SpongeListener {

    @Listener
    public void onServerReload(GameReloadEvent event) {
        DarwinServer.getEventBus().post(new ServerReloadEvent(null));
    }

    @Listener
    public void onPlayerTeleport(MoveEntityEvent.Teleport event, @First Player p) {
        postCancellable(new PlayerTeleportEvent(new DarwinPlayer(p.getUniqueId(), p.getName())), event);
    }

    @Listener
    public void onPlayerMove(MoveEntityEvent event, @First Player p) {
        postCancellable(new PlayerMoveEvent(new DarwinPlayer(p.getUniqueId(), p.getName())), event);
    }

    @Listener
    public void onPlayerLoggedIn(ClientConnectionEvent.Join event, @First Player p) {
        DarwinServer.getEventBus().post(new PlayerLoggedInEvent(new DarwinPlayer(p.getUniqueId(), p.getName())));
    }

    @Listener
    public void onInventoryInteract(InteractInventoryEvent event, @First Player p) {
        postCancellable(new InventoryInteractionEvent(new DarwinPlayer(p.getUniqueId(), p.getName())), event);
    }

    private <I extends CancellableEvent> void postCancellable(I e, Cancellable se) {
        DarwinServer.getEventBus().post(e);
        if (e.isCancelled()) se.setCancelled(true);
    }
}
