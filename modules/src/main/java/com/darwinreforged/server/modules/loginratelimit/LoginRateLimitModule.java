package com.darwinreforged.server.modules.loginratelimit;

import com.darwinreforged.server.api.modules.ModuleInfo;
import com.darwinreforged.server.api.modules.PluginModule;
import com.darwinreforged.server.api.resources.Translations;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.network.ClientConnectionEvent;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@ModuleInfo(
        id = "ratelimit",
        name = "Rate Limit",
        description = "Rate limit relog attempts",
        authors = {
                "GuusLieben"
        }
)
public class LoginRateLimitModule extends PluginModule {

    private static final Map<UUID, LocalDateTime> TIME_MAP = new HashMap<>();

    public LoginRateLimitModule() {
    }

    @Listener(order = Order.FIRST)
    public void onPlayerConnect(ClientConnectionEvent.Login event) {
        UUID playerUUID = event.getProfile().getUniqueId();
        boolean add = true;
        LocalDateTime currentLoginTime = LocalDateTime.now();
        if (TIME_MAP.containsKey(playerUUID)) {
            LocalDateTime previousLoginTime = TIME_MAP.get(playerUUID);
            if (previousLoginTime.until(currentLoginTime, ChronoUnit.MINUTES) < 2) {
                event.setCancelled(true);
                add = false;

                // Usually the player won't be present yet, and the event cancellation will prevent the player from
                // logging in. However if lag occurs, or another plugins changes the event we need to make sure the
                // player can not connect
                Optional<Player> optionalPlayer = Sponge.getServer().getPlayer(playerUUID);
                optionalPlayer.ifPresent(player -> player.kick(Translations.RATE_LIMIT_KICK_MESSAGE.t()));
            }
        }
        if (add) TIME_MAP.put(playerUUID, currentLoginTime);
    }
}
