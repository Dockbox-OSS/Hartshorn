package org.dockbox.hartshorn.hsl.parser.statement;

import org.dockbox.hartshorn.hsl.ast.expression.Expression;
import org.dockbox.hartshorn.hsl.ast.statement.BlockStatement;
import org.dockbox.hartshorn.hsl.ast.statement.WhileStatement;
import org.dockbox.hartshorn.hsl.parser.TokenParser;
import org.dockbox.hartshorn.hsl.parser.TokenStepValidator;
import org.dockbox.hartshorn.hsl.token.TokenType;
import org.dockbox.hartshorn.util.option.Option;

import java.util.Set;

public class WhileStatementParser extends AbstractBodyStatementParser<WhileStatement> {

    @Override
    public Option<WhileStatement> parse(final TokenParser parser, final TokenStepValidator validator) {
        if (parser.match(TokenType.WHILE)) {
            validator.expectAfter(TokenType.LEFT_PAREN, TokenType.WHILE);
            final Expression condition = parser.expression();
            validator.expectAfter(TokenType.RIGHT_PAREN, "while condition");
            final BlockStatement loopBody = this.blockStatement("while", condition, parser, validator);
            return Option.of(new WhileStatement(condition, loopBody));
        }
        return Option.empty();
    }

    @Override
    public Set<Class<? extends WhileStatement>> types() {
        return Set.of(WhileStatement.class);
    }
}
