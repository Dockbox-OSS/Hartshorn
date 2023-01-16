package org.dockbox.hartshorn.hsl.interpreter.statement;

import org.dockbox.hartshorn.hsl.ast.statement.ReturnStatement;
import org.dockbox.hartshorn.hsl.interpreter.ASTNodeInterpreter;
import org.dockbox.hartshorn.hsl.interpreter.InterpreterAdapter;
import org.dockbox.hartshorn.hsl.runtime.Return;

public class ReturnStatementInterpreter implements ASTNodeInterpreter<Void, ReturnStatement> {

    @Override
    public Void interpret(final ReturnStatement node, final InterpreterAdapter adapter) {
        Object value = null;
        if (node.value() != null) {
            value = adapter.evaluate(node.value());
        }
        throw new Return(value);
    }
}
