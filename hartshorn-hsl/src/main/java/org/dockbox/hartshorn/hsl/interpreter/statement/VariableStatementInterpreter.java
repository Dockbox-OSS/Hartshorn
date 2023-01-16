package org.dockbox.hartshorn.hsl.interpreter.statement;

import org.dockbox.hartshorn.hsl.ast.statement.VariableStatement;
import org.dockbox.hartshorn.hsl.interpreter.ASTNodeInterpreter;
import org.dockbox.hartshorn.hsl.interpreter.InterpreterAdapter;

public class VariableStatementInterpreter implements ASTNodeInterpreter<Void, VariableStatement> {

    @Override
    public Void interpret(final VariableStatement node, final InterpreterAdapter adapter) {
        Object value = null;
        if (node.initializer() != null) {
            value = adapter.evaluate(node.initializer());
        }
        adapter.visitingScope().define(node.name().lexeme(), value);
        return null;
    }
}
