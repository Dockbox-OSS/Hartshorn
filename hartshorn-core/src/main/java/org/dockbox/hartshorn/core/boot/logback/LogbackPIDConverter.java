package org.dockbox.hartshorn.core.boot.logback;

import java.lang.management.ManagementFactory;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

public class LogbackPIDConverter extends ClassicConverter {

    private static final long PROCESS_ID =
            ManagementFactory.getRuntimeMXBean().getPid();

    @Override
    public String convert(final ILoggingEvent event) {
        return "" + PROCESS_ID;
    }
}
