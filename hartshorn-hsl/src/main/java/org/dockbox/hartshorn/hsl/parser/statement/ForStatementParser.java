package org.dockbox.hartshorn.hsl.parser.statement;

import org.dockbox.hartshorn.hsl.ScriptEvaluationError;
import org.dockbox.hartshorn.hsl.ast.expression.Expression;
import org.dockbox.hartshorn.hsl.ast.statement.BlockStatement;
import org.dockbox.hartshorn.hsl.ast.statement.BodyStatement;
import org.dockbox.hartshorn.hsl.ast.statement.ForEachStatement;
import org.dockbox.hartshorn.hsl.ast.statement.ForStatement;
import org.dockbox.hartshorn.hsl.ast.statement.Statement;
import org.dockbox.hartshorn.hsl.ast.statement.VariableStatement;
import org.dockbox.hartshorn.hsl.parser.TokenParser;
import org.dockbox.hartshorn.hsl.parser.TokenStepValidator;
import org.dockbox.hartshorn.hsl.runtime.Phase;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.TokenType;
import org.dockbox.hartshorn.util.option.Option;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class ForStatementParser extends AbstractBodyStatementParser<BodyStatement> {

    @Override
    public Option<BodyStatement> parse(final TokenParser parser, final TokenStepValidator validator) {
        if (parser.check(TokenType.FOR)) {
            final Token forToken = parser.advance();
            validator.expectAfter(TokenType.LEFT_PAREN, TokenType.FOR);

            validator.expect(TokenType.VAR);
            final VariableStatement initializer = parser.firstCompatibleParser(VariableStatement.class)
                    .flatMap(nodeParser -> nodeParser.parse(parser, validator))
                    .orElseThrow(() -> new ScriptEvaluationError("Expected variable statement in for-each loop", Phase.PARSING, forToken));

            if (parser.match(TokenType.IN)) {
                return this.parseForEachStatement(forToken, parser, validator, initializer);

            } else {
                return this.parseForStatement(forToken, parser, validator, initializer);
            }
        }
        return Option.empty();
    }

    @Override
    public Set<Class<? extends BodyStatement>> types() {
        return Set.of(ForStatement.class, ForEachStatement.class);
    }

    @NotNull
    private Option<BodyStatement> parseForStatement(final Token forToken, final TokenParser parser, final TokenStepValidator validator, final VariableStatement initializer) {
        validator.expectAfter(TokenType.SEMICOLON, "for assignment");

        final Expression condition = parser.expression();
        validator.expectAfter(TokenType.SEMICOLON, "for condition");

        final Statement increment = parser.expressionStatement();
        validator.expectAfter(TokenType.RIGHT_PAREN, "for increment");

        final BlockStatement loopBody = this.blockStatement("for", forToken, parser, validator);
        return Option.of(new ForStatement(initializer, condition, increment, loopBody));
    }

    @NotNull
    private Option<BodyStatement> parseForEachStatement(final Token forToken, final TokenParser parser, final TokenStepValidator validator, final VariableStatement initializer) {
        final Expression collection = parser.expression();
        validator.expectAfter(TokenType.RIGHT_PAREN, "for collection");

        final BlockStatement loopBody = this.blockStatement("for", forToken, parser, validator);
        return Option.of(new ForEachStatement(initializer, collection, loopBody));
    }
}
