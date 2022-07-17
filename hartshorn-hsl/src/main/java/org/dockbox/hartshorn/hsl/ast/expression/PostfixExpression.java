package org.dockbox.hartshorn.hsl.ast.expression;

import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.visitors.ExpressionVisitor;

public class PostfixExpression extends Expression {

    private final Token operator;
    private final Expression leftExpr;

    public PostfixExpression(final Token operator, final Expression leftExpr) {
        super(operator);
        this.operator = operator;
        this.leftExpr = leftExpr;
    }

    public Token operator() {
        return this.operator;
    }

    public Expression leftExpression() {
        return this.leftExpr;
    }

    @Override
    public <R> R accept(final ExpressionVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
