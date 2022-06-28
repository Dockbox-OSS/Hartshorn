package org.dockbox.hartshorn.hsl.ast.expression;

import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.visitors.ExpressionVisitor;

public class SuperExpression extends Expression {

    private final Token keyword;
    private final Token method;

    public SuperExpression(final Token keyword, final Token method) {
        super(keyword);
        this.keyword = keyword;
        this.method = method;
    }

    public Token keyword() {
        return this.keyword;
    }

    public Token method() {
        return this.method;
    }

    @Override
    public <R> R accept(final ExpressionVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
