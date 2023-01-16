package org.dockbox.hartshorn.hsl.interpreter.expression;

import org.dockbox.hartshorn.hsl.ast.ASTNode;
import org.dockbox.hartshorn.hsl.ast.expression.Expression;
import org.dockbox.hartshorn.hsl.interpreter.Array;
import org.dockbox.hartshorn.hsl.interpreter.InterpreterAdapter;
import org.dockbox.hartshorn.hsl.interpreter.ASTNodeInterpreter;
import org.dockbox.hartshorn.hsl.token.Token;

import java.util.function.BiFunction;

public abstract class ArrayInterpreter<R, T extends ASTNode> implements ASTNodeInterpreter<R, T> {

    protected Object accessArray(final InterpreterAdapter adapter, final Token name,
                               final Expression indexExp, final BiFunction<Array, Integer, Object> converter) {
        final Array array = (Array) adapter.visitingScope().get(name);
        final Double indexValue = (Double) adapter.evaluate(indexExp);
        final int index = indexValue.intValue();

        if (index < 0 || array.length() < index) {
            throw new ArrayIndexOutOfBoundsException("Size can't be negative or bigger than array size");
        }

        return converter.apply(array, index);
    }
}
