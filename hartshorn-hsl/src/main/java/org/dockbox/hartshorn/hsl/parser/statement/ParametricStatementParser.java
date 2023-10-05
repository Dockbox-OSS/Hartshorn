/*
 * Copyright 2019-2023 the original author or authors.
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
import org.dockbox.hartshorn.hsl.token.type.TokenTypePair;
import org.dockbox.hartshorn.hsl.token.type.TokenType;
import org.dockbox.hartshorn.hsl.token.type.BaseTokenType;
import org.dockbox.hartshorn.hsl.token.type.LiteralTokenType;

public interface ParametricStatementParser {

    default List<Parameter> parameters(final TokenParser parser, final TokenStepValidator validator, final String functionName, final int expectedNumberOfArguments, final TokenType functionType) {
        TokenTypePair parameterTokens = parser.tokenRegistry().tokenPairs().parameters();
        validator.expectAfter(parameterTokens.open(), functionName);
        final List<Parameter> parameters = new ArrayList<>();
        if (!parser.check(parameterTokens.close())) {
            do {
                if (parameters.size() >= expectedNumberOfArguments) {
                    final String message = "Cannot have more than " + expectedNumberOfArguments + " parameters" + (functionType == null ? "" : " for " + functionType.representation() + " functions");
                    throw new ScriptEvaluationError(message, Phase.PARSING, parser.peek());
                }
                final Token parameterName = validator.expect(LiteralTokenType.IDENTIFIER, "parameter name");
                parameters.add(new Parameter(parameterName));
            }
            while (parser.match(BaseTokenType.COMMA));
        }

        validator.expectAfter(parameterTokens.close(), "parameters");
        return parameters;
    }
}
