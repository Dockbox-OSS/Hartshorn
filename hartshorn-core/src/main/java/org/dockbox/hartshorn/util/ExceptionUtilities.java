package org.dockbox.hartshorn.util;

import java.util.ArrayList;
import java.util.Collection;

public final class ExceptionUtilities {

    private ExceptionUtilities() {
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

    public static String[] format(final String message, final Throwable throwable, final boolean stacktraces) {
        final Collection<String> formattedLines = new ArrayList<>();
        String errorMessage = message;
        if (null != throwable) {

            String location = "";
            if (0 < throwable.getStackTrace().length) {
                final StackTraceElement root = throwable.getStackTrace()[0];
                final String line = 0 < root.getLineNumber() ? ":" + root.getLineNumber() : " (internal call)";
                location = root.getFileName() + line;
            }

            if (errorMessage == null) errorMessage = "";
            final String[] lines = errorMessage.split("\n");
            formattedLines.add("Exception: " + throwable.getClass().getCanonicalName() + " ("+ location +"): " + lines[0]);
            if (lines.length > 1) {
                for (int i = 1; i < lines.length; i++) {
                    formattedLines.add("  " + lines[i]);
                }
            }

            if (stacktraces) {
                Throwable nextException = throwable;

                while (null != nextException) {
                    final StackTraceElement[] trace = nextException.getStackTrace();
                    final String nextMessage = String.valueOf(nextException.getMessage());
                    final String[] nextLines = nextMessage.split("\n");
                    formattedLines.add(nextException.getClass().getCanonicalName() + ": " + nextLines[0]);
                    if (nextLines.length > 1) {
                        for (int i = 1; i < nextLines.length; i++) {
                            formattedLines.add("  " + nextLines[i]);
                        }
                    }

                    for (final StackTraceElement element : trace) {
                        final String elLine = 0 < element.getLineNumber() ? ":" + element.getLineNumber() : "(internal call)";
                        String logMessage = "  at " + element.getClassName() + "." + element.getMethodName() + "(" + element.getFileName() + elLine + ")";
                        if (logMessage.indexOf('\r') >= 0) {
                            // Use half indentation, \r is permitted to be in the message to request additional visual focus.
                            logMessage = " " + logMessage.substring(logMessage.indexOf('\r') + 1);
                        }
                        formattedLines.add(logMessage);
                    }
                    nextException = nextException.getCause();
                }
            }
        }
        return formattedLines.toArray(String[]::new);
    }
}
