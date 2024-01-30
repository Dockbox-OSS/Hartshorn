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

import java.util.Set;

import org.dockbox.hartshorn.hsl.ast.statement.BreakStatement;
import org.dockbox.hartshorn.hsl.parser.ASTNodeParser;
import org.dockbox.hartshorn.hsl.parser.TokenParser;
import org.dockbox.hartshorn.hsl.parser.TokenStepValidator;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.type.BaseTokenType;
import org.dockbox.hartshorn.hsl.token.type.ControlTokenType;
import org.dockbox.hartshorn.util.option.Option;

public class BreakStatementParser implements ASTNodeParser<BreakStatement> {

    @Override
    public Option<? extends BreakStatement> parse(TokenParser parser, TokenStepValidator validator) {
        if (parser.match(ControlTokenType.BREAK)) {
            Token keyword = parser.previous();
            validator.expectAfter(BaseTokenType.SEMICOLON, "value");
            return Option.of(new BreakStatement(keyword));
        }
        return Option.empty();
    }

    @Override
    public Set<Class<? extends BreakStatement>> types() {
        return Set.of(BreakStatement.class);
    }
}
