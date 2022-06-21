package org.dockbox.hartshorn.hsl.ast;

import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.visitors.ExpressionVisitor;

public class ArrayVariable extends Expression {

    private final Token name;
    private final Expression index;

    public ArrayVariable(final Token name, final Expression index) {
        this.name = name;
        this.index = index;
    }

    public Token getName() {
        return this.name;
    }

    public Expression getIndex() {
        return this.index;
    }

    @Override
    public <R> R accept(final ExpressionVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
