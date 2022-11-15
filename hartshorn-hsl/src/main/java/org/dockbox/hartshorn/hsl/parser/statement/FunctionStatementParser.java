/*
 * Copyright 2019-2022 the original author or authors.
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
import org.dockbox.hartshorn.hsl.ast.statement.BlockStatement;
import org.dockbox.hartshorn.hsl.ast.statement.ExtensionStatement;
import org.dockbox.hartshorn.hsl.ast.statement.Function;
import org.dockbox.hartshorn.hsl.ast.statement.FunctionStatement;
import org.dockbox.hartshorn.hsl.ast.statement.ParametricExecutableStatement.Parameter;
import org.dockbox.hartshorn.hsl.parser.TokenParser;
import org.dockbox.hartshorn.hsl.parser.TokenStepValidator;
import org.dockbox.hartshorn.hsl.parser.expression.FunctionParserContext;
import org.dockbox.hartshorn.hsl.runtime.Phase;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.TokenType;
import org.dockbox.hartshorn.util.option.Option;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class FunctionStatementParser extends AbstractBodyStatementParser<Function> {

    @Override
    public Option<Function> parse(final TokenParser parser, final TokenStepValidator validator) {
        if (parser.check(TokenType.PREFIX, TokenType.INFIX, TokenType.FUN)) {
            final Token functionType = parser.advance();
            final Token functionToken = functionType.type() == TokenType.FUN ? functionType : parser.advance();
            final Token name = validator.expect(TokenType.IDENTIFIER, "function name");

            int expectedNumberOrArguments = Integer.MAX_VALUE;

            if (functionType.type() == TokenType.PREFIX) {
                this.functionParserContext(parser).addPrefixFunction(name.lexeme());
                expectedNumberOrArguments = 1;
            }
            else if (functionType.type() == TokenType.INFIX) {
                this.functionParserContext(parser).addInfixFunction(name.lexeme());
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
                final FunctionStatement function = new FunctionStatement(functionType, extensionName, parameters, body);
                return Option.of(new ExtensionStatement(name, function));
            }
            else {
                return Option.of(new FunctionStatement(functionType, name, parameters, body));
            }
        }
        return Option.empty();
    }

    private FunctionParserContext functionParserContext(final TokenParser parser) {
        final Option<FunctionParserContext> context = parser.first(FunctionParserContext.class);
        // Compute locally, to avoid auto-creation of this context
        return context.orCompute(() -> {
            final FunctionParserContext newContext = new FunctionParserContext();
            parser.add(newContext);
            return newContext;
        }).get();
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
