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

import java.util.List;
import java.util.Set;

import org.dockbox.hartshorn.hsl.ast.statement.BlockStatement;
import org.dockbox.hartshorn.hsl.ast.statement.ConstructorStatement;
import org.dockbox.hartshorn.hsl.ast.statement.ParametricExecutableStatement.Parameter;
import org.dockbox.hartshorn.hsl.parser.TokenParser;
import org.dockbox.hartshorn.hsl.parser.TokenStepValidator;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.type.FunctionTokenType;
import org.dockbox.hartshorn.util.option.Option;

/**
 * TODO: #1061 Add documentation
 *
 * @since 0.4.13
 *
 * @author Guus Lieben
 */
public class ConstructorStatementParser extends AbstractBodyStatementParser<ConstructorStatement> implements ParametricStatementParser {

    @Override
    public Option<? extends ConstructorStatement> parse(TokenParser parser, TokenStepValidator validator) {
        Token keyword = parser.peek();
        if (keyword.type() == FunctionTokenType.CONSTRUCTOR) {
            parser.advance();
            List<Parameter> parameters = this.parameters(parser, validator, "constructor", Integer.MAX_VALUE, keyword.type());
            BlockStatement body = this.blockStatement("constructor", keyword, parser, validator);
            return Option.of(new ConstructorStatement(keyword, parameters, body));
        }
        return Option.empty();
    }

    @Override
    public Set<Class<? extends ConstructorStatement>> types() {
        return Set.of(ConstructorStatement.class);
    }
}
