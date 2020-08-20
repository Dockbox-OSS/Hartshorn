package org.dockbox.darwin.sponge.util.player;

import org.dockbox.darwin.core.objects.user.Player;
import org.dockbox.darwin.core.util.player.PlayerStorageService;
import org.dockbox.darwin.sponge.objects.location.SpongePlayer;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.user.UserStorageService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class SpongePlayerStorageService implements PlayerStorageService {
    @NotNull
    @Override
    public List<Player> getOnlinePlayers() {
        return Sponge.getServer().getOnlinePlayers().stream().map(sp -> new SpongePlayer(sp.getUniqueId(), sp.getName())).collect(Collectors.toList());
    }

    @NotNull
    @Override
    public Optional<Player> getPlayer(@NotNull UUID uuid) {
        Optional<Player> player = Optional.empty();
        Optional<org.spongepowered.api.entity.living.player.Player> osp = Sponge.getServer().getPlayer(uuid);

        if (osp.isPresent()) {
            player = osp.map(sp -> new SpongePlayer(sp.getUniqueId(), sp.getName()));
        } else {
            Optional<UserStorageService> ouss = Sponge.getServiceManager().provide(UserStorageService.class);
            Optional<User> ou = ouss.flatMap(uss -> uss.get(uuid));
            if (ou.isPresent()) player = ou.map(u -> new SpongePlayer(u.getUniqueId(), u.getName()));
        }

        return player;
    }
}
