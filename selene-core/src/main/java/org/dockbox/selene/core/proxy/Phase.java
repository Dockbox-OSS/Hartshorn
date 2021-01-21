package org.dockbox.selene.core.proxy;

public enum Phase {
    HEAD, OVERWRITE, TAIL;

    public Phase[] collect() {
        return new Phase[] {HEAD, OVERWRITE, TAIL};
    }
}
