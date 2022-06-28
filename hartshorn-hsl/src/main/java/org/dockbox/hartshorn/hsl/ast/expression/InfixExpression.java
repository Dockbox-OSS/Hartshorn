package org.dockbox.hartshorn.hsl.ast.expression;

import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.visitors.ExpressionVisitor;

public class InfixExpression extends Expression {

    private final Expression leftExp;
    private final Token infixOperator;
    private final Expression rightExp;

    public InfixExpression(final Expression leftExp, final Token infixOperator, final Expression rightExp) {
        super(infixOperator);
        this.leftExp = leftExp;
        this.infixOperator = infixOperator;
        this.rightExp = rightExp;
    }

    public Expression leftExpression() {
        return this.leftExp;
    }

    public Token infixOperatorName() {
        return this.infixOperator;
    }

    public Expression rightExpression() {
        return this.rightExp;
    }

    @Override
    public <R> R accept(final ExpressionVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
