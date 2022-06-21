package org.dockbox.hartshorn.hsl.ast;

import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.visitors.StatementVisitor;

import java.util.List;

public class FunctionStatement extends Function {

    private final Token name;
    private final List<Token> params;
    private final List<Statement> funcBody;

    public FunctionStatement(final Token name,
                             final List<Token> params,
                             final List<Statement> funcBody) {
        this.name = name;
        this.params = params;
        this.funcBody = funcBody;
    }

    public Token getName() {
        return this.name;
    }

    public List<Token> getParams() {
        return this.params;
    }

    public List<Statement> getFunctionBody() {
        return this.funcBody;
    }

    @Override
    public <R> R accept(final StatementVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
