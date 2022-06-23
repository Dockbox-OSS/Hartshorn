package org.dockbox.hartshorn.hsl.ast.expression;

import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.visitors.ExpressionVisitor;

public class ThisExpression extends Expression {

    private final Token keyword;

    public ThisExpression(final Token keyword) {
        this.keyword = keyword;
    }

    public Token keyword() {
        return this.keyword;
    }

    @Override
    public <R> R accept(final ExpressionVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
