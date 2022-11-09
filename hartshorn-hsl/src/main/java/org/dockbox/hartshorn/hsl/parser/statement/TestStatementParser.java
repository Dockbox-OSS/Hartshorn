package org.dockbox.hartshorn.hsl.parser.statement;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.dockbox.hartshorn.hsl.ast.statement.BlockStatement;
import org.dockbox.hartshorn.hsl.ast.statement.Statement;
import org.dockbox.hartshorn.hsl.ast.statement.TestStatement;
import org.dockbox.hartshorn.hsl.parser.TokenParser;
import org.dockbox.hartshorn.hsl.parser.TokenStepValidator;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.TokenType;
import org.dockbox.hartshorn.util.option.Option;

public class TestStatementParser extends AbstractBodyStatementParser<TestStatement> {

    @Override
    public Option<TestStatement> parse(final TokenParser parser, final TokenStepValidator validator) {
        if (parser.match(TokenType.TEST)) {
            validator.expectAfter(TokenType.LEFT_PAREN, "test statement");

            final Token name = validator.expect(TokenType.STRING, "test name");
            validator.expectAfter(TokenType.RIGHT_PAREN, "test statement name value");
            validator.expectBefore(TokenType.LEFT_BRACE, "test body");

            final List<Statement> statements = new ArrayList<>();
            final Token bodyStart = parser.peek();
            while (!parser.match(TokenType.RIGHT_BRACE) && !parser.isAtEnd()) {
                final Statement statement = parser.statement();
                statements.add(statement);
            }

            final BlockStatement body = new BlockStatement(bodyStart, statements);
            return Option.of(new TestStatement(name, body));
        }
        return Option.empty();
    }

    @Override
    public Set<Class<? extends TestStatement>> types() {
        return Set.of(TestStatement.class);
    }
}
