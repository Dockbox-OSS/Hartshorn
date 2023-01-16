package org.dockbox.hartshorn.hsl.interpreter;

import org.dockbox.hartshorn.hsl.ast.expression.Expression;
import org.dockbox.hartshorn.hsl.ast.statement.BlockStatement;
import org.dockbox.hartshorn.hsl.ast.statement.Statement;
import org.dockbox.hartshorn.hsl.token.Token;

import java.util.List;

public interface InterpreterAdapter {

    Object evaluate(final Expression expr);

    void execute(final Statement stmt);

    void execute(final BlockStatement blockStatement, final VariableScope localVariableScope);

    void execute(final List<Statement> statementList, final VariableScope localVariableScope);

    Object lookUpVariable(final Token name, final Expression expr);

    void resolve(final Expression expr, final int depth);

    VariableScope visitingScope();

    VariableScope global();

    void withNextScope(final Runnable runnable);

    void enterScope(VariableScope scope);

    Integer distance(final Expression expr);

    Interpreter interpreter();
}
