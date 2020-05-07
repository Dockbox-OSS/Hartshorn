package com.darwinreforged.server.core.entities.living;

import java.util.UUID;

public enum GenericTarget {
    SERVER(UUID.fromString(""), "Server");

    private Target target;
    GenericTarget(UUID uuid, String name) {
        target = new Target() {
            @Override
            public UUID getUuid() {
                return uuid;
            }

            @Override
            public String getName() {
                return name;
            }
        };
    }

    public Target getTarget() {
        return target;
    }
}
