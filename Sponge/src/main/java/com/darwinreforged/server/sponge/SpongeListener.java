package com.darwinreforged.server.sponge;

import com.darwinreforged.server.core.DarwinServer;
import com.darwinreforged.server.core.events.CancellableEvent;
import com.darwinreforged.server.core.events.internal.chat.SendChatMessageEvent;
import com.darwinreforged.server.core.events.internal.player.InventoryInteractionEvent;
import com.darwinreforged.server.core.events.internal.player.PlayerLoggedInEvent;
import com.darwinreforged.server.core.events.internal.player.PlayerMoveEvent;
import com.darwinreforged.server.core.events.internal.player.PlayerTeleportEvent;
import com.darwinreforged.server.core.events.internal.server.ServerReloadEvent;
import com.darwinreforged.server.core.player.PlayerManager;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.command.SendCommandEvent;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent;
import org.spongepowered.api.event.message.MessageChannelEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;

import xyz.olivermartin.multichat.spongebridge.MultiChatSponge;

public class SpongeListener {

    @Listener
    public void onServerReload(GameReloadEvent event) {
        DarwinServer.getEventBus().post(new ServerReloadEvent(null));
    }

    @Listener
    public void onPlayerTeleport(MoveEntityEvent.Teleport event, @First Player p) {
        postCancellable(new PlayerTeleportEvent(PlayerManager.getPlayer(p.getUniqueId(), p.getName())), event);
    }

    @Listener
    public void onPlayerMove(MoveEntityEvent event, @First Player p) {
        postCancellable(new PlayerMoveEvent(PlayerManager.getPlayer(p.getUniqueId(), p.getName())), event);
    }

    @Listener
    public void onPlayerLoggedIn(ClientConnectionEvent.Join event, @First Player p) {
        DarwinServer.getEventBus().post(new PlayerLoggedInEvent(PlayerManager.getPlayer(p.getUniqueId(), p.getName())));
    }

    @Listener
    public void onInventoryInteract(InteractInventoryEvent event, @First Player p) {
        postCancellable(new InventoryInteractionEvent(PlayerManager.getPlayer(p.getUniqueId(), p.getName())), event);
    }

    @Listener
    public void onChatMessageSent(MessageChannelEvent.Chat event, @First Player p) {
        String channel = MultiChatSponge.playerChannels.getOrDefault(p, "global");
        postCancellable(new SendChatMessageEvent(PlayerManager.getPlayer(p.getUniqueId(), p.getName()), event.getRawMessage().toPlain(), channel.equalsIgnoreCase("global")), event);
    }

    @Listener
    public void onCommand(SendCommandEvent event) {
//        SpongeCommandUtils cu = (SpongeCommandUtils) DarwinServer.getUtilChecked(CommandUtils.class);
//        if (event.getSource() instanceof CommandSource) {
//            boolean cancel = cu.handleCommandSend((CommandSource) event.getSource(), String.format("%s %s", event.getCommand(), event.getArguments()));
////            if (cancel) event.setCancelled(true);
//        }
    }

    private <I extends CancellableEvent> void postCancellable(I e, Cancellable se) {
        DarwinServer.getEventBus().post(e);
        if (e.isCancelled()) se.setCancelled(true);
    }
}
