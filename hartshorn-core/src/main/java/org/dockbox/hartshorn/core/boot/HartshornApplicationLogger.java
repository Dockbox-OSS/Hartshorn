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

package org.dockbox.hartshorn.core.boot;

import org.dockbox.hartshorn.core.annotations.context.LogExclude;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A {@link ApplicationLogger} implementation that uses the calling class as the logger name, provided the
 * {@link LogExclude} annotation is not present on the calling class, in which case the next calling class
 * on the current stacktrace is used.
 *
 * @author Guus Lieben
 * @since 21.9
 */
@LogExclude
public class HartshornApplicationLogger implements ApplicationLogger {

    private final Map<String, Logger> loggers = new ConcurrentHashMap<>();

    @Override
    public Logger log() {
        StackTraceElement element = null;
        for (final StackTraceElement ste : Thread.currentThread().getStackTrace()) {
            final boolean isJavaModule = ste.getModuleName() != null && ste.getModuleName().startsWith("java.");
            final boolean isExcluded = TypeContext.lookup(ste.getClassName().split("\\$")[0]).annotation(LogExclude.class).present();
            if (!isJavaModule && !isExcluded) {
                element = ste;
                break;
            }
        }

        if (element == null) throw new IllegalStateException("Could not determine caller from stacktrace");

        final String className = element.getClassName().split("\\$")[0];
        if (this.loggers.containsKey(className)) return this.loggers.get(className);

        final Logger logger = LoggerFactory.getLogger(TypeContext.lookup(className).type());
        this.loggers.put(className, logger);
        return logger;
    }
}
