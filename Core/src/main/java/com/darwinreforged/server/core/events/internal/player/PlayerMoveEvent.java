package com.darwinreforged.server.core.events.internal.player;

import com.darwinreforged.server.core.types.living.Target;
import com.darwinreforged.server.core.events.CancellableEvent;

public class PlayerMoveEvent extends CancellableEvent {

    public PlayerMoveEvent(Target target) {
        super(target);
    }
}
