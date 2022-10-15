package org.dockbox.hartshorn.hsl.parser.statement;

import org.dockbox.hartshorn.hsl.ScriptEvaluationError;
import org.dockbox.hartshorn.hsl.ast.statement.FieldStatement;
import org.dockbox.hartshorn.hsl.ast.statement.VariableStatement;
import org.dockbox.hartshorn.hsl.parser.ASTNodeParser;
import org.dockbox.hartshorn.hsl.parser.TokenParser;
import org.dockbox.hartshorn.hsl.parser.TokenStepValidator;
import org.dockbox.hartshorn.hsl.runtime.Phase;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.TokenType;
import org.dockbox.hartshorn.util.Result;

import java.util.Set;

public class FieldStatementParser implements ASTNodeParser<FieldStatement> {

    @Override
    public Result<FieldStatement> parse(final TokenParser parser, final TokenStepValidator validator) {
        final Token modifier = parser.find(TokenType.PRIVATE, TokenType.PUBLIC);
        final boolean isFinal = parser.match(TokenType.FINAL);
        final VariableStatement variable = parser.firstCompatibleParser(VariableStatement.class)
                .flatMap(fieldParser -> fieldParser.parse(parser, validator))
                .orThrow(() -> new ScriptEvaluationError("Expected valid variable declaration after field modifiers", Phase.PARSING, modifier));
        return Result.of(new FieldStatement(modifier, variable.name(), variable.initializer(), isFinal));
    }

    @Override
    public Set<Class<? extends FieldStatement>> types() {
        return Set.of(FieldStatement.class);
    }
}
