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

import org.dockbox.hartshorn.hsl.ScriptEvaluationError;
import org.dockbox.hartshorn.hsl.ast.expression.PostfixExpression;
import org.dockbox.hartshorn.hsl.ast.expression.VariableExpression;
import org.dockbox.hartshorn.hsl.interpreter.ASTNodeInterpreter;
import org.dockbox.hartshorn.hsl.interpreter.Interpreter;
import org.dockbox.hartshorn.hsl.interpreter.InterpreterUtilities;
import org.dockbox.hartshorn.hsl.runtime.Phase;
import org.dockbox.hartshorn.hsl.token.type.ArithmeticTokenType;
import org.dockbox.hartshorn.hsl.token.type.TokenType;

/**
 * TODO: #1061 Add documentation
 *
 * @param <Object> ...
 * @param <PostfixExpression>> ...
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public class PostfixExpressionInterpreter implements ASTNodeInterpreter<Object, PostfixExpression> {

    @Override
    public Object interpret(PostfixExpression node, Interpreter interpreter) {
        Object left = interpreter.evaluate(node.leftExpression());
        InterpreterUtilities.checkNumberOperand(node.operator(), left);

        TokenType type = node.operator().type();
        if (!(type instanceof ArithmeticTokenType arithmeticTokenType)) {
            throw new ScriptEvaluationError("Invalid postfix operator " + type, Phase.INTERPRETING, node.operator());
        }
        double newValue = switch (arithmeticTokenType) {
            case PLUS_PLUS -> (double) left + 1;
            case MINUS_MINUS -> (double) left -1;
            default -> throw new ScriptEvaluationError("Invalid postfix operator " + type, Phase.INTERPRETING, node.operator());
        };

        if (node.leftExpression() instanceof VariableExpression variable) {
            interpreter.visitingScope().assign(variable.name(), newValue);
        }
        return left;
    }
}
