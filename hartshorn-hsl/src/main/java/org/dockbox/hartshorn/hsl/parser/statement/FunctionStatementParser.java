package org.dockbox.hartshorn.hsl.parser.statement;

import org.dockbox.hartshorn.hsl.ScriptEvaluationError;
import org.dockbox.hartshorn.hsl.ast.statement.BlockStatement;
import org.dockbox.hartshorn.hsl.ast.statement.ExtensionStatement;
import org.dockbox.hartshorn.hsl.ast.statement.Function;
import org.dockbox.hartshorn.hsl.ast.statement.FunctionStatement;
import org.dockbox.hartshorn.hsl.ast.statement.ParametricExecutableStatement.Parameter;
import org.dockbox.hartshorn.hsl.parser.TokenParser;
import org.dockbox.hartshorn.hsl.parser.TokenStepValidator;
import org.dockbox.hartshorn.hsl.runtime.Phase;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.TokenType;
import org.dockbox.hartshorn.util.Result;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FunctionStatementParser extends AbstractBodyStatementParser<Function> {

    private static final int MAX_NUM_OF_ARGUMENTS = 8;

    private final Set<String> prefixFunctions = new HashSet<>();
    private final Set<String> infixFunctions = new HashSet<>();

    @Override
    public Result<Function> parse(final TokenParser parser, final TokenStepValidator validator) {
        if (parser.check(TokenType.PREFIX, TokenType.INFIX, TokenType.FUN)) {
            final Token functionType = parser.advance();
            final Token functionToken = functionType.type() == TokenType.FUN ? functionType : parser.advance();
            final Token name = validator.expect(TokenType.IDENTIFIER, "function name");

            int expectedNumberOrArguments = MAX_NUM_OF_ARGUMENTS;

            if (functionType.type() == TokenType.PREFIX) {
                this.prefixFunctions.add(name.lexeme());
                expectedNumberOrArguments = 1;
            }
            else if (functionType.type() == TokenType.INFIX) {
                this.infixFunctions.add(name.lexeme());
                expectedNumberOrArguments = 2;
            }

            Token extensionName = null;

            if (parser.peek().type() == TokenType.COLON) {
                validator.expectAfter(TokenType.COLON, "class name");
                extensionName = validator.expect(TokenType.IDENTIFIER, "extension name");
            }

            final List<Parameter> parameters = this.functionParameters(parser, validator, "function name", expectedNumberOrArguments, functionToken);
            final BlockStatement body = this.blockStatement("function", name, parser, validator);

            if (extensionName != null) {
                final FunctionStatement function = new FunctionStatement(extensionName, parameters, body);
                return Result.of(new ExtensionStatement(name, function));
            }
            else {
                return Result.of(new FunctionStatement(name, parameters, body));
            }
        }
        return Result.empty();
    }

    private List<Parameter> functionParameters(final TokenParser parser, final TokenStepValidator validator, final String functionName, final int expectedNumberOrArguments, final Token token) {
        validator.expectAfter(TokenType.LEFT_PAREN, functionName);
        final List<Parameter> parameters = new ArrayList<>();
        if (!parser.check(TokenType.RIGHT_PAREN)) {
            do {
                if (parameters.size() >= expectedNumberOrArguments) {
                    final String message = "Cannot have more than " + expectedNumberOrArguments + " parameters" + (token == null ? "" : " for " + token.type() + " functions");
                    throw new ScriptEvaluationError(message, Phase.PARSING, parser.peek());
                }
                final Token parameterName = validator.expect(TokenType.IDENTIFIER, "parameter name");
                parameters.add(new Parameter(parameterName));
            }
            while (parser.match(TokenType.COMMA));
        }

        validator.expectAfter(TokenType.RIGHT_PAREN, "parameters");
        return parameters;
    }

    @Override
    public Set<Class<? extends Function>> types() {
        return Set.of(ExtensionStatement.class, FunctionStatement.class, Function.class);
    }
}
