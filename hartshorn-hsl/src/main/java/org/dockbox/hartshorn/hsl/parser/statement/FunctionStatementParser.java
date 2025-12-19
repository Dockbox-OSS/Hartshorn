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

import org.dockbox.hartshorn.hsl.ast.statement.BlockStatement;
import org.dockbox.hartshorn.hsl.ast.statement.Function;
import org.dockbox.hartshorn.hsl.ast.statement.FunctionStatement;
import org.dockbox.hartshorn.hsl.ast.statement.ParametricExecutableStatement.Parameter;
import org.dockbox.hartshorn.hsl.parser.TokenParser;
import org.dockbox.hartshorn.hsl.parser.TokenStepValidator;
import org.dockbox.hartshorn.hsl.parser.expression.FunctionParserContext;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.type.FunctionTokenType;
import org.dockbox.hartshorn.hsl.token.type.TokenType;
import org.dockbox.hartshorn.util.option.Option;

import java.util.List;
import java.util.Set;

/**
 * A parser for {@link FunctionStatement} nodes.
 *
 * @since 0.4.13
 *
 * @author Guus Lieben
 */
public class FunctionStatementParser extends AbstractBodyStatementParser<Function>
    implements ParametricStatementParser {

    @Override
    public Option<? extends Function> parse(TokenParser parser, TokenStepValidator validator) {
        if (parser.check(FunctionTokenType.PREFIX,
            FunctionTokenType.INFIX,
            FunctionTokenType.FUNCTION)) {
            Token functionType = parser.advance();
            FunctionTokenType functionTokenType = (FunctionTokenType) functionType.type();
            if (functionType.type() != FunctionTokenType.FUNCTION) {
                parser.advance();
            }
            TokenType identifier = parser.tokenRegistry().literals().identifier();
            Token name = validator.expect(identifier, "function name");

            int expectedNumberOfArguments = -1;

            if (functionType.type() == FunctionTokenType.PREFIX) {
                this.functionParserContext(parser).addPrefixFunction(name.lexeme());
                expectedNumberOfArguments = 1;
            }
            else if (functionType.type() == FunctionTokenType.INFIX) {
                this.functionParserContext(parser).addInfixFunction(name.lexeme());
                expectedNumberOfArguments = 2;
            }

            List<Parameter> parameters = this.parameters(
                parser,
                validator,
                "function name",
                expectedNumberOfArguments,
                functionTokenType
            );
            BlockStatement body = this.blockStatement("function", name, parser, validator);

            return Option.of(new FunctionStatement(functionType, name, parameters, body));
        }
        return Option.empty();
    }

    private FunctionParserContext functionParserContext(TokenParser parser) {
        Option<FunctionParserContext> context = parser.firstContext(FunctionParserContext.class);
        // Compute locally, to avoid auto-creation of this context
        return context.orCompute(() -> {
            FunctionParserContext newContext = new FunctionParserContext();
            parser.addContext(newContext);
            return newContext;
        }).get();
    }

    @Override
    public Set<Class<? extends Function>> types() {
        return Set.of(FunctionStatement.class, Function.class);
    }
}
