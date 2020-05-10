package com.darwinreforged.server.core.events.internal.player;

import com.darwinreforged.server.core.entities.living.Target;
import com.darwinreforged.server.core.events.util.Event;

public class PlayerLoggedInEvent extends Event {
    public PlayerLoggedInEvent(Target target) {
        super(target);
    }
}
