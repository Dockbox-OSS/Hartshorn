package org.dockbox.hartshorn.hsl.parser.statement;

import org.dockbox.hartshorn.hsl.ast.expression.Expression;
import org.dockbox.hartshorn.hsl.ast.statement.BlockStatement;
import org.dockbox.hartshorn.hsl.ast.statement.DoWhileStatement;
import org.dockbox.hartshorn.hsl.parser.TokenParser;
import org.dockbox.hartshorn.hsl.parser.TokenStepValidator;
import org.dockbox.hartshorn.hsl.token.TokenType;
import org.dockbox.hartshorn.util.Result;

import java.util.Set;

public class DoWhileStatementParser extends AbstractBodyStatementParser<DoWhileStatement> {

    @Override
    public Result<DoWhileStatement> parse(final TokenParser parser, final TokenStepValidator validator) {
        if (parser.match(TokenType.DO)) {
            final BlockStatement loopBody = this.blockStatement("do", parser.previous(), parser, validator);
            validator.expect(TokenType.WHILE);
            validator.expectAfter(TokenType.LEFT_PAREN, TokenType.WHILE);
            final Expression condition = parser.expression();
            validator.expectAfter(TokenType.RIGHT_PAREN, "do while condition");
            validator.expectAfter(TokenType.SEMICOLON, "do while condition");
            return Result.of(new DoWhileStatement(condition, loopBody));
        }
        return Result.empty();
    }

    @Override
    public Set<Class<? extends DoWhileStatement>> types() {
        return Set.of(DoWhileStatement.class);
    }
}
