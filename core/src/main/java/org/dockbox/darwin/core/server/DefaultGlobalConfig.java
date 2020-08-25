package org.dockbox.darwin.core.server;

import org.dockbox.darwin.core.i18n.Languages;
import org.jetbrains.annotations.NotNull;

public class DefaultGlobalConfig implements GlobalConfig {

    @NotNull
    @Override
    public Languages getDefaultLanguage() {
        return Languages.EN_US; // TODO: File based config
    }

    @NotNull
    @Override
    public <T> T getSetting(@NotNull String key) {
        throw new UnsupportedOperationException("Not implemented");
    }
}
