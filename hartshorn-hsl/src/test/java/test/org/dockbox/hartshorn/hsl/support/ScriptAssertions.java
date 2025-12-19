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

package test.org.dockbox.hartshorn.hsl.support;

import org.dockbox.hartshorn.hsl.ExecutableScript;
import org.dockbox.hartshorn.hsl.ScriptEvaluationError;
import org.dockbox.hartshorn.hsl.runtime.DiagnosticMessage;
import org.dockbox.hartshorn.hsl.runtime.FormattedDiagnostic;
import org.dockbox.hartshorn.util.StringUtilities;
import org.junit.jupiter.api.Assertions;

import java.util.Locale;

public class ScriptAssertions {

    public static void assertEvaluationFails(
        ExecutableScript executableScript,
        FormattedDiagnostic diagnosticMessage
    ) {
        ScriptEvaluationError error =
            Assertions.assertThrows(ScriptEvaluationError.class, executableScript::evaluate);
        assertEvaluationError(error, diagnosticMessage);
    }

    public static void assertEvaluationError(
        ScriptEvaluationError error,
        FormattedDiagnostic diagnosticMessage
    ) {
        if (diagnosticMessage.message().phase() != null) {
            Assertions.assertEquals(diagnosticMessage.message().phase(), error.phase());
        }
        String phase = error.phase().name().toLowerCase(Locale.ROOT);
        String expectedMessageStart = diagnosticMessage.format();
        String actualMessageStart = error.getMessage().split("While " + phase)[0].trim();
        Assertions.assertEquals(expectedMessageStart, actualMessageStart);
    }

    public static void assertEvaluationError(
        ScriptEvaluationError error,
        DiagnosticMessage diagnosticMessage
    ) {
        if (diagnosticMessage.phase() != null) {
            Assertions.assertEquals(diagnosticMessage.phase(), error.phase());
        }
        String phase = error.phase().name().toLowerCase(Locale.ROOT);
        String actualMessageStart = error.getMessage().split("While " + phase)[0].trim();
        String rawMessage = diagnosticMessage.format();
        Assertions.assertTrue(
            StringUtilities.matchesFormatted(rawMessage, actualMessageStart),
            "Expected message to match:\n" +
                rawMessage + "\nbut was:\n" +
                actualMessageStart
        );
    }

    public static void assertEvaluationFailedAtPosition(
        ScriptEvaluationError error,
        int line,
        int column
    ) {
        Assertions.assertEquals(line, error.line());
        Assertions.assertEquals(column, error.column());
    }
}
