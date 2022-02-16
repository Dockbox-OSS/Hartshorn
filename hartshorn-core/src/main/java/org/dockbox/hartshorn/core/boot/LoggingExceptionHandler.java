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

package org.dockbox.hartshorn.core.boot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Getter;
import lombok.Setter;

/**
 * This class is used to handle exceptions that occur during the application lifecycle. This default implementation
 * of the {@link ExceptionHandler} interface logs the exception to the {@link Logger} and is able to correctly display
 * stacktraces when {@link #stacktraces()} is {@code true}.
 *
 * @author Guus Lieben
 * @since 21.9
 */
public class LoggingExceptionHandler implements ExceptionHandler, ApplicationManaged {

    @Getter @Setter
    private boolean stacktraces;

    @Getter
    private ApplicationManager applicationManager;

    @Override
    public void handle(final Throwable throwable) {
        this.handle(firstMessage(throwable), throwable);
    }

    @Override
    public void handle(String message, final Throwable throwable) {
        if (null != throwable) {
            final Logger log = this.applicationManager() != null ? this.applicationManager().log() : LoggerFactory.getLogger(LoggingExceptionHandler.class);

            String location = "";
            if (0 < throwable.getStackTrace().length) {
                final StackTraceElement root = throwable.getStackTrace()[0];
                final String line = 0 < root.getLineNumber() ? ":" + root.getLineNumber() : "(internal call)";
                location = root.getFileName() + line;
            }

            if (message == null) message = "";
            log.error("Exception: " + throwable.getClass().getCanonicalName() + " ("+ location +"): " + message);

            if (this.stacktraces()) {
                Throwable nextException = throwable;

                while (null != nextException) {
                    final StackTraceElement[] trace = nextException.getStackTrace();
                    log.error(nextException.getClass().getCanonicalName() + ": " + nextException.getMessage());

                    for (final StackTraceElement element : trace) {
                        final String elLine = 0 < element.getLineNumber() ? ":" + element.getLineNumber() : "(internal call)";
                        log.error("  at " + element.getClassName() + "." + element.getMethodName() + "(" + element.getFileName() + elLine + ")");
                    }
                    nextException = nextException.getCause();
                }
            }
        }
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

    @Override
    public void applicationManager(final ApplicationManager applicationManager) {
        if (this.applicationManager == null) this.applicationManager = applicationManager;
        else throw new IllegalArgumentException("Application manager has already been configured");
    }
}
