/*
 * Copyright 2019-2023 the original author or authors.
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

    public LoggingExceptionHandler stacktraces(final boolean stacktraces) {
        this.stacktraces = stacktraces;
        this.findLogger().debug("{} stacktraces for all reported errors", stacktraces ? "Enabled" : "Disabled");
        return this;
    }

    public boolean stacktraces() {
        return this.stacktraces;
    }

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
        this.handle(firstMessage(throwable), throwable);
    }

    @Override
    public void handle(String message, final Throwable throwable) {
        if (null != throwable) {
            final Logger log = this.findLogger();

            String location = "";
            if (0 < throwable.getStackTrace().length) {
                final StackTraceElement root = throwable.getStackTrace()[0];
                final String line = 0 < root.getLineNumber() ? ":" + root.getLineNumber() : "(internal call)";
                location = root.getFileName() + line;
            }

            if (message == null) message = "";
            final String[] lines = message.split("\n");
            log.error("Exception: " + throwable.getClass().getCanonicalName() + " ("+ location +"): " + lines[0]);
            if (lines.length > 1) {
                for (int i = 1; i < lines.length; i++) {
                    log.error("  " + lines[i]);
                }
            }

            if (this.stacktraces()) {
                Throwable nextException = throwable;

                while (null != nextException) {
                    final StackTraceElement[] trace = nextException.getStackTrace();
                    final String nextMessage = String.valueOf(nextException.getMessage());
                    final String[] nextLines = nextMessage.split("\n");
                    log.error(nextException.getClass().getCanonicalName() + ": " + nextLines[0]);
                    if (nextLines.length > 1) {
                        for (int i = 1; i < nextLines.length; i++) {
                            log.error("  " + nextLines[i]);
                        }
                    }

                    for (final StackTraceElement element : trace) {
                        final String elLine = 0 < element.getLineNumber() ? ":" + element.getLineNumber() : "(internal call)";
                        String logMessage = "  at " + element.getClassName() + "." + element.getMethodName() + "(" + element.getFileName() + elLine + ")";
                        if (logMessage.indexOf('\r') >= 0) {
                            // Use half indentation, \r is permitted to be in the message to request additional visual focus.
                            logMessage = " " + logMessage.substring(logMessage.indexOf('\r') + 1);
                        }
                        log.error(logMessage);
                    }
                    nextException = nextException.getCause();
                }
            }
        }
    }

    private Logger findLogger() {
        return this.environment() != null ? this.environment().log() : LoggerFactory.getLogger(LoggingExceptionHandler.class);
    }

    /**
     * Returns the first message of the given {@link Throwable} or {@code null} if the given {@link Throwable} is
     * {@code null}.
     *
     * @param throwable The {@link Throwable} to get the first message from.
     * @return The first message of the given {@link Throwable} or {@code null}.
     */
    public static String firstMessage(final Throwable throwable) {
        Throwable next = throwable;
        while (next != null) {
            if (null != next.getMessage()) return next.getMessage();
            else {
                // Avoid infinitely looping if the throwable has itself as cause
                if (!next.equals(throwable.getCause())) next = next.getCause();
                else break;
            }
        }
        return "No message provided";
    }
}
