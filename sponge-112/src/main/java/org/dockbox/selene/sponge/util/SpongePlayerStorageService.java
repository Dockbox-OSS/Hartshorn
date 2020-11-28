package org.dockbox.selene.sponge.util;

import org.dockbox.selene.core.impl.util.player.DefaultPlayerStorageService;
import org.dockbox.selene.core.objects.optional.Exceptional;
import org.dockbox.selene.core.objects.user.Player;
import org.dockbox.selene.sponge.objects.targets.SpongePlayer;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.user.UserStorageService;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class SpongePlayerStorageService extends DefaultPlayerStorageService {
    @NotNull
    @Override
    public List<Player> getOnlinePlayers() {
        return Sponge.getServer().getOnlinePlayers().stream()
                .map(player -> new SpongePlayer(player.getUniqueId(), player.getName()))
                .collect(Collectors.toList());
    }

    @NotNull
    @Override
    public Exceptional<Player> getPlayer(@NotNull String name) {
        return this.getPlayer(Exceptional.of(Sponge.getServer().getPlayer(name)), name);
    }

    @NotNull
    @Override
    public Exceptional<Player> getPlayer(@NotNull UUID uuid) {
        return this.getPlayer(Exceptional.of(Sponge.getServer().getPlayer(uuid)), uuid);
    }

    private Exceptional<Player> getPlayer(
            Exceptional<org.spongepowered.api.entity.living.player.Player> osp,
            Object obj
    ) {
        if (osp.isPresent()) {
            return osp.map(p -> new SpongePlayer(p.getUniqueId(), p.getName()));
        } else {
            Exceptional<Player> player = Exceptional.empty();
            Exceptional<UserStorageService> ouss = Exceptional.of(
                    Sponge.getServiceManager().provide(UserStorageService.class)
            );
            Exceptional<User> ou;
            if (obj instanceof UUID) {
                ou = ouss.flatMap(uss -> Exceptional.of(uss.get((UUID) obj)));
            } else {
                ou = ouss.flatMap(uss -> Exceptional.of(uss.get(obj.toString())));
            }
            if (ou.isPresent()) player = ou.map(u -> new SpongePlayer(u.getUniqueId(), u.getName()));
            return player;
        }
    }
}
