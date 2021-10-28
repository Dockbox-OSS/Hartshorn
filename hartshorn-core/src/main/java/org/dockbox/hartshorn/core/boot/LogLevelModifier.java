package org.dockbox.hartshorn.core.boot;

import org.dockbox.hartshorn.core.Modifier;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class LogLevelModifier implements Modifier {

    public enum LogLevel {
        OFF,
        FATAL,
        ERROR,
        WARN,
        INFO,
        DEBUG,
        TRACE,
        ALL,
    }

    @Getter LogLevel level;

}
