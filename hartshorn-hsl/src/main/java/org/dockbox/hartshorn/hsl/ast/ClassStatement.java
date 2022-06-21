package org.dockbox.hartshorn.hsl.ast;

import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.visitors.StatementVisitor;

import java.util.List;

public class ClassStatement extends Statement {

    private final Token name;
    private final Variable superClass;
    private final List<FunctionStatement> methods;

    public ClassStatement(final Token name, final Variable superClass, final List<FunctionStatement> methods) {
        this.name = name;
        this.superClass = superClass;
        this.methods = methods;
    }

    public Token getName() {
        return this.name;
    }

    public Variable getSuperClass() {
        return this.superClass;
    }

    public List<FunctionStatement> getMethods() {
        return this.methods;
    }

    @Override
    public <R> R accept(final StatementVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
