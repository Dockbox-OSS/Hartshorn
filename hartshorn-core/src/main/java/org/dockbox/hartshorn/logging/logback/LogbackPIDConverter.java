/*
 * Copyright 2019-2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dockbox.hartshorn.logging.logback;

import java.lang.management.ManagementFactory;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

/**
 * Logback converter that provides the PID of the current process. This is used by the
 * {@link LogbackEncoder} to offer the PID to the log message.
 *
 * @see LogbackEncoder
 *
 * @since 0.4.8
 *
 * @author Guus Lieben
 */
public class LogbackPIDConverter extends ClassicConverter {

    private static final long PROCESS_ID = ManagementFactory.getRuntimeMXBean().getPid();

    @Override
    public String convert(ILoggingEvent event) {
        return String.valueOf(PROCESS_ID);
    }
}
