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

package test.org.dockbox.hartshorn.hsl;

import org.dockbox.hartshorn.launchpad.ApplicationContext;
import org.dockbox.hartshorn.hsl.ExecutableScript;
import org.dockbox.hartshorn.hsl.ScriptEvaluationError;
import org.dockbox.hartshorn.hsl.UseExpressionValidation;
import org.dockbox.hartshorn.test.junit.HartshornIntegrationTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import org.dockbox.hartshorn.inject.annotations.Inject;

@HartshornIntegrationTest(includeBasePackages = false)
@UseExpressionValidation
public class VirtualClassTests {

    @Inject
    private ApplicationContext applicationContext;

    public static Stream<Arguments> propertyAccessors() {
        final String readMessage = "HSL4026: Cannot read property name of User because it is not accessible from the current scope. The property is declared private and has no members.";
        final String writeMessage = "HSL4026: Cannot assign to property name of User because it is not accessible from the current scope. The property is declared private and has no members.";
        final String reassignMessage = "HSL5006: Cannot reassign property name of User because it is final.";
        return Stream.of(
                Arguments.of("public", "getName()", false, null),
                Arguments.of("private", "getName()", false, null),
                Arguments.of("", "getName()", false, null),

                Arguments.of("public", "name", false, null),
                Arguments.of("private", "name", true, readMessage),
                Arguments.of("", "name", false, null),

                Arguments.of("public", "setName(\"Foo\")", false, null),
                Arguments.of("private", "setName(\"Foo\")", false, null),
                Arguments.of("", "setName(\"Foo\")", false, null),

                Arguments.of("public", "name = \"Foo\"", false, null),
                Arguments.of("private", "name = \"Foo\"", true, writeMessage),
                Arguments.of("", "name = \"Foo\"", false, null),

                Arguments.of("public final", "getName()", false, null),
                Arguments.of("private final", "getName()", false, null),
                Arguments.of("final", "getName()", false, null),

                Arguments.of("public final", "name", false, null),
                Arguments.of("private final", "name", true, readMessage),
                Arguments.of("final", "name", false, null),

                Arguments.of("public final", "setName(\"Foo\")", true, reassignMessage),
                Arguments.of("private final", "setName(\"Foo\")", true, reassignMessage),
                Arguments.of("final", "setName(\"Foo\")", true, reassignMessage),

                Arguments.of("public final", "name = \"Foo\"", true, reassignMessage),
                Arguments.of("private final", "name = \"Foo\"", true, writeMessage),
                Arguments.of("final", "name = \"Foo\"", true, reassignMessage)
        );
    }

    @ParameterizedTest
    @MethodSource("propertyAccessors")
    void test(String modifier, String accessor, boolean shouldFail, String message) {
        ExecutableScript script = ExecutableScript.of(this.applicationContext, """
                class User {
                    %s name;
                    constructor(name) {
                        this.name = name;
                    }
                    function getName() {
                        return this.name;
                    }
                    function setName(name) {
                        this.name = name;
                    }
                }
                var user = User("Bar");
                user.%s;
                """.formatted(modifier, accessor));

        if (shouldFail) {
            ScriptEvaluationError error = Assertions.assertThrows(ScriptEvaluationError.class, script::evaluate);
            String errorMessage = error.getMessage();
            errorMessage = errorMessage.substring(0, errorMessage.indexOf(" While "));
            Assertions.assertEquals(message, errorMessage);
        } else {
            Assertions.assertDoesNotThrow(script::evaluate);
        }
    }
}
