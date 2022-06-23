package org.dockbox.hartshorn.hsl.ast.statement;

import org.dockbox.hartshorn.hsl.ast.expression.Expression;
import org.dockbox.hartshorn.hsl.ast.statement.Statement;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.visitors.StatementVisitor;

public class VariableStatement extends Statement {

    private final Token name;
    private final Expression initializer;

    public VariableStatement(final Token name, final Expression initializer) {
        this.name = name;
        this.initializer = initializer;
    }

    public Token name() {
        return this.name;
    }

    public Expression initializer() {
        return this.initializer;
    }

    @Override
    public <R> R accept(final StatementVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
