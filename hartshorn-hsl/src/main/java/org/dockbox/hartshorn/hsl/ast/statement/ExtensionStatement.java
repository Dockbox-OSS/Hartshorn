package org.dockbox.hartshorn.hsl.ast.statement;

import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.visitors.StatementVisitor;

public class ExtensionStatement extends Function {

    private final Token className;
    private final FunctionStatement functionStatement;

    public ExtensionStatement(final Token className, final FunctionStatement function) {
        super(className);
        this.className = className;
        this.functionStatement = function;
    }

    public Token className() {
        return this.className;
    }

    public FunctionStatement functionStatement() {
        return this.functionStatement;
    }

    @Override
    public <R> R accept(final StatementVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
