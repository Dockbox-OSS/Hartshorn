package org.dockbox.hartshorn.hsl.ast;

import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.visitors.ExpressionVisitor;

public class SuperExp extends Expression {

    private final Token keyword;
    private final Token method;

    public SuperExp(final Token keyword, final Token method) {
        this.keyword = keyword;
        this.method = method;
    }

    public Token getKeyword() {
        return this.keyword;
    }

    public Token getMethod() {
        return this.method;
    }

    @Override
    public <R> R accept(final ExpressionVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
