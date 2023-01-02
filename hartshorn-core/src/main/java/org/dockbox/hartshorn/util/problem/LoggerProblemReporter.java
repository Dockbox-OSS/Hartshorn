package org.dockbox.hartshorn.util.problem;

import org.dockbox.hartshorn.logging.ApplicationLogger;
import org.dockbox.hartshorn.logging.LogExclude;
import org.dockbox.hartshorn.util.ExceptionUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.inject.Inject;

@LogExclude
public class LoggerProblemReporter implements ProblemReporter {

    private final ApplicationLogger applicationLogger;

    @Inject
    public LoggerProblemReporter(final ApplicationLogger applicationLogger) {
        this.applicationLogger = applicationLogger;
    }

    @Override
    public void report(final Problem problem) {
        final Logger logger = this.logger(problem);
        final Level level = this.level(problem);
        final String[] lines = this.format(problem);
        for (final String message : lines) {
            switch (level) {
                case TRACE -> logger.trace(message);
                case DEBUG -> logger.debug(message);
                case INFO -> logger.info(message);
                case WARN -> logger.warn(message);
                case ERROR -> logger.error(message);
            }
        }
    }

    /**
     * Formats the given {@link Problem} into an array of strings, each string representing a single line. This will
     * format all standard attributes in order, followed by any custom attributes. If {@link ProblemAttributes#CAUSE} is
     * present, the cause will be formatted and appended to the end of the message.
     *
     * <p>A typical formatted message will look like this:
     * <pre>{@code
     * Problem reported at LoggerProblemReporter: This is a test message
     * Short description: This description is short
     * Long description: This description is very long
     * Reason: This is a test
     * Documentation Link: https://www.example.com/wiki
     * Possible solutions:
     * - This is a possible solution
     * - This is another possible solution
     * ... additional properties ...
     * Exception: java.lang.RuntimeException (ProblemTests.java:56): This is a test message
     * java.lang.RuntimeException: This is a test cause
     *   at test.org.dockbox.hartshorn.problem.ProblemTests.name(ProblemTests.java:56)
     *   at jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(NativeMethodAccessorImpl.java(internal call))
     *   at jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:77)
     *   at jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
     *   at java.lang.reflect.Method.invoke(Method.java:568)
     *   at org.junit.platform.commons.util.ReflectionUtils.invokeMethod(ReflectionUtils.java:727)
     * }</pre>
     *
     * @param problem The {@link Problem} to format.
     * @return The formatted message.
     */
    private String[] format(final Problem problem) {
        final Map<ProblemAttribute<?>, Object> attributes = new HashMap<>(problem.attributes());
        attributes.remove(ProblemAttributes.SEVERITY);

        String origin = null;
        if (attributes.containsKey(ProblemAttributes.ORIGIN)) {
            attributes.remove(ProblemAttributes.ORIGIN);
            origin = problem.attribute(ProblemAttributes.ORIGIN).getSimpleName();
        }

        final String[] formattedException;
        if (attributes.containsKey(ProblemAttributes.CAUSE)) {
            final String message = problem.attribute(ProblemAttributes.MESSAGE);
            final Throwable cause = (Throwable) attributes.remove(ProblemAttributes.CAUSE);
            formattedException = ExceptionUtilities.format(message, cause, true);

            if (origin == null) {
                origin = cause.getStackTrace()[0].toString();
            }
        }
        else formattedException = new String[0];

        final StringBuilder builder = new StringBuilder();
        builder.append("Problem reported");
        if (origin != null) builder.append(" at ").append(origin);

        if (attributes.containsKey(ProblemAttributes.MESSAGE)) {
            builder.append(": ")
                    .append(attributes.remove(ProblemAttributes.MESSAGE));
        }

        builder.append(System.lineSeparator());

        this.append(builder, attributes, ProblemAttributes.SHORT_DESCRIPTION);
        this.append(builder, attributes, ProblemAttributes.LONG_DESCRIPTION);
        this.append(builder, attributes, ProblemAttributes.REASON);
        this.append(builder, attributes, ProblemAttributes.DOCUMENTATION_LINK);

        if (attributes.containsKey(ProblemAttributes.SOLUTIONS)) {
            attributes.remove(ProblemAttributes.SOLUTIONS);

            final List<String> solutions = problem.attribute(ProblemAttributes.SOLUTIONS);
            if (!solutions.isEmpty()) {
                builder.append(ProblemAttributes.SOLUTIONS.name())
                        .append(":")
                        .append(System.lineSeparator());

                for (final String solution : solutions) {
                    builder.append(" - ")
                            .append(solution)
                            .append(System.lineSeparator());
                }
            }
        }

        for (final Map.Entry<ProblemAttribute<?>, Object> entry : attributes.entrySet()) {
            builder.append(entry.getKey().name())
                    .append(": ")
                    .append(entry.getValue())
                    .append(System.lineSeparator());
        }

        for (final String line : formattedException) {
            builder.append(line).append(System.lineSeparator());
        }

        return builder.toString().split(System.lineSeparator());
    }

    private void append(final StringBuilder builder, final Map<ProblemAttribute<?>, Object> attributes,
                        final ProblemAttribute<?> attribute) {
        if (attributes.containsKey(attribute)) {
            builder.append(attribute.name())
                    .append(": ")
                    .append(attributes.remove(attribute))
                    .append(System.lineSeparator());
        }
    }

    private Logger logger(final Problem problem) {
        if (problem.hasAttribute(ProblemAttributes.ORIGIN)) {
            return LoggerFactory.getLogger(problem.attribute(ProblemAttributes.ORIGIN));
        }
        return this.applicationLogger.log();
    }

    private Level level(final Problem problem) {
        if (problem.hasAttribute(ProblemAttributes.SEVERITY)) {
            final ProblemSeverity severity = problem.attribute(ProblemAttributes.SEVERITY);
            return switch (severity) {
                case DEPRECATION, WARNING, INFO -> Level.WARN;
                case ERROR, FATAL -> Level.ERROR;
                case DEBUG -> Level.DEBUG;
                case TRACE -> Level.TRACE;
                case UNKNOWN -> Level.INFO;
            };
        }
        return Level.INFO;
    }
}
