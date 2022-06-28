package org.dockbox.hartshorn.hsl.ast.statement;

import org.dockbox.hartshorn.hsl.ast.expression.VariableExpression;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.visitors.StatementVisitor;

import java.util.List;

public class ClassStatement extends Statement {

    private final Token name;
    private final VariableExpression superClass;
    private final List<FunctionStatement> methods;

    public ClassStatement(final Token name, final VariableExpression superClass, final List<FunctionStatement> methods) {
        super(name);
        this.name = name;
        this.superClass = superClass;
        this.methods = methods;
    }

    public Token name() {
        return this.name;
    }

    public VariableExpression superClass() {
        return this.superClass;
    }

    public List<FunctionStatement> methods() {
        return this.methods;
    }

    @Override
    public <R> R accept(final StatementVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
