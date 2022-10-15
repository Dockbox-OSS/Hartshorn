package org.dockbox.hartshorn.hsl.parser.statement;

import org.dockbox.hartshorn.hsl.ast.expression.Expression;
import org.dockbox.hartshorn.hsl.ast.statement.BlockStatement;
import org.dockbox.hartshorn.hsl.ast.statement.IfStatement;
import org.dockbox.hartshorn.hsl.parser.TokenParser;
import org.dockbox.hartshorn.hsl.parser.TokenStepValidator;
import org.dockbox.hartshorn.hsl.token.TokenType;
import org.dockbox.hartshorn.util.Result;

import java.util.Set;

public class IfStatementParser extends AbstractBodyStatementParser<IfStatement> {

    @Override
    public Result<IfStatement> parse(final TokenParser parser, final TokenStepValidator validator) {
        if (parser.match(TokenType.IF)) {
            validator.expectAfter(TokenType.LEFT_PAREN, TokenType.IF);
            final Expression condition = parser.expression();
            validator.expectAfter(TokenType.RIGHT_PAREN, "if condition");
            final BlockStatement thenBlock = this.blockStatement("if", condition, parser, validator);
            BlockStatement elseBlock = null;
            if (parser.match(TokenType.ELSE)) {
                elseBlock = this.blockStatement("else", condition, parser, validator);
            }
            return Result.of(new IfStatement(condition, thenBlock, elseBlock));
        }
        return Result.empty();
    }

    @Override
    public Set<Class<? extends IfStatement>> types() {
        return Set.of(IfStatement.class);
    }
}
