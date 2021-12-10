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

import org.slf4j.Logger;

import lombok.Getter;
import lombok.Setter;

public class HartshornExceptionHandler implements ExceptionHandler {

    @Getter @Setter
    private boolean stacktraces;

    @Override
    public void handle(final Throwable throwable) {
        this.handle(firstMessage(throwable), throwable);
    }

    @Override
    public void handle(String message, final Throwable throwable) {
        if (null != throwable) {
            final Logger log = Hartshorn.log();

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
