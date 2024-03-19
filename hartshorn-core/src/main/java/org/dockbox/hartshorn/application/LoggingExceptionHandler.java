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

package org.dockbox.hartshorn.application;

import org.dockbox.hartshorn.util.IllegalModificationException;
import org.dockbox.hartshorn.application.environment.ApplicationEnvironment;
import org.dockbox.hartshorn.application.environment.ApplicationManaged;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is used to handle exceptions that occur during the application lifecycle. This default implementation
 * of the {@link ExceptionHandler} interface logs the exception to the {@link Logger} and is able to correctly display
 * stacktraces when {@link #printStackTraces()} is {@code true}.
 *
 * @author Guus Lieben
 * @since 0.4.8
 */
public class LoggingExceptionHandler implements ExceptionHandler, ApplicationManaged {

    private static final Logger LOG = LoggerFactory.getLogger(LoggingExceptionHandler.class);

    private boolean printStackTraces;
    private ApplicationEnvironment environment;

    @Override
    public LoggingExceptionHandler printStackTraces(boolean stacktraces) {
        this.printStackTraces = stacktraces;
        LOG.debug("{} stacktraces for all reported errors", stacktraces ? "Enabled" : "Disabled");
        return this;
    }

    /**
     * Returns whether stacktraces are printed for all reported errors.
     *
     * @return {@code true} if stacktraces are printed, {@code false} otherwise
     */
    public boolean printStackTraces() {
        return this.printStackTraces;
    }

    @Override
    public ApplicationEnvironment environment() {
        return this.environment;
    }

    @Override
    public void environment(ApplicationEnvironment environment) {
        if (this.environment == null) {
            this.environment = environment;
        }
        else {
            throw new IllegalModificationException("Application environment has already been configured");
        }
    }

    @Override
    public void handle(Throwable throwable) {
        this.handle(firstMessage(throwable), throwable);
    }

    @Override
    public void handle(String message, Throwable throwable) {
        if (null != throwable) {
            if (message == null) {
                message = "";
            }

            if (this.printStackTraces()) {
                LOG.error(message, throwable);
            }
            else if (!message.isBlank()) {
                LOG.error(message);
            }
        }
    }

    /**
     * Returns the first message of the given {@link Throwable} or {@code null} if the given {@link Throwable} is
     * {@code null}.
     *
     * @param throwable The {@link Throwable} to get the first message from.
     *
     * @return The first message of the given {@link Throwable} or {@code null}.
     */
    public static String firstMessage(Throwable throwable) {
        Throwable next = throwable;
        while (next != null) {
            if (null != next.getMessage()) {
                return next.getMessage();
            }
            else {
                // Avoid infinitely looping if the throwable has itself as cause
                if (!next.equals(throwable.getCause())) {
                    next = next.getCause();
                }
                else {
                    break;
                }
            }
        }
        return "No message provided";
    }
}
