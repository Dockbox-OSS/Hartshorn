package org.dockbox.hartshorn.hsl.ast.statement;

import org.dockbox.hartshorn.hsl.ast.expression.Expression;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.visitors.StatementVisitor;

public class ReturnStatement extends Statement {

    private final Token keyword;
    private final Expression value;

    public ReturnStatement(final Token keyword, final Expression value) {
        this.keyword = keyword;
        this.value = value;
    }

    public Token keyword() {
        return this.keyword;
    }

    public Expression value() {
        return this.value;
    }

    @Override
    public <R> R accept(final StatementVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
