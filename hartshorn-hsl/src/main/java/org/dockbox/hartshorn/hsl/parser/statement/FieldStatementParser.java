package org.dockbox.hartshorn.hsl.parser.statement;

import org.dockbox.hartshorn.hsl.ast.expression.Expression;
import org.dockbox.hartshorn.hsl.ast.statement.FieldStatement;
import org.dockbox.hartshorn.hsl.ast.statement.VariableStatement;
import org.dockbox.hartshorn.hsl.parser.ASTNodeParser;
import org.dockbox.hartshorn.hsl.parser.TokenParser;
import org.dockbox.hartshorn.hsl.parser.TokenStepValidator;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.TokenType;
import org.dockbox.hartshorn.util.option.Option;

import java.util.Set;

public class FieldStatementParser implements ASTNodeParser<FieldStatement> {

    @Override
    public Option<FieldStatement> parse(final TokenParser parser, final TokenStepValidator validator) {
        if (parser.check(TokenType.PRIVATE, TokenType.PUBLIC)) {
            final Token modifier = parser.find(TokenType.PRIVATE, TokenType.PUBLIC);
            final boolean isFinal = parser.match(TokenType.FINAL);

            final Token name = validator.expect(TokenType.IDENTIFIER, "variable name");

            Expression initializer = null;
            if (parser.match(TokenType.EQUAL)) {
                initializer = parser.expression();
            }

            validator.expectAfter(TokenType.SEMICOLON, "variable declaration");
            final VariableStatement variable = new VariableStatement(name, initializer);

            return Option.of(new FieldStatement(modifier, variable.name(), variable.initializer(), isFinal));
        }
        return Option.empty();
    }

    @Override
    public Set<Class<? extends FieldStatement>> types() {
        return Set.of(FieldStatement.class);
    }
}
