package org.dockbox.hartshorn.hsl.parser.statement;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.dockbox.hartshorn.hsl.ast.statement.BlockStatement;
import org.dockbox.hartshorn.hsl.ast.statement.Statement;
import org.dockbox.hartshorn.hsl.parser.ASTNodeParser;
import org.dockbox.hartshorn.hsl.parser.TokenParser;
import org.dockbox.hartshorn.hsl.parser.TokenStepValidator;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.TokenType;
import org.dockbox.hartshorn.util.option.Option;

public class BlockStatementParser implements ASTNodeParser<BlockStatement> {

    @Override
    public Option<BlockStatement> parse(final TokenParser parser, final TokenStepValidator validator) {
        final Token start = parser.peek();
        validator.expectBefore(TokenType.LEFT_BRACE, "block");

        final List<Statement> statements = new ArrayList<>();
        while (!parser.check(TokenType.RIGHT_BRACE) && !parser.isAtEnd()) {
            statements.add(parser.statement());
        }
        validator.expectAfter(TokenType.RIGHT_BRACE, "block");

        return Option.of(new BlockStatement(start, statements));
    }

    @Override
    public Set<Class<? extends BlockStatement>> types() {
        return Set.of(BlockStatement.class);
    }
}
