package com.darwinreforged.server.core.events.internal.player;

import com.darwinreforged.server.core.entities.living.Target;
import com.darwinreforged.server.core.events.CancellableEvent;

public class PlayerTeleportEvent extends CancellableEvent {

    public PlayerTeleportEvent(Target target) {
        super(target);
    }
}
