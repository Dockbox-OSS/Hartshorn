package org.dockbox.hartshorn.hsl.parser.statement;

import org.dockbox.hartshorn.hsl.ScriptEvaluationError;
import org.dockbox.hartshorn.hsl.ast.expression.Expression;
import org.dockbox.hartshorn.hsl.ast.statement.BlockStatement;
import org.dockbox.hartshorn.hsl.ast.statement.IfStatement;
import org.dockbox.hartshorn.hsl.parser.ASTNodeParser;
import org.dockbox.hartshorn.hsl.parser.TokenParser;
import org.dockbox.hartshorn.hsl.parser.TokenStepValidator;
import org.dockbox.hartshorn.hsl.runtime.Phase;
import org.dockbox.hartshorn.hsl.token.TokenType;
import org.dockbox.hartshorn.inject.binding.Bound;
import org.dockbox.hartshorn.util.Result;

import java.util.Set;

import jakarta.inject.Inject;

public class IfStatementParser implements ASTNodeParser<IfStatement> {

    private final BlockStatementParser blockStatementParser;

    @Inject
    @Bound
    public IfStatementParser(final BlockStatementParser blockStatementParser) {
        this.blockStatementParser = blockStatementParser;
    }

    @Override
    public Result<IfStatement> parse(final TokenParser parser, final TokenStepValidator validator) {
        if (parser.match(TokenType.IF)) {
            validator.expectAfter(TokenType.LEFT_PAREN, TokenType.IF);
            final Expression condition = parser.expression();
            validator.expectAfter(TokenType.RIGHT_PAREN, "if condition");
            final BlockStatement thenBlock = this.blockStatementParser.parse(parser, validator)
                    .orThrow(() -> new ScriptEvaluationError("Expected block after if statement", Phase.PARSING, condition));
            BlockStatement elseBlock = null;
            if (parser.match(TokenType.ELSE)) {
                elseBlock = this.blockStatementParser.parse(parser, validator)
                        .orThrow(() -> new ScriptEvaluationError("Expected block after else statement", Phase.PARSING, condition));
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
