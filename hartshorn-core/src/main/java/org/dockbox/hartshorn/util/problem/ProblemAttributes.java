package org.dockbox.hartshorn.util.problem;

import org.dockbox.hartshorn.util.option.Option;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ProblemAttributes {

    private static final Function<String, String[]> STRING_FORMATTER = value -> new String[] { value };

    public static final ProblemAttribute<ProblemSeverity> SEVERITY = SimpleProblemAttribute.of(
            "Severity", ProblemSeverity.ERROR,
            value -> ProblemAttributeValidator.instanceOf(ProblemSeverity.class, "Severity", value),
            value -> STRING_FORMATTER.apply(value.name()));

    public static final ProblemAttribute<String> MESSAGE = SimpleProblemAttribute.of(
            "Message", "",
            value -> ProblemAttributeValidator.nonEmpty("Message", value),
            STRING_FORMATTER);

    public static final ProblemAttribute<String> SHORT_DESCRIPTION = SimpleProblemAttribute.of(
            "Short description", "",
            value -> ProblemAttributeValidator.nonEmpty("Short description", value),
            STRING_FORMATTER);

    public static final ProblemAttribute<String> LONG_DESCRIPTION = SimpleProblemAttribute.of(
            "Long description", "",
            value -> ProblemAttributeValidator.nonEmpty("Long description", value),
            STRING_FORMATTER);

    public static final ProblemAttribute<String> REASON = SimpleProblemAttribute.of(
            "Reason", "",
            value -> ProblemAttributeValidator.nonEmpty("Reason", value),
            STRING_FORMATTER);

    public static final ProblemAttribute<String> DOCUMENTATION_LINK = SimpleProblemAttribute.of(
            "Documentation link", "",
            value -> ProblemAttributeValidator.nonEmpty("Documentation link", value),
            STRING_FORMATTER);

    public static final ProblemAttribute<List<String>> SOLUTIONS = SimpleProblemAttribute.of(
            "Possible solutions", new ArrayList<>(),
            value -> ProblemAttributeValidator.instanceOf(List.class, "Solutions", value)
                    .orComputeFlat(() -> {
                        for (final Object solution : value) {
                            if (!(solution instanceof String)) {
                                return Option.of("Solutions must be a list of strings");
                            }
                        }
                        return Option.empty();
                    }),
            value -> value.stream().map("- %s"::formatted).toArray(String[]::new));

    public static final ProblemAttribute<Throwable> CAUSE = SimpleProblemAttribute.of(
            "Cause", null,
            value -> ProblemAttributeValidator.instanceOf(Throwable.class, "Cause", value),
            value -> {
                throw new UnsupportedOperationException("Cannot format cause, this should be handled using ExceptionUtilities#format");
            });

    public static final ProblemAttribute<Class<?>> ORIGIN = SimpleProblemAttribute.of(
            "Origin", null,
            value -> ProblemAttributeValidator.instanceOf(Class.class, "Origin", value),
            value -> STRING_FORMATTER.apply(value.getSimpleName()));

}
