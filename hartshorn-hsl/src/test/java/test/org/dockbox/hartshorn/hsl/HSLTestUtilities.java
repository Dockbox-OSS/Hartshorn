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

import org.dockbox.hartshorn.hsl.ExecutableScript;
import org.dockbox.hartshorn.hsl.ScriptEvaluationError;
import org.dockbox.hartshorn.hsl.runtime.FormattedDiagnostic;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

public class HSLTestUtilities {

    public static void assertEvaluationFails(
            ExecutableScript executableScript,
            FormattedDiagnostic diagnosticMessage
    ) {
        ScriptEvaluationError error = assertThatExceptionOfType(ScriptEvaluationError.class).isThrownBy(executableScript::evaluate).actual();
        assertEvaluationError(error, diagnosticMessage);
    }

    public static void assertEvaluationError(
            ScriptEvaluationError error,
            FormattedDiagnostic diagnosticMessage
    ) {
        String phase = error.phase().name().toLowerCase(Locale.ROOT);
        String expectedMessageStart = diagnosticMessage.format();
        String actualMessageStart = error.getMessage().split("While " + phase)[0].trim();
        assertThat(actualMessageStart).isEqualTo(expectedMessageStart);
    }

    public static void assertEvaluationFailedAtPosition(ScriptEvaluationError error, int line, int column) {
        assertThat(error.line()).isEqualTo(line);
        assertThat(error.column()).isEqualTo(column);
    }
}
