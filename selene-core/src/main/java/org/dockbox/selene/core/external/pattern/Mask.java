package org.dockbox.selene.core.external.pattern;

import org.dockbox.selene.core.external.WorldEditService;
import org.dockbox.selene.core.server.Selene;

public interface Mask {

    static Mask parse(String mask) {
        return Selene.provide(WorldEditService.class).parseMask(mask);
    }

}
