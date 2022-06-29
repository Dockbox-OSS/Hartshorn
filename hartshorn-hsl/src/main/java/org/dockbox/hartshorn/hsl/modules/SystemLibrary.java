package org.dockbox.hartshorn.hsl.modules;

public class SystemLibrary {

    public String env(final String program) {
        return System.getenv(program);
    }
}

