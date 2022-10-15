package org.dockbox.hartshorn.hsl.parser.statement;

import org.dockbox.hartshorn.hsl.ast.statement.NativeFunctionStatement;
import org.dockbox.hartshorn.hsl.ast.statement.ParametricExecutableStatement.Parameter;
import org.dockbox.hartshorn.hsl.parser.TokenParser;
import org.dockbox.hartshorn.hsl.parser.TokenStepValidator;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.TokenType;
import org.dockbox.hartshorn.util.Result;

import java.util.List;
import java.util.Set;

public class NativeFunctionStatementParser extends AbstractBodyStatementParser<NativeFunctionStatement> implements ParametricStatementParser {

    @Override
    public Result<NativeFunctionStatement> parse(final TokenParser parser, final TokenStepValidator validator) {
        if (parser.match(TokenType.NATIVE) && parser.match(TokenType.FUN)) {
            final Token moduleName = validator.expect(TokenType.IDENTIFIER, "module name");

            while (parser.match(TokenType.COLON)) {
                final Token token = new Token(TokenType.DOT, ".", moduleName.line(), moduleName.column());
                moduleName.concat(token);
                final Token submodule = validator.expect(TokenType.IDENTIFIER, "module name");
                moduleName.concat(submodule);
            }

            validator.expectBefore(TokenType.DOT, "method body");
            final Token funcName = validator.expect(TokenType.IDENTIFIER, "function name");
            final List<Parameter> parameters = ParametricStatementParser.super.parameters(parser, validator, "method name", Integer.MAX_VALUE, TokenType.NATIVE);

            validator.expectAfter(TokenType.SEMICOLON, "value");
            return Result.of(new NativeFunctionStatement(funcName, moduleName, null, parameters));
        }
        return Result.empty();
    }

    @Override
    public Set<Class<? extends NativeFunctionStatement>> types() {
        return Set.of(NativeFunctionStatement.class);
    }
}
