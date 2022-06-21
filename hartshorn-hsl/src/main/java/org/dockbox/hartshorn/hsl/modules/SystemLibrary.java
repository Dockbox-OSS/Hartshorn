package org.dockbox.hartshorn.hsl.modules;

@Library("system")
public class SystemLibrary {

    public String osName() {
        return System.getProperty("os.name");
    }

    public String osEnvVar(final String program) {
        return System.getenv(program);
    }

    public String osArch( ) {
        return System.getProperty("os.arch");
    }
}

