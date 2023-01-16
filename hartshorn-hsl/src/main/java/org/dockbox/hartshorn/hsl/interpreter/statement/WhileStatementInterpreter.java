package org.dockbox.hartshorn.hsl.interpreter.statement;

import org.dockbox.hartshorn.hsl.ast.MoveKeyword;
import org.dockbox.hartshorn.hsl.ast.statement.WhileStatement;
import org.dockbox.hartshorn.hsl.interpreter.ASTNodeInterpreter;
import org.dockbox.hartshorn.hsl.interpreter.InterpreterAdapter;
import org.dockbox.hartshorn.hsl.interpreter.InterpreterUtilities;

public class WhileStatementInterpreter implements ASTNodeInterpreter<Void, WhileStatement> {

    @Override
    public Void interpret(final WhileStatement node, final InterpreterAdapter adapter) {
        while (InterpreterUtilities.isTruthy(adapter.evaluate(node.condition()))) {
            try {
                adapter.execute(node.body());
            }
            catch (final MoveKeyword moveKeyword) {
                if (moveKeyword.moveType() == MoveKeyword.MoveType.BREAK) {
                    break;
                }
            }
        }
        return null;
    }
}
