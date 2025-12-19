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
import org.dockbox.hartshorn.hsl.ast.statement.RepeatStatement;
import org.dockbox.hartshorn.hsl.interpreter.Interpreter;
import org.dockbox.hartshorn.hsl.runtime.DiagnosticMessage;
import org.dockbox.hartshorn.hsl.runtime.Phase;

/**
 * Interpreter for {@link RepeatStatement} nodes.
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public class RepeatStatementInterpreter implements StatementInterpreter<RepeatStatement> {

    @Override
    public Void interpret(RepeatStatement node, Interpreter interpreter) {
        interpreter.withNextScope(() -> {
            Object value = interpreter.evaluate(node.value());

            if (!(value instanceof Number number)) {
                throw ScriptEvaluationError.builder(Phase.INTERPRETING)
                    .message(DiagnosticMessage.NON_NUMBER_OPERAND, value)
                    .at(node.value())
                    .build();
            }
            if (number.doubleValue() < 0) {
                throw ScriptEvaluationError.builder(Phase.INTERPRETING)
                    .message(DiagnosticMessage.ILLEGAL_NEGATIVE_NUMBER, number.doubleValue())
                    .at(node.value())
                    .build();
            }

            int counter = number.intValue();
            for (int i = 0; i < counter; i++) {
                try {
                    interpreter.execute(node.body());
                }
                catch (FlowControlKeyword keyword) {
                    if (keyword.moveType() == FlowControlKeyword.MoveType.BREAK) {
                        break;
                    }
                }
            }
        });
        return null;
    }
}
