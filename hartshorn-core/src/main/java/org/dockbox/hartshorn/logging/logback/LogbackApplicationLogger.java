/*
 * Copyright 2019-2022 the original author or authors.
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

import org.dockbox.hartshorn.logging.CallerLookupApplicationLogger;
import org.slf4j.ILoggerFactory;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.lang.management.ManagementFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;

public class LogbackApplicationLogger extends CallerLookupApplicationLogger {

    public LogbackApplicationLogger() {
        MDC.put("process_id", ManagementFactory.getRuntimeMXBean().getName());
    }

    @Override
    public void setDebugActive(final boolean active) {
        final ILoggerFactory factory = LoggerFactory.getILoggerFactory();

        final Level level = active ? Level.DEBUG : Level.INFO;
        if (factory instanceof LoggerContext loggerContext) {
            for (final Logger logger : loggerContext.getLoggerList()) {
                logger.setLevel(level);
            }
        }
        else {
            final Logger rootLogger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
            rootLogger.setLevel(level);
        }
    }
}
