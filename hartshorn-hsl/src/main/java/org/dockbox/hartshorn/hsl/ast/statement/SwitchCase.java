package org.dockbox.hartshorn.hsl.ast.statement;

import org.dockbox.hartshorn.hsl.ast.expression.LiteralExpression;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.visitors.StatementVisitor;

public class SwitchCase extends Statement {

    private final Statement body;
    private final LiteralExpression expression;
    private final boolean isDefault;

    public SwitchCase(final Token caseToken, Statement body, LiteralExpression expression, boolean isDefault) {
        super(caseToken);
        this.body = body;
        this.expression = expression;
        this.isDefault = isDefault;
    }

    public Statement body() {
        return body;
    }

    public LiteralExpression expression() {
        return expression;
    }

    public boolean isDefault() {
        return isDefault;
    }

    @Override
    public <R> R accept(StatementVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
