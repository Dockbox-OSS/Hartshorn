package org.dockbox.hartshorn.logging;

public class Slf4jApplicationLogger extends CallerLookupApplicationLogger {

    @Override
    public void setDebugActive(boolean active) {
        // Not supported by SLF4J directly, so we do nothing. Specific implementations may support this.
        log().warn("Changing level at runtime is not supported by SLF4J, please use a different application logger");
    }
}
