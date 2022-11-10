package org.dockbox.hartshorn.hsl.parser.statement;

import org.dockbox.hartshorn.hsl.ast.statement.BreakStatement;
import org.dockbox.hartshorn.hsl.parser.ASTNodeParser;
import org.dockbox.hartshorn.hsl.parser.TokenParser;
import org.dockbox.hartshorn.hsl.parser.TokenStepValidator;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.TokenType;
import org.dockbox.hartshorn.util.option.Option;

import java.util.Set;

public class BreakStatementParser implements ASTNodeParser<BreakStatement> {

    @Override
    public Option<BreakStatement> parse(final TokenParser parser, final TokenStepValidator validator) {
        if (parser.match(TokenType.BREAK)) {
            final Token keyword = parser.previous();
            validator.expectAfter(TokenType.SEMICOLON, "value");
            return Option.of(new BreakStatement(keyword));
        }
        return Option.empty();
    }

    @Override
    public Set<Class<? extends BreakStatement>> types() {
        return Set.of(BreakStatement.class);
    }
}
