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

import org.dockbox.hartshorn.hsl.ast.expression.Expression;
import org.dockbox.hartshorn.hsl.ast.statement.ReturnStatement;
import org.dockbox.hartshorn.hsl.parser.TokenParser;
import org.dockbox.hartshorn.hsl.parser.TokenStepValidator;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.type.ControlTokenType;
import org.dockbox.hartshorn.hsl.token.type.TokenType;
import org.dockbox.hartshorn.util.option.Option;

import java.util.Set;

/**
 * A parser for {@link ReturnStatement} nodes.
 *
 * @since 0.4.13
 *
 * @author Guus Lieben
 */
public class ReturnStatementParser implements StatementParser<ReturnStatement> {

    @Override
    public Option<? extends ReturnStatement> parse(TokenParser parser, TokenStepValidator validator) {
        if (parser.match(ControlTokenType.RETURN, ControlTokenType.YIELD)) {
            Token keyword = parser.previous();
            Expression value = null;
            TokenType statementEnd = parser.tokenRegistry().statementEnd();
            if (!parser.check(statementEnd)) {
                value = parser.expression();
            }
            validator.expectAfter(statementEnd, "return value");
            ReturnStatement.ReturnType returnType = switch (keyword.type()) {
                case ControlTokenType.RETURN -> ReturnStatement.ReturnType.RETURN;
                case ControlTokenType.YIELD -> ReturnStatement.ReturnType.YIELD;
                default -> throw new IllegalStateException("Unexpected return token type: " + keyword.type());
            };
            return Option.of(new ReturnStatement(keyword, value, returnType));
        }
        return Option.empty();
    }

    @Override
    public Set<Class<? extends ReturnStatement>> types() {
        return Set.of(ReturnStatement.class);
    }
}
