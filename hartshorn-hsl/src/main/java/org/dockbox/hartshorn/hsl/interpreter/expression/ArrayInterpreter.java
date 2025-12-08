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

package org.dockbox.hartshorn.hsl.interpreter.expression;

import org.dockbox.hartshorn.hsl.ScriptEvaluationError;
import org.dockbox.hartshorn.hsl.ast.ASTNode;
import org.dockbox.hartshorn.hsl.ast.expression.Expression;
import org.dockbox.hartshorn.hsl.interpreter.ASTNodeInterpreter;
import org.dockbox.hartshorn.hsl.interpreter.Array;
import org.dockbox.hartshorn.hsl.interpreter.Interpreter;
import org.dockbox.hartshorn.hsl.runtime.DiagnosticMessage;
import org.dockbox.hartshorn.hsl.runtime.Phase;
import org.dockbox.hartshorn.hsl.token.Token;

import java.util.function.BiFunction;

/**
 * Base interpreter for array access expressions.
 *
 * @param <R> return type, typically the element type of the array
 * @param <T> the specific AST node type this interpreter handles
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public abstract class ArrayInterpreter<R, T extends ASTNode> implements ASTNodeInterpreter<R, T> {

    protected Object accessArray(
            Interpreter interpreter, Token name, Expression indexExpression,
            BiFunction<Array, Integer, Object> converter
    ) {
        Array array = (Array) interpreter.visitingScope().get(name);
        Number indexValue = (Number) interpreter.evaluate(indexExpression);
        int index = indexValue.intValue();

        if (index < 0 || array.length() < index) {
            throw ScriptEvaluationError.builder(Phase.INTERPRETING)
                    .message(DiagnosticMessage.ARRAY_INDEX_OUT_OF_BOUNDS, index, array.length())
                    .at(indexExpression)
                    .build();
        }

        return converter.apply(array, index);
    }
}
