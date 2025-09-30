package org.dockbox.hartshorn.hsl.interpreter.statement;

import org.dockbox.hartshorn.hsl.ast.statement.Statement;
import org.dockbox.hartshorn.hsl.interpreter.ASTNodeInterpreter;

public interface StatementInterpreter<T extends Statement> extends ASTNodeInterpreter<Void, T> {
}
