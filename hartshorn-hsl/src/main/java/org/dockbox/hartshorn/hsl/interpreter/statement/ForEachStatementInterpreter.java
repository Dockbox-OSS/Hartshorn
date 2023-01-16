package org.dockbox.hartshorn.hsl.interpreter.statement;

import org.dockbox.hartshorn.hsl.ast.statement.ForEachStatement;
import org.dockbox.hartshorn.hsl.interpreter.ASTNodeInterpreter;
import org.dockbox.hartshorn.hsl.interpreter.InterpreterAdapter;
import org.dockbox.hartshorn.hsl.interpreter.InterpreterUtilities;

public class ForEachStatementInterpreter implements ASTNodeInterpreter<Void, ForEachStatement> {

    @Override
    public Void interpret(final ForEachStatement node, final InterpreterAdapter adapter) {
        adapter.withNextScope(() -> {
            Object collection = adapter.evaluate(node.collection());
            collection = InterpreterUtilities.unwrap(collection);

            if (collection instanceof Iterable<?> iterable) {
                adapter.visitingScope().define(node.selector().name().lexeme(), null);
                for (final Object item : iterable) {
                    adapter.visitingScope().assign(node.selector().name(), item);
                    adapter.execute(node.body());
                }
            }
            else {
                throw new RuntimeException("Only iterables are supported for for-each.");
            }
        });
        return null;
    }
}
