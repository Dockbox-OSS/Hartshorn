package org.dockbox.hartshorn.hsl.ast;

import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.visitors.StatementVisitor;

public class ExtensionStatement extends Function {

    private final Token className;
    private final FunctionStatement functionStatement;

    public ExtensionStatement(final Token className, final FunctionStatement function) {
        this.className = className;
        this.functionStatement = function;
    }

    public Token getClassName() {
        return this.className;
    }

    public FunctionStatement getFunctionStatement() {
        return this.functionStatement;
    }

    @Override
    public <R> R accept(final StatementVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
