package org.dockbox.hartshorn.hsl.ast;

import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.visitors.ExpressionVisitor;

public class ArraySetExp extends Expression {

    private final Token name;
    private final Expression index;
    private final Expression value;

    public ArraySetExp(final Token name, final Expression index, final Expression value) {
        this.name = name;
        this.index = index;
        this.value = value;
    }

    public Token getName() {
        return this.name;
    }

    public Expression getIndex() {
        return this.index;
    }

    public Expression getValue() {
        return this.value;
    }

    @Override
    public <R> R accept(final ExpressionVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
