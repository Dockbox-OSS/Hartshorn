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

package org.dockbox.hartshorn.logging;

import org.dockbox.hartshorn.util.reflect.TypeContext;
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
public abstract class CallerLookupApplicationLogger implements ApplicationLogger {

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

        if (element == null) throw new StackVisitingException("Could not determine caller from stacktrace");

        final String className = element.getClassName().split("\\$")[0];
        if (this.loggers.containsKey(className)) return this.loggers.get(className);

        final Logger logger = LoggerFactory.getLogger(TypeContext.lookup(className).type());
        this.loggers.put(className, logger);
        return logger;
    }
}
