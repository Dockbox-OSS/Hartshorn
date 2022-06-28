package org.dockbox.hartshorn.hsl.ast.expression;

import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.visitors.ExpressionVisitor;

public class GetExpression extends Expression {

    private final Token name;
    private final Expression object;

    public GetExpression(final Token name, final Expression object) {
        super(name);
        this.name = name;
        this.object = object;
    }

    public Token name() {
        return this.name;
    }

    public Expression object() {
        return this.object;
    }

    @Override
    public <R> R accept(final ExpressionVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
