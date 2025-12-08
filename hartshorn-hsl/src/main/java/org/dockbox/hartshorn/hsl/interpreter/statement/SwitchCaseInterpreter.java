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

package org.dockbox.hartshorn.hsl.interpreter.statement;

import org.dockbox.hartshorn.hsl.ScriptEvaluationError;
import org.dockbox.hartshorn.hsl.ast.FlowControlKeyword;
import org.dockbox.hartshorn.hsl.ast.statement.SwitchCase;
import org.dockbox.hartshorn.hsl.interpreter.Interpreter;
import org.dockbox.hartshorn.hsl.runtime.DiagnosticMessage;
import org.dockbox.hartshorn.hsl.runtime.Phase;

/**
 * Interpreter for {@link SwitchCase} nodes.
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public class SwitchCaseInterpreter implements StatementInterpreter<SwitchCase> {

    @Override
    public Void interpret(SwitchCase node, Interpreter interpreter) {
        interpreter.withNextScope(() -> {
            try {
                interpreter.execute(node.body());
            } catch (FlowControlKeyword keyword) {
                if (keyword.moveType() != FlowControlKeyword.MoveType.BREAK) {
                    throw ScriptEvaluationError.builder(Phase.INTERPRETING)
                            .message(
                                    DiagnosticMessage.UNEXPECTED_FLOW_CONTROL_X_IN_Y,
                                    keyword.moveType().name().toLowerCase(),
                                    "switch case"
                            )
                            .at(keyword.origin())
                            .build();
                }
            }
        });
        return null;
    }
}
