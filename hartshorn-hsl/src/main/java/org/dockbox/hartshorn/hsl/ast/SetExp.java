package org.dockbox.hartshorn.hsl.ast;

import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.visitors.ExpressionVisitor;

public class SetExp extends Expression {

    private final Expression object;
    private final Token name;
    private final Expression value;

    public SetExp(final Expression object, final Token name, final Expression value) {
        this.object = object;
        this.name = name;
        this.value = value;
    }

    public Expression getObject() {
        return this.object;
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
