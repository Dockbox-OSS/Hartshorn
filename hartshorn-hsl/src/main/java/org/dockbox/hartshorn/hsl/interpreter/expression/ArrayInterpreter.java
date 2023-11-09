/*
 * Copyright 2019-2023 the original author or authors.
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

import java.util.function.BiFunction;

import org.dockbox.hartshorn.hsl.ast.ASTNode;
import org.dockbox.hartshorn.hsl.ast.expression.Expression;
import org.dockbox.hartshorn.hsl.interpreter.ASTNodeInterpreter;
import org.dockbox.hartshorn.hsl.interpreter.Array;
import org.dockbox.hartshorn.hsl.interpreter.InterpreterAdapter;
import org.dockbox.hartshorn.hsl.token.Token;

public abstract class ArrayInterpreter<R, T extends ASTNode> implements ASTNodeInterpreter<R, T> {

    protected Object accessArray(InterpreterAdapter adapter, Token name,
                               Expression indexExp, BiFunction<Array, Integer, Object> converter) {
        Array array = (Array) adapter.visitingScope().get(name);
        Number indexValue = (Number) adapter.evaluate(indexExp);
        int index = indexValue.intValue();

        if (index < 0 || array.length() < index) {
            throw new ArrayIndexOutOfBoundsException("Size can't be negative or bigger than array size");
        }

        return converter.apply(array, index);
    }
}
