package org.dockbox.hartshorn.hsl.ast;

import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.visitors.ExpressionVisitor;

public class GetExp extends Expression {

    private final Token name;
    private final Expression object;

    public GetExp(final Token name, final Expression object) {
        this.name = name;
        this.object = object;
    }

    public Token getName() {
        return this.name;
    }

    public Expression getObject() {
        return this.object;
    }

    @Override
    public <R> R accept(final ExpressionVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
