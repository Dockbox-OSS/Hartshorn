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

import org.dockbox.hartshorn.hsl.ast.statement.BlockStatement;
import org.dockbox.hartshorn.hsl.ast.statement.Statement;
import org.dockbox.hartshorn.hsl.parser.ASTNodeParser;
import org.dockbox.hartshorn.hsl.parser.TokenParser;
import org.dockbox.hartshorn.hsl.parser.TokenStepValidator;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.TokenType;
import org.dockbox.hartshorn.util.option.Option;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BlockStatementParser implements ASTNodeParser<BlockStatement> {

    @Override
    public Option<BlockStatement> parse(final TokenParser parser, final TokenStepValidator validator) {
        if (parser.check(TokenType.LEFT_BRACE)) {
            final Token start = parser.advance();

            final List<Statement> statements = new ArrayList<>();
            while (!parser.check(TokenType.RIGHT_BRACE) && !parser.isAtEnd()) {
                statements.add(parser.statement());
            }
            validator.expectAfter(TokenType.RIGHT_BRACE, "block");

            return Option.of(new BlockStatement(start, statements));
        }
        return Option.empty();
    }

    @Override
    public Set<Class<? extends BlockStatement>> types() {
        return Set.of(BlockStatement.class);
    }
}
