/*
 * Copyright 2019-2025 the original author or authors.
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

import java.util.stream.Stream;
import org.dockbox.hartshorn.hsl.ExecutableScript;
import org.dockbox.hartshorn.hsl.ScriptEvaluationError;
import org.dockbox.hartshorn.hsl.UseExpressionValidation;
import org.dockbox.hartshorn.hsl.runtime.DiagnosticMessage;
import org.dockbox.hartshorn.hsl.runtime.FormattedDiagnostic;
import org.dockbox.hartshorn.inject.annotations.Inject;
import org.dockbox.hartshorn.launchpad.ApplicationContext;
import org.dockbox.hartshorn.test.junit.HartshornIntegrationTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@HartshornIntegrationTest(includeBasePackages = false)
@UseExpressionValidation
public class VirtualClassTests {

    @Inject
    private ApplicationContext applicationContext;

    public static Stream<Arguments> propertyAccessors() {
        FormattedDiagnostic readPrivateMessage = FormattedDiagnostic.builder()
            .message(DiagnosticMessage.INVALID_PROPERTY_ACCESS)
            .argument("read")
            .argument("name")
            .argument("User")
            .argument("private")
            .argument("no members")
            .build();
        FormattedDiagnostic writePrivateMessage = FormattedDiagnostic.builder()
            .message(DiagnosticMessage.INVALID_PROPERTY_ACCESS)
            .argument("assign to")
            .argument("name")
            .argument("User")
            .argument("private")
            .argument("no members")
            .build();
        FormattedDiagnostic reassignFinalMessage = FormattedDiagnostic.builder()
            .message(DiagnosticMessage.ILLEGAL_FINAL_X_OF_Y_REASSIGNMENT)
            .argument("property")
            .argument("name")
            .argument("User")
            .build();
        return Stream.of(
                Arguments.of("public", "getName()", null),
                Arguments.of("private", "getName()", null),
                Arguments.of("", "getName()", null),
                Arguments.of("public", "name", null),
                Arguments.of("private", "name", readPrivateMessage),
                Arguments.of("", "name", null),
                Arguments.of("public", "setName(\"Foo\")", null),
                Arguments.of("private", "setName(\"Foo\")", null),
                Arguments.of("", "setName(\"Foo\")", null),
                Arguments.of("public", "name = \"Foo\"", null),
                Arguments.of("private", "name = \"Foo\"", writePrivateMessage),
                Arguments.of("", "name = \"Foo\"", null),
                Arguments.of("public final", "getName()", null),
                Arguments.of("private final", "getName()", null),
                Arguments.of("final", "getName()", null),
                Arguments.of("public final", "name", null),
                Arguments.of("private final", "name", readPrivateMessage),
                Arguments.of("final", "name", null),
                Arguments.of("public final", "setName(\"Foo\")", reassignFinalMessage),
                Arguments.of("private final", "setName(\"Foo\")", reassignFinalMessage),
                Arguments.of("final", "setName(\"Foo\")", reassignFinalMessage),
                Arguments.of("public final", "name = \"Foo\"", reassignFinalMessage),
                Arguments.of("private final", "name = \"Foo\"", writePrivateMessage),
                Arguments.of("final", "name = \"Foo\"", reassignFinalMessage)
        );
    }

    @ParameterizedTest
    @MethodSource("propertyAccessors")
    void test(String modifier, String accessor, FormattedDiagnostic message) {
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

        // If an error message is expected, assert that the script evaluation fails with the expected message
        if (message != null) {
            ScriptEvaluationError error = Assertions.assertThrows(ScriptEvaluationError.class, script::evaluate);
            HSLTestUtilities.assertEvaluationError(error, message);
        } else {
            Assertions.assertDoesNotThrow(script::evaluate);
        }
    }
}
