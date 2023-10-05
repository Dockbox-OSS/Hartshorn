package org.dockbox.hartshorn.hsl.runtime;

import org.dockbox.hartshorn.hsl.ast.expression.Expression;
import org.dockbox.hartshorn.hsl.ast.statement.Statement;
import org.dockbox.hartshorn.hsl.parser.ASTNodeParser;

public interface MutableScriptRuntime extends ScriptRuntime {

    void expressionParser(ASTNodeParser<? extends Expression> parser);

    void statementParser(ASTNodeParser<? extends Statement> parser);
}
