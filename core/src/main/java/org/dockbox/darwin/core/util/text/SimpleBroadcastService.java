package org.dockbox.darwin.core.util.text;

import org.dockbox.darwin.core.i18n.Permission;
import org.dockbox.darwin.core.objects.user.Player;
import org.dockbox.darwin.core.server.Server;
import org.dockbox.darwin.core.text.Text;
import org.dockbox.darwin.core.util.player.PlayerStorageService;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class SimpleBroadcastService implements BroadcastService {

    @Override
    public void broadcastPublic(@NotNull Text message) {
        Server.getInstance(PlayerStorageService.class).getOnlinePlayers().forEach(message::send);
    }

    @Override
    public void broadcastWithFilter(@NotNull Text message, @NotNull Predicate<Player> filter) {
        sendWithPredicate(message, filter);
    }

    @Override
    public void broadcastForPermission(@NotNull Text message, @NotNull Permission permission) {
        sendWithPredicate(message, p -> p.hasPermission(permission));
    }

    @Override
    public void broadcastForPermission(@NotNull Text message, @NotNull String permission) {
        sendWithPredicate(message, p -> p.hasPermission(permission));
    }

    @Override
    public void broadcastForPermissionWithFilter(@NotNull Text message, @NotNull Permission permission, @NotNull Predicate<Player> filter) {
        sendWithPredicate(message, p -> filter.test(p) && p.hasPermission(permission));
    }

    @Override
    public void broadcastForPermissionWithFilter(@NotNull Text message, @NotNull String permission, @NotNull Predicate<Player> filter) {
        sendWithPredicate(message, p -> filter.test(p) && p.hasPermission(permission));
    }

    private void sendWithPredicate(@NotNull Text message, @NotNull Predicate<Player> filter) {
        Server.getInstance(PlayerStorageService.class).getOnlinePlayers().stream().filter(filter).forEach(message::send);
    }
}
