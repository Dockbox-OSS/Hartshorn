package org.dockbox.hartshorn.hsl.parser.statement;

import org.dockbox.hartshorn.hsl.ast.expression.Expression;
import org.dockbox.hartshorn.hsl.ast.statement.VariableStatement;
import org.dockbox.hartshorn.hsl.parser.ASTNodeParser;
import org.dockbox.hartshorn.hsl.parser.TokenParser;
import org.dockbox.hartshorn.hsl.parser.TokenStepValidator;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.TokenType;
import org.dockbox.hartshorn.util.Result;

import java.util.Set;

public class VariableDeclarationParser implements ASTNodeParser<VariableStatement> {

    @Override
    public Result<VariableStatement> parse(final TokenParser parser, final TokenStepValidator validator) {
        if (parser.match(TokenType.VAR)) {
            final Token name = validator.expect(TokenType.IDENTIFIER, "variable name");

            Expression initializer = null;
            if (parser.match(TokenType.EQUAL)) {
                initializer = parser.expression();
            }

            validator.expectAfter(TokenType.SEMICOLON, "variable declaration");
            return Result.of(new VariableStatement(name, initializer));
        }
        return Result.empty();
    }

    @Override
    public Set<Class<? extends VariableStatement>> types() {
        return Set.of(VariableStatement.class);
    }
}
