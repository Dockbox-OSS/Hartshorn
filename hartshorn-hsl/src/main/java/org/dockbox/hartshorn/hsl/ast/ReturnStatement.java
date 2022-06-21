package org.dockbox.hartshorn.hsl.ast;

import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.visitors.StatementVisitor;

public class ReturnStatement extends Statement {

    private final Token keyword;
    private final Expression value;

    public ReturnStatement(final Token keyword, final Expression value) {
        this.keyword = keyword;
        this.value = value;
    }

    public Token getKeyword() {
        return this.keyword;
    }

    public Expression getValue() {
        return this.value;
    }

    @Override
    public <R> R accept(final StatementVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
