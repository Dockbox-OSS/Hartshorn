package org.dockbox.hartshorn.hsl.ast.statement;

import org.dockbox.hartshorn.hsl.ast.expression.Expression;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.visitors.StatementVisitor;

import java.util.List;

public class SwitchStatement extends Statement {

    private final Expression expression;
    private final List<SwitchCase> cases;
    private final SwitchCase defaultCase;

    public SwitchStatement(Token switchToken, Expression expression, List<SwitchCase> cases, SwitchCase defaultCase) {
        super(switchToken);
        this.expression = expression;
        this.cases = cases;
        this.defaultCase = defaultCase;
    }

    public Expression expression() {
        return expression;
    }

    public List<SwitchCase> cases() {
        return cases;
    }

    public SwitchCase defaultCase() {
        return defaultCase;
    }

    @Override
    public <R> R accept(StatementVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
