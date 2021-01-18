package org.dockbox.selene.core.external;

import org.dockbox.selene.core.external.region.Clipboard;
import org.dockbox.selene.core.external.region.Region;
import org.dockbox.selene.core.objects.Exceptional;
import org.dockbox.selene.core.objects.player.Player;

public abstract class WorldEditService {

    public abstract Exceptional<Region> getPlayerRegion(Player player);

    public abstract void setPlayerRegion(Player player, Region region);

    public abstract Exceptional<Clipboard> getPlayerClipboard(Player player);

    public abstract void setPlayerClipboard(Player player, Clipboard clipboard);

}
