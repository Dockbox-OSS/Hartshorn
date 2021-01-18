package org.dockbox.selene.core.external;

import org.dockbox.selene.core.external.region.Clipboard;
import org.dockbox.selene.core.external.region.Region;
import org.dockbox.selene.core.objects.keys.Key;
import org.dockbox.selene.core.objects.keys.Keys;
import org.dockbox.selene.core.objects.player.Player;
import org.dockbox.selene.core.server.Selene;

public final class WorldEditKeys {

    public static final Key<Player, Region> SELECTION = Keys.dynamicKeyOf(
            (player, region) -> Selene.provide(WorldEditService.class).setPlayerRegion(player, region),
            player -> Selene.provide(WorldEditService.class).getPlayerRegion(player)
    );

    public static final Key<Player, Clipboard> CLIPBOARD = Keys.dynamicKeyOf(
            (player, clipboard) -> Selene.provide(WorldEditService.class).setPlayerClipboard(player, clipboard),
            player -> Selene.provide(WorldEditService.class).getPlayerClipboard(player)
    );

    private WorldEditKeys() {}
}
