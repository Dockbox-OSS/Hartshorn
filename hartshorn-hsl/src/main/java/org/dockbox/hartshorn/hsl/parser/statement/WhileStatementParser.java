package org.dockbox.hartshorn.hsl.parser.statement;

import org.dockbox.hartshorn.hsl.ast.expression.Expression;
import org.dockbox.hartshorn.hsl.ast.statement.BlockStatement;
import org.dockbox.hartshorn.hsl.ast.statement.WhileStatement;
import org.dockbox.hartshorn.hsl.parser.TokenParser;
import org.dockbox.hartshorn.hsl.parser.TokenStepValidator;
import org.dockbox.hartshorn.hsl.token.TokenType;
import org.dockbox.hartshorn.inject.binding.Bound;
import org.dockbox.hartshorn.util.Result;

import java.util.Set;

import jakarta.inject.Inject;

public class WhileStatementParser extends AbstractBodyStatementParser<WhileStatement> {

    @Override
    public Result<WhileStatement> parse(final TokenParser parser, final TokenStepValidator validator) {
        if (parser.match(TokenType.WHILE)) {
            validator.expectAfter(TokenType.LEFT_PAREN, TokenType.WHILE);
            final Expression condition = parser.expression();
            validator.expectAfter(TokenType.RIGHT_PAREN, "while condition");
            final BlockStatement loopBody = this.blockStatement("while", condition, parser, validator);
            return Result.of(new WhileStatement(condition, loopBody));
        }
        return Result.empty();
    }

    @Override
    public Set<Class<? extends WhileStatement>> types() {
        return Set.of(WhileStatement.class);
    }
}
