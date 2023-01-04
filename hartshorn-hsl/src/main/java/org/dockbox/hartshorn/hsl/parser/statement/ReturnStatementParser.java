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

import org.dockbox.hartshorn.hsl.ast.expression.Expression;
import org.dockbox.hartshorn.hsl.ast.statement.ReturnStatement;
import org.dockbox.hartshorn.hsl.parser.ASTNodeParser;
import org.dockbox.hartshorn.hsl.parser.TokenParser;
import org.dockbox.hartshorn.hsl.parser.TokenStepValidator;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.TokenType;
import org.dockbox.hartshorn.util.option.Option;

import java.util.Set;

public class ReturnStatementParser implements ASTNodeParser<ReturnStatement> {

    @Override
    public Option<ReturnStatement> parse(final TokenParser parser, final TokenStepValidator validator) {
        if (parser.match(TokenType.RETURN)) {
            final Token keyword = parser.previous();
            Expression value = null;
            if (!parser.check(TokenType.SEMICOLON)) {
                value = parser.expression();
            }
            validator.expectAfter(TokenType.SEMICOLON, "return value");
            return Option.of(new ReturnStatement(keyword, value));
        }
        return Option.empty();
    }

    @Override
    public Set<Class<? extends ReturnStatement>> types() {
        return Set.of(ReturnStatement.class);
    }
}
