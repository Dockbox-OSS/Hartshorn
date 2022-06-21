package org.dockbox.hartshorn.hsl.ast;

import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.visitors.ExpressionVisitor;

public class AssignExp extends Expression {

    private final Token name;
    private final Expression value;

    public AssignExp(final Token name, final Expression value) {
        this.name = name;
        this.value = value;
    }

    public Token getName() {
        return this.name;
    }

    public Expression getValue() {
        return this.value;
    }

    @Override
    public <R> R accept(final ExpressionVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
