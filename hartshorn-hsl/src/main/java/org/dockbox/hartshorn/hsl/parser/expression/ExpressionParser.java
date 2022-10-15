package org.dockbox.hartshorn.hsl.parser.expression;

import org.dockbox.hartshorn.hsl.ast.expression.Expression;
import org.dockbox.hartshorn.hsl.parser.ASTNodeParser;

public interface ExpressionParser<T extends Expression> extends ASTNodeParser<T> {
    boolean isValueExpression();
}
