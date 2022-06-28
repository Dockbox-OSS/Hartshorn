package org.dockbox.hartshorn.hsl.ast.statement;

import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.visitors.StatementVisitor;

public class ContinueStatement extends Statement {

    private final Token keyword;

    public ContinueStatement(final Token keyword) {
        super(keyword);
        this.keyword = keyword;
    }

    public Token keyword() {
        return this.keyword;
    }

    @Override
    public <R> R accept(final StatementVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
