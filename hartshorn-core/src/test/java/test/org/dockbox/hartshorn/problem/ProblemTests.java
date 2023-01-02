package test.org.dockbox.hartshorn.problem;

import org.dockbox.hartshorn.util.problem.LoggerProblemReporter;
import org.dockbox.hartshorn.util.problem.Problem;
import org.dockbox.hartshorn.util.problem.ProblemAttribute;
import org.dockbox.hartshorn.util.problem.ProblemAttributes;
import org.dockbox.hartshorn.util.problem.ProblemBuilder;
import org.dockbox.hartshorn.util.problem.ProblemSeverity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class ProblemTests {

    public static Stream<Arguments> builderProperties() {
        return Stream.of(
                arguments(ProblemBuilder::severity, ProblemAttributes.SEVERITY, ProblemSeverity.ERROR),
                arguments(ProblemBuilder::message, ProblemAttributes.MESSAGE, "This is a test message"),
                arguments(ProblemBuilder::shortDescription, ProblemAttributes.SHORT_DESCRIPTION, "This is a test short description"),
                arguments(ProblemBuilder::longDescription, ProblemAttributes.LONG_DESCRIPTION, "This is a test long description"),
                arguments(ProblemBuilder::reason, ProblemAttributes.REASON, "This is a test reason"),
                arguments(ProblemBuilder::documentationLink, ProblemAttributes.DOCUMENTATION_LINK, "https://www.dockbox.org"),
                arguments(ProblemBuilder::cause, ProblemAttributes.CAUSE, new RuntimeException("This is a test cause")),
                arguments(ProblemBuilder::origin, ProblemAttributes.ORIGIN, ProblemTests.class),
                arguments(ProblemBuilder::solutions, ProblemAttributes.SOLUTIONS, List.of("This is a test solution"))
        );
    }

    public static <T> Arguments arguments(final BiConsumer<ProblemBuilder, T> action, final ProblemAttribute<T> attribute, final T value) {
        return Arguments.of((Consumer<ProblemBuilder>) builder -> action.accept(builder, value), attribute, value);
    }

    @ParameterizedTest
    @MethodSource("builderProperties")
    void testProblemBuilderProperty(final Consumer<ProblemBuilder> action, final ProblemAttribute<?> attribute, final Object value) {
        final ProblemBuilder builder = new ProblemBuilder();
        action.accept(builder);
        final Problem problem = builder.build();

        Assertions.assertTrue(problem.hasAttribute(attribute));
        Assertions.assertEquals(value, problem.attribute(attribute));
    }

    @Test
    void name() {
        final Problem problem = new ProblemBuilder()
                .severity(ProblemSeverity.ERROR)
                .message("This is a test message")
                .shortDescription("This is a test short description")
                .longDescription("This is a test long description")
                .reason("This is a test reason")
                .documentationLink("https://www.dockbox.org")
                .cause(new RuntimeException("This is a test cause"))
                .origin(ProblemTests.class)
                .solutions(List.of("This is a test solution"))
                .build();
        new LoggerProblemReporter(null).report(problem);
    }
}
