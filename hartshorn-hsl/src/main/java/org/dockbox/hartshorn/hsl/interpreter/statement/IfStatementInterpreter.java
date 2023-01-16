package org.dockbox.hartshorn.hsl.interpreter.statement;

import org.dockbox.hartshorn.hsl.ast.statement.IfStatement;
import org.dockbox.hartshorn.hsl.interpreter.ASTNodeInterpreter;
import org.dockbox.hartshorn.hsl.interpreter.InterpreterAdapter;
import org.dockbox.hartshorn.hsl.interpreter.InterpreterUtilities;
import org.dockbox.hartshorn.hsl.interpreter.VariableScope;

public class IfStatementInterpreter implements ASTNodeInterpreter<Void, IfStatement> {

    @Override
    public Void interpret(final IfStatement node, final InterpreterAdapter adapter) {
        final Object conditionResult = adapter.evaluate(node.condition());
        final VariableScope previous = adapter.visitingScope();

        if (InterpreterUtilities.isTruthy(conditionResult)) {
            final VariableScope thenVariableScope = new VariableScope(previous);
            adapter.enterScope(thenVariableScope);
            adapter.execute(node.thenBranch(), thenVariableScope);
        }
        else if (node.elseBranch() != null) {
            final VariableScope elseVariableScope = new VariableScope(previous);
            adapter.enterScope(elseVariableScope);
            adapter.execute(node.elseBranch(), elseVariableScope);
        }
        adapter.enterScope(previous);
        return null;
    }
}
