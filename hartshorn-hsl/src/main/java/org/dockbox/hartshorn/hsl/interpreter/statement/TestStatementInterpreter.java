package org.dockbox.hartshorn.hsl.interpreter.statement;

import org.dockbox.hartshorn.hsl.ast.statement.TestStatement;
import org.dockbox.hartshorn.hsl.interpreter.ASTNodeInterpreter;
import org.dockbox.hartshorn.hsl.interpreter.InterpreterAdapter;
import org.dockbox.hartshorn.hsl.interpreter.InterpreterUtilities;
import org.dockbox.hartshorn.hsl.interpreter.VariableScope;
import org.dockbox.hartshorn.hsl.runtime.Return;

public class TestStatementInterpreter implements ASTNodeInterpreter<Void, TestStatement> {

    @Override
    public Void interpret(final TestStatement node, final InterpreterAdapter adapter) {
        final String name = String.valueOf(node.name().literal());
        final VariableScope variableScope = new VariableScope(adapter.global());
        try {
            adapter.execute(node.body(), variableScope);
        }
        catch (final Return r) {
            final Object value = r.value();
            final boolean val = InterpreterUtilities.isTruthy(value);
            adapter.interpreter().resultCollector().addResult(name, val);
        }
        return null;
    }
}
