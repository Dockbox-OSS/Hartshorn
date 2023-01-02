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

package org.dockbox.hartshorn.application;

import org.dockbox.hartshorn.application.context.IllegalModificationException;
import org.dockbox.hartshorn.application.environment.ApplicationEnvironment;
import org.dockbox.hartshorn.application.environment.ApplicationManaged;
import org.dockbox.hartshorn.util.ExceptionUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is used to handle exceptions that occur during the application lifecycle. This default implementation
 * of the {@link ExceptionHandler} interface logs the exception to the {@link Logger} and is able to correctly display
 * stacktraces when {@link #stacktraces()} is {@code true}.
 *
 * @author Guus Lieben
 * @since 21.9
 */
public class LoggingExceptionHandler implements ExceptionHandler, ApplicationManaged {

    private boolean stacktraces;
    private ApplicationEnvironment environment;

    @Override
    public LoggingExceptionHandler stacktraces(final boolean stacktraces) {
        this.stacktraces = stacktraces;
        this.findLogger().debug("{} stacktraces for all reported errors", stacktraces ? "Enabled" : "Disabled");
        return this;
    }

    public boolean stacktraces() {
        return this.stacktraces;
    }

    @Override
    public ApplicationEnvironment environment() {
        return this.environment;
    }

    @Override
    public void environment(final ApplicationEnvironment environment) {
        if (this.environment == null) this.environment = environment;
        else throw new IllegalModificationException("Application environment has already been configured");
    }

    @Override
    public void handle(final Throwable throwable) {
        this.handle(ExceptionUtilities.firstMessage(throwable), throwable);
    }

    @Override
    public void handle(final String message, final Throwable throwable) {
        final String[] lines = ExceptionUtilities.format(message, throwable, this.stacktraces());
        for (final String line : lines) {
            this.findLogger().error(line);
        }
    }

    private Logger findLogger() {
        return this.environment() != null
                ? this.environment().log()
                : LoggerFactory.getLogger(LoggingExceptionHandler.class);
    }
}
