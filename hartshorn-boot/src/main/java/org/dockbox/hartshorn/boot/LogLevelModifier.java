package org.dockbox.hartshorn.boot;

import org.dockbox.hartshorn.di.Modifier;

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
