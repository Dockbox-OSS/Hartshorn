package com.darwinreforged.server.core.init;

public enum ServerType {
    SPONGE(DarwinServer.class), PAPER(null), MAGMA(null);


    ServerType(Class<? extends DarwinServer> impl) {

    }
}
