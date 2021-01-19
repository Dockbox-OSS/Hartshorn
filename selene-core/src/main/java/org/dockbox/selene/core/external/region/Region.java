package org.dockbox.selene.core.external.region;

import org.dockbox.selene.core.external.WorldEditService;
import org.dockbox.selene.core.external.pattern.Mask;
import org.dockbox.selene.core.external.pattern.Pattern;
import org.dockbox.selene.core.objects.item.Item;
import org.dockbox.selene.core.objects.location.World;
import org.dockbox.selene.core.objects.player.Player;
import org.dockbox.selene.core.objects.tuple.Vector3N;
import org.dockbox.selene.core.server.Selene;
import org.dockbox.selene.core.util.SeleneUtils;

import java.util.Collection;

public interface Region {

    Vector3N getMinimumPoint();

    Vector3N getMaximumPoint();

    Vector3N getCenter();

    int getArea();

    int getWidth();

    int getHeight();

    int getLength();

    World getWorld();

    default void replace(Mask mask, Pattern pattern, Player cause) {
        Selene.provide(WorldEditService.class).replace(this, mask, pattern, cause);
    }

    default void set(Pattern pattern, Player cause) {
        Selene.provide(WorldEditService.class).set(this, pattern, cause);
    }

    default void replace(Item mask, Item pattern, Player cause) {
        this.replace(SeleneUtils.singletonList(mask), SeleneUtils.singletonList(pattern),cause );
    }

    default void set(Item pattern, Player cause) {
        this.set(SeleneUtils.singletonList(pattern), cause);
    }

    default void replace(Collection<Item> mask, Collection<Item> pattern, Player cause) {
        Selene.provide(WorldEditService.class).replace(this, mask, pattern, cause);
    }

    default void set(Collection<Item> pattern, Player cause) {
        Selene.provide(WorldEditService.class).set(this, pattern, cause);
    }

}
