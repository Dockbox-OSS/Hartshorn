package org.dockbox.hartshorn.hsl.interpreter;

import org.dockbox.hartshorn.hsl.ast.ASTNode;
import org.dockbox.hartshorn.hsl.interpreter.InterpreterAdapter;

public interface ASTNodeInterpreter<R, T extends ASTNode> {

     R interpret(T node, InterpreterAdapter adapter);
}
