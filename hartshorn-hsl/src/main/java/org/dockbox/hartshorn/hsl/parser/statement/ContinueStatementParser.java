package org.dockbox.hartshorn.hsl.parser.statement;

import org.dockbox.hartshorn.hsl.ast.statement.ContinueStatement;
import org.dockbox.hartshorn.hsl.parser.ASTNodeParser;
import org.dockbox.hartshorn.hsl.parser.TokenParser;
import org.dockbox.hartshorn.hsl.parser.TokenStepValidator;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.TokenType;
import org.dockbox.hartshorn.util.Result;

import java.util.Set;

public class ContinueStatementParser implements ASTNodeParser<ContinueStatement> {

    @Override
    public Result<ContinueStatement> parse(final TokenParser parser, final TokenStepValidator validator) {
        if (parser.match(TokenType.CONTINUE)) {
            final Token keyword = parser.previous();
            validator.expectAfter(TokenType.SEMICOLON, "value");
            return Result.of(new ContinueStatement(keyword));
        }
        return Result.empty();
    }

    @Override
    public Set<Class<? extends ContinueStatement>> types() {
        return Set.of(ContinueStatement.class);
    }
}
