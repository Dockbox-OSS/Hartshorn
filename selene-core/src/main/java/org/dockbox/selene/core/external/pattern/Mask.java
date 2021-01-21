package org.dockbox.selene.core.external.pattern;

import org.dockbox.selene.core.external.WorldEditService;
import org.dockbox.selene.core.objects.Exceptional;
import org.dockbox.selene.core.objects.player.Player;
import org.dockbox.selene.core.server.Selene;

public interface Mask {

    static Exceptional<Mask> parse(String mask) {
        return Selene.provide(WorldEditService.class).parseMask(mask, null);
    }

    static Exceptional<Mask> parse(String mask, Player cause) {
        return Selene.provide(WorldEditService.class).parseMask(mask, cause);
    }

}
