package org.dockbox.hartshorn.hsl.interpreter.statement;

import org.dockbox.hartshorn.hsl.ast.statement.SwitchCase;
import org.dockbox.hartshorn.hsl.ast.statement.SwitchStatement;
import org.dockbox.hartshorn.hsl.interpreter.ASTNodeInterpreter;
import org.dockbox.hartshorn.hsl.interpreter.InterpreterAdapter;
import org.dockbox.hartshorn.hsl.interpreter.InterpreterUtilities;

public class SwitchStatementInterpreter implements ASTNodeInterpreter<Void, SwitchStatement> {

    @Override
    public Void interpret(final SwitchStatement node, final InterpreterAdapter adapter) {
        Object value = adapter.evaluate(node.expression());
        value = InterpreterUtilities.unwrap(value);
        for (final SwitchCase switchCase : node.cases()) {
            if (InterpreterUtilities.isEqual(value, switchCase.expression().value())) {
                adapter.execute(switchCase);
                return null;
            }
        }
        if (node.defaultCase() != null) {
            adapter.withNextScope(() -> adapter.execute(node.defaultCase()));
        }
        return null;
    }
}
