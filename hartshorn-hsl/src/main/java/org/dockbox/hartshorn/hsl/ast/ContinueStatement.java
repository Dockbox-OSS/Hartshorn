package org.dockbox.hartshorn.hsl.ast;

import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.visitors.StatementVisitor;

public class ContinueStatement extends Statement {

    private final Token keyword;

    public ContinueStatement(final Token keyword) {
        this.keyword = keyword;
    }

    public Token getKeyword() {
        return this.keyword;
    }

    @Override
    public <R> R accept(final StatementVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
