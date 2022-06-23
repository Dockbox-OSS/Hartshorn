package org.dockbox.hartshorn.hsl.ast.statement;

import org.dockbox.hartshorn.hsl.ast.expression.Expression;
import org.dockbox.hartshorn.hsl.visitors.StatementVisitor;

public class ExpressionStatement extends Statement {

    private final Expression expression;

    public ExpressionStatement(final Expression expression) {
        this.expression = expression;
    }

    public Expression expression() {
        return this.expression;
    }

    @Override
    public <R> R accept(final StatementVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
