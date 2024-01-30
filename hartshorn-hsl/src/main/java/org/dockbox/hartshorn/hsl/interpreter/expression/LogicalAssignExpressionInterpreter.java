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

package org.dockbox.hartshorn.hsl.interpreter.expression;

import org.dockbox.hartshorn.hsl.ast.expression.LogicalAssignExpression;
import org.dockbox.hartshorn.hsl.interpreter.Interpreter;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.type.TokenType;

public class LogicalAssignExpressionInterpreter extends BitwiseInterpreter<Object, LogicalAssignExpression> {

    @Override
    public Object interpret(LogicalAssignExpression node, Interpreter interpreter) {
        Token name = node.name();
        Object left = interpreter.lookUpVariable(name, node);

        Object right = interpreter.evaluate(node.value());

        Token op = node.assignmentOperator();
        TokenType bitwiseOperator = node.logicalOperator();

        // Virtual token to indicate the position of the operator
        Token token = Token.of(bitwiseOperator)
                .lexeme(op.lexeme())
                .position(op)
                .build();
        Object result = this.getBitwiseResult(token, left, right);

        Integer distance = interpreter.distance(node);
        if (distance != null) {
            interpreter.visitingScope().assignAt(distance, name, result);
        }
        else {
            interpreter.global().assign(name, result);
        }
        return result;
    }
}
