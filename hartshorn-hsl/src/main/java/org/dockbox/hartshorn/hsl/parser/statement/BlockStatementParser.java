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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.dockbox.hartshorn.hsl.ast.statement.BlockStatement;
import org.dockbox.hartshorn.hsl.ast.statement.Statement;
import org.dockbox.hartshorn.hsl.parser.ASTNodeParser;
import org.dockbox.hartshorn.hsl.parser.TokenParser;
import org.dockbox.hartshorn.hsl.parser.TokenStepValidator;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.type.TokenTypePair;
import org.dockbox.hartshorn.util.option.Option;

/**
 * A parser for block statements which respects the block token pair defined in the token registry.
 *
 * @see BlockStatement
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public class BlockStatementParser implements ASTNodeParser<BlockStatement> {

    @Override
    public Option<? extends BlockStatement> parse(TokenParser parser, TokenStepValidator validator) {
        TokenTypePair block = parser.tokenRegistry().tokenPairs().block();
        if (parser.check(block.open())) {
            Token start = parser.advance();

            List<Statement> statements = new ArrayList<>();
            while (!parser.check(block.close()) && !parser.isAtEnd()) {
                statements.add(parser.statement());
            }
            validator.expectAfter(block.close(), "block");

            return Option.of(new BlockStatement(start, statements));
        }
        return Option.empty();
    }

    @Override
    public Set<Class<? extends BlockStatement>> types() {
        return Set.of(BlockStatement.class);
    }
}
