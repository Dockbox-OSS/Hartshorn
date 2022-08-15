package org.dockbox.hartshorn.hsl;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.testsuite.HartshornTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import jakarta.inject.Inject;

@HartshornTest
@UseExpressionValidation
public class VirtualClassTests {

    @Inject
    private ApplicationContext applicationContext;

    public static Stream<Arguments> propertyAccessors() {
        return Stream.of(
                Arguments.of("public", "getName()", false, null),
                Arguments.of("private", "getName()", false, null),
                Arguments.of("", "getName()", false, null),

                Arguments.of("public", "name", false, null),
                Arguments.of("private", "name", true, "Cannot access property 'name' outside of its class scope"),
                Arguments.of("", "name", false, null),

                Arguments.of("public", "setName(\"Foo\")", false, null),
                Arguments.of("private", "setName(\"Foo\")", false, null),
                Arguments.of("", "setName(\"Foo\")", false, null),

                Arguments.of("public", "name = \"Foo\"", false, null),
                Arguments.of("private", "name = \"Foo\"", true, "Cannot access property 'name' outside of its class scope"),
                Arguments.of("", "name = \"Foo\"", false, null),

                Arguments.of("public final", "getName()", false, null),
                Arguments.of("private final", "getName()", false, null),
                Arguments.of("final", "getName()", false, null),

                Arguments.of("public final", "name", false, null),
                Arguments.of("private final", "name", true, "Cannot access property 'name' outside of its class scope"),
                Arguments.of("final", "name", false, null),

                Arguments.of("public final", "setName(\"Foo\")", true, "Cannot reassign final property 'name'"),
                Arguments.of("private final", "setName(\"Foo\")", true, "Cannot reassign final property 'name'"),
                Arguments.of("final", "setName(\"Foo\")", true, "Cannot reassign final property 'name'"),

                Arguments.of("public final", "name = \"Foo\"", true, "Cannot reassign final property 'name'"),
                Arguments.of("private final", "name = \"Foo\"", true, "Cannot reassign final property 'name'"),
                Arguments.of("final", "name = \"Foo\"", true, "Cannot reassign final property 'name'")
        );
    }

    @ParameterizedTest
    @MethodSource("propertyAccessors")
    void test(final String modifier, final String accessor, final boolean shouldFail, final String message) {
        final HslScript script = HslScript.of(this.applicationContext, """
                class User {
                    %s name;
                    constructor(name) {
                        this.name = name;
                    }
                    fun getName() {
                        return this.name;
                    }
                    fun setName(name) {
                        this.name = name;
                    }
                }
                var user = User("Bar");
                user.%s;
                """.formatted(modifier, accessor));

        if (shouldFail) {
            final ScriptEvaluationError error = Assertions.assertThrows(ScriptEvaluationError.class, script::evaluate);
            String errorMessage = error.getMessage();
            errorMessage = errorMessage.substring(0, errorMessage.indexOf("."));
            Assertions.assertEquals(message, errorMessage);
        } else {
            Assertions.assertDoesNotThrow(script::evaluate);
        }
    }
}
