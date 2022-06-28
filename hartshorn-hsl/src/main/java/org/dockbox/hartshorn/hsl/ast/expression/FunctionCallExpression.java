package org.dockbox.hartshorn.hsl.ast.expression;

import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.visitors.ExpressionVisitor;

import java.util.List;

public class FunctionCallExpression extends Expression {

    private final Expression callee;
    private final Token closingParenthesis;
    private final List<Expression> arguments;

    public FunctionCallExpression(final Expression callee, final Token paren, final List<Expression> arguments) {
        super(callee.line());
        this.callee = callee;
        this.closingParenthesis = paren;
        this.arguments = arguments;
    }

    public Expression callee() {
        return this.callee;
    }

    public Token closingParenthesis() {
        return this.closingParenthesis;
    }

    public List<Expression> arguments() {
        return this.arguments;
    }

    @Override
    public <R> R accept(final ExpressionVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
