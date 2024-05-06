/*
 * Copyright 2019-2024 the original author or authors.
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

import java.util.ArrayList;
import java.util.List;

import org.dockbox.hartshorn.hsl.ScriptEvaluationError;
import org.dockbox.hartshorn.hsl.ast.statement.ParametricExecutableStatement.Parameter;
import org.dockbox.hartshorn.hsl.parser.TokenParser;
import org.dockbox.hartshorn.hsl.parser.TokenStepValidator;
import org.dockbox.hartshorn.hsl.runtime.Phase;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.type.BaseTokenType;
import org.dockbox.hartshorn.hsl.token.type.TokenType;
import org.dockbox.hartshorn.hsl.token.type.TokenTypePair;

/**
 * TODO: #1061 Add documentation
 *
 * @since 0.4.13
 *
 * @author Guus Lieben
 */
public interface ParametricStatementParser {

    default List<Parameter> parameters(TokenParser parser, TokenStepValidator validator, String functionName, int expectedNumberOfArguments, TokenType functionType) {
        TokenTypePair parameterTokens = parser.tokenRegistry().tokenPairs().parameters();
        validator.expectAfter(parameterTokens.open(), functionName);
        List<Parameter> parameters = new ArrayList<>();
        if (!parser.check(parameterTokens.close())) {
            TokenType identifier = parser.tokenRegistry().literals().identifier();
            do {
                if (parameters.size() >= expectedNumberOfArguments) {
                    String message = "Cannot have more than " + expectedNumberOfArguments + " parameters" + (functionType == null ? "" : " for " + functionType.representation() + " functions");
                    throw new ScriptEvaluationError(message, Phase.PARSING, parser.peek());
                }
                Token parameterName = validator.expect(identifier, "parameter name");
                parameters.add(new Parameter(parameterName));
            }
            while (parser.match(BaseTokenType.COMMA));
        }

        validator.expectAfter(parameterTokens.close(), "parameters");
        return parameters;
    }
}
