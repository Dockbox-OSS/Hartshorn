package org.dockbox.hartshorn.hsl.ast.statement;

import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.visitors.StatementVisitor;

import java.util.List;

public class NativeFunctionStatement extends Function {

    private final Token name;
    private final Token moduleName;
    private final List<Token> params;

    public NativeFunctionStatement(final Token name, final Token moduleName, final List<Token> params) {
        super(name);
        this.name = name;
        this.moduleName = moduleName;
        this.params = params;
    }

    public Token name() {
        return this.name;
    }

    public Token moduleName() {
        return this.moduleName;
    }

    public List<Token> params() {
        return this.params;
    }

    @Override
    public <R> R accept(final StatementVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
