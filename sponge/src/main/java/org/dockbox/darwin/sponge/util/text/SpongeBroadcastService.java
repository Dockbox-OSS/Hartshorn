package org.dockbox.darwin.sponge.util.text;

import org.dockbox.darwin.core.i18n.Permission;
import org.dockbox.darwin.core.objects.user.Player;
import org.dockbox.darwin.core.text.Text;
import org.dockbox.darwin.core.util.text.BroadcastService;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class SpongeBroadcastService implements BroadcastService {
    @Override
    public void broadcastPublic(@NotNull Text message) {
        // TODO
    }

    @Override
    public void broadcastWithFilter(@NotNull Text message, @NotNull Predicate<Player> filter) {
        // TODO
    }

    @Override
    public void broadcastForPermission(@NotNull Text message, @NotNull Permission permission) {
        // TODO
    }

    @Override
    public void broadcastForPermission(@NotNull Text message, @NotNull String permission) {
        // TODO
    }

    @Override
    public void broadcastForPermissionWithFilter(@NotNull Text message, @NotNull Permission permission, @NotNull Predicate<Player> filter) {
        // TODO
    }

    @Override
    public void broadcastForPermissionWithFilter(@NotNull Text message, @NotNull String permission, @NotNull Predicate<Player> filter) {
        // TODO
    }
}
