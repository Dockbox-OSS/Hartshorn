package org.dockbox.hartshorn.hsl.interpreter;

import org.dockbox.hartshorn.hsl.visitors.ExpressionVisitor;
import org.dockbox.hartshorn.hsl.visitors.StatementVisitor;

public interface InterpreterVisitor extends ExpressionVisitor<Object>, StatementVisitor<Void> {

    Interpreter interpreter();

}
