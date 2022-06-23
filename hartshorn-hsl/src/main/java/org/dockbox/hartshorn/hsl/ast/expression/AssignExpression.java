package org.dockbox.hartshorn.hsl.ast.expression;

import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.visitors.ExpressionVisitor;

public class AssignExpression extends Expression {

    private final Token name;
    private final Expression value;

    public AssignExpression(final Token name, final Expression value) {
        this.name = name;
        this.value = value;
    }

    public Token name() {
        return this.name;
    }

    public Expression value() {
        return this.value;
    }

    @Override
    public <R> R accept(final ExpressionVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
