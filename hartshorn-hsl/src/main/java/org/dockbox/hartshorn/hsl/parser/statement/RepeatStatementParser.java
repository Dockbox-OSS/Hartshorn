package org.dockbox.hartshorn.hsl.parser.statement;

import org.dockbox.hartshorn.hsl.ast.expression.Expression;
import org.dockbox.hartshorn.hsl.ast.statement.BlockStatement;
import org.dockbox.hartshorn.hsl.ast.statement.RepeatStatement;
import org.dockbox.hartshorn.hsl.parser.TokenParser;
import org.dockbox.hartshorn.hsl.parser.TokenStepValidator;
import org.dockbox.hartshorn.hsl.token.TokenType;
import org.dockbox.hartshorn.util.option.Option;

import java.util.Set;

public class RepeatStatementParser extends AbstractBodyStatementParser<RepeatStatement> {

    @Override
    public Option<RepeatStatement> parse(final TokenParser parser, final TokenStepValidator validator) {
        if (parser.match(TokenType.REPEAT)) {
            validator.expectAfter(TokenType.LEFT_PAREN, "repeat");
            final Expression value = parser.expression();
            validator.expectAfter(TokenType.RIGHT_PAREN, "repeat value");
            final BlockStatement loopBody = this.blockStatement("repeat", value, parser, validator);
            return Option.of(new RepeatStatement(value, loopBody));
        }
        return Option.empty();
    }

    @Override
    public Set<Class<? extends RepeatStatement>> types() {
        return Set.of(RepeatStatement.class);
    }
}
