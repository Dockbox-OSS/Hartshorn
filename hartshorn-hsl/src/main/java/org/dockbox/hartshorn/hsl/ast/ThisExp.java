package org.dockbox.hartshorn.hsl.ast;

import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.visitors.ExpressionVisitor;

public class ThisExp extends Expression {

    private final Token keyword;

    public ThisExp(final Token keyword) {
        this.keyword = keyword;
    }

    public Token getKeyword() {
        return this.keyword;
    }

    @Override
    public <R> R accept(final ExpressionVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
