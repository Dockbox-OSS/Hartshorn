package org.dockbox.hartshorn.hsl.parser.statement;

import org.dockbox.hartshorn.hsl.ScriptEvaluationError;
import org.dockbox.hartshorn.hsl.ast.statement.ParametricExecutableStatement.Parameter;
import org.dockbox.hartshorn.hsl.parser.TokenParser;
import org.dockbox.hartshorn.hsl.parser.TokenStepValidator;
import org.dockbox.hartshorn.hsl.runtime.Phase;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.TokenType;

import java.util.ArrayList;
import java.util.List;

public interface ParametricStatementParser {

    default List<Parameter> parameters(final TokenParser parser, final TokenStepValidator validator, final String functionName, final int expectedNumberOfArguments, final TokenType functionType) {
        validator.expectAfter(TokenType.LEFT_PAREN, functionName);
        final List<Parameter> parameters = new ArrayList<>();
        if (!parser.check(TokenType.RIGHT_PAREN)) {
            do {
                if (parameters.size() >= expectedNumberOfArguments) {
                    final String message = "Cannot have more than " + expectedNumberOfArguments + " parameters" + (functionType == null ? "" : " for " + functionType.representation() + " functions");
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
}
