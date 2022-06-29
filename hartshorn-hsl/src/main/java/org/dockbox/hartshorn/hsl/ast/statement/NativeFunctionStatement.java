package org.dockbox.hartshorn.hsl.ast.statement;

import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.visitors.StatementVisitor;
import org.dockbox.hartshorn.util.reflect.MethodContext;

import java.util.List;

public class NativeFunctionStatement extends Function {

    private final Token name;
    private final Token moduleName;
    private final MethodContext<?, ?> method;
    private final List<Token> params;

    public NativeFunctionStatement(final Token name, final Token moduleName, final MethodContext<?, ?> method, final List<Token> params) {
        super(name);
        this.name = name;
        this.moduleName = moduleName;
        this.method = method;
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

    public MethodContext<?, ?> method() {
        return this.method;
    }

    @Override
    public <R> R accept(final StatementVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
