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

import org.dockbox.hartshorn.hsl.ast.statement.NativeFunctionStatement;
import org.dockbox.hartshorn.hsl.ast.statement.ParametricExecutableStatement.Parameter;
import org.dockbox.hartshorn.hsl.parser.TokenParser;
import org.dockbox.hartshorn.hsl.parser.TokenStepValidator;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.type.BaseTokenType;
import org.dockbox.hartshorn.hsl.token.type.FunctionTokenType;
import org.dockbox.hartshorn.hsl.token.type.TokenType;
import org.dockbox.hartshorn.util.option.Option;

import java.util.List;
import java.util.Set;

/**
 * TODO: #1061 Add documentation
 *
 * @since 0.4.13
 *
 * @author Guus Lieben
 */
public class NativeFunctionStatementParser extends AbstractBodyStatementParser<NativeFunctionStatement> implements ParametricStatementParser {

    @Override
    public Option<? extends NativeFunctionStatement> parse(TokenParser parser, TokenStepValidator validator) {
        if (parser.match(FunctionTokenType.NATIVE) && parser.match(FunctionTokenType.FUNCTION)) {
            TokenType identifier = parser.tokenRegistry().literals().identifier();
            Token moduleName = validator.expect(identifier, "module name");

            while (parser.match(BaseTokenType.DOT)) {
                Token token = Token.of(BaseTokenType.DOT)
                        .position(moduleName)
                        .build();
                moduleName.concat(token);

                Token submodule = validator.expect(identifier, "module name");
                moduleName.concat(submodule);
            }

            validator.expectBefore(BaseTokenType.COLON, "function name");
            Token funcName = validator.expect(identifier, "function name");
            List<Parameter> parameters = ParametricStatementParser.super.parameters(parser, validator, "function name", -1, FunctionTokenType.NATIVE);

            validator.expectAfter(parser.tokenRegistry().statementEnd(), "value");
            // Do not resolve actual methods at this point, as we might not have loaded
            // the module yet.
            return Option.of(new NativeFunctionStatement(funcName, moduleName, null, parameters));
        }
        return Option.empty();
    }

    @Override
    public Set<Class<? extends NativeFunctionStatement>> types() {
        return Set.of(NativeFunctionStatement.class);
    }
}
