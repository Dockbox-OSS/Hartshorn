/*
 * Copyright 2019-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dockbox.hartshorn.hsl.parser.statement;

import org.dockbox.hartshorn.hsl.ScriptEvaluationError;
import org.dockbox.hartshorn.hsl.ast.statement.ParametricExecutableStatement.Parameter;
import org.dockbox.hartshorn.hsl.parser.TokenParser;
import org.dockbox.hartshorn.hsl.parser.TokenStepValidator;
import org.dockbox.hartshorn.hsl.runtime.DiagnosticMessage;
import org.dockbox.hartshorn.hsl.runtime.Phase;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.type.BaseTokenType;
import org.dockbox.hartshorn.hsl.token.type.TokenType;
import org.dockbox.hartshorn.hsl.token.type.TokenTypePair;

import java.util.ArrayList;
import java.util.List;

/**
 * Base interface for parsers that parse statements with parameters, such as functions or
 * constructors.
 *
 * @since 0.4.13
 *
 * @author Guus Lieben
 */
public interface ParametricStatementParser {

    /**
     * Attempts to parse a list of parameters for a statement. If the number of parameters does not
     * match the expected number of arguments, a {@link ScriptEvaluationError} will be thrown
     * instead.
     *
     * @param parser the parser which is parsing the parameters
     * @param validator the validator to use for validating tokens
     * @param functionName the name of the function being parsed
     * @param expectedNumberOfArguments the expected number of arguments, or -1 for any number
     * @param functionType the type of function being parsed
     *
     * @return the list of parsed parameters
     */
    default List<Parameter> parameters(
        TokenParser parser,
        TokenStepValidator validator,
        String functionName,
        int expectedNumberOfArguments,
        TokenType functionType
    ) {
        TokenTypePair parameterTokens = parser.tokenRegistry().tokenPairs().parameters();
        validator.expectAfter(parameterTokens.open(), functionName);
        List<Parameter> parameters = new ArrayList<>();
        if (!parser.check(parameterTokens.close())) {
            TokenType identifier = parser.tokenRegistry().literals().identifier();
            do {
                if (expectedNumberOfArguments >= 0
                    && parameters.size() >= expectedNumberOfArguments) {
                    throw ScriptEvaluationError.builder(Phase.PARSING)
                        .message(DiagnosticMessage.TOO_MANY_PARAMETERS_FOR_X,
                            expectedNumberOfArguments,
                            functionType.representation()
                        ).at(parser.peek())
                        .build();
                }
                Token parameterName = validator.expect(identifier, "parameter name");
                parameters.add(new Parameter(parameterName));
            }
            while (parser.match(BaseTokenType.COMMA));
        }

        if (expectedNumberOfArguments >= 0 && parameters.size() < expectedNumberOfArguments) {
            throw ScriptEvaluationError.builder(Phase.PARSING)
                .message(DiagnosticMessage.NOT_ENOUGH_PARAMETERS_FOR_X,
                    expectedNumberOfArguments,
                    functionType.representation()
                ).at(parser.peek())
                .build();
        }
        validator.expectAfter(parameterTokens.close(), "parameters");
        return parameters;
    }
}
