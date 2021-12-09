/*
 * Copyright (C) 2020 Guus Lieben
 *
 * This framework is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.hartshorn.core.boot.logback;

import java.lang.management.ManagementFactory;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

/**
 * Logback converter that provides the PID of the current process. This is used by the
 * {@link LogbackEncoder} to offer the PID to the log message.
 *
 * @author Guus Lieben
 * @since 4.2.4
 * @see LogbackEncoder
 */
public class LogbackPIDConverter extends ClassicConverter {

    private static final long PROCESS_ID = ManagementFactory.getRuntimeMXBean().getPid();

    @Override
    public String convert(final ILoggingEvent event) {
        return "" + PROCESS_ID;
    }
}
