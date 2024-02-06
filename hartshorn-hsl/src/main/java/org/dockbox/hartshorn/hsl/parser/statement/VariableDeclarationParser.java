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

import java.util.Set;

import org.dockbox.hartshorn.hsl.ast.expression.Expression;
import org.dockbox.hartshorn.hsl.ast.statement.VariableStatement;
import org.dockbox.hartshorn.hsl.parser.ASTNodeParser;
import org.dockbox.hartshorn.hsl.parser.TokenParser;
import org.dockbox.hartshorn.hsl.parser.TokenStepValidator;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.type.BaseTokenType;
import org.dockbox.hartshorn.hsl.token.type.LiteralTokenType;
import org.dockbox.hartshorn.hsl.token.type.VariableTokenType;
import org.dockbox.hartshorn.util.option.Option;

public class VariableDeclarationParser implements ASTNodeParser<VariableStatement> {

    @Override
    public Option<? extends VariableStatement> parse(final TokenParser parser, final TokenStepValidator validator) {
        if (parser.match(VariableTokenType.VAR)) {
            final Token name = validator.expect(LiteralTokenType.IDENTIFIER, "variable name");

            Expression initializer = null;
            if (parser.match(BaseTokenType.EQUAL)) {
                initializer = parser.expression();
            }

            validator.expectAfter(BaseTokenType.SEMICOLON, "variable declaration");
            return Option.of(new VariableStatement(name, initializer));
        }
        return Option.empty();
    }

    @Override
    public Set<Class<? extends VariableStatement>> types() {
        return Set.of(VariableStatement.class);
    }
}
