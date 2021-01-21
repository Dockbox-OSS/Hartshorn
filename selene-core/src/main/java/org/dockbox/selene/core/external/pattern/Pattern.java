package org.dockbox.selene.core.external.pattern;

import org.dockbox.selene.core.external.WorldEditService;
import org.dockbox.selene.core.objects.Exceptional;
import org.dockbox.selene.core.objects.player.Player;
import org.dockbox.selene.core.server.Selene;

public interface Pattern {

    static Exceptional<Pattern> parse(String pattern) {
        return Selene.provide(WorldEditService.class).parsePattern(pattern, null);
    }

    static Exceptional<Pattern> parse(String pattern, Player cause) {
        return Selene.provide(WorldEditService.class).parsePattern(pattern, cause);
    }

}
