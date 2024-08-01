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
import org.dockbox.hartshorn.hsl.ast.statement.ReturnStatement;
import org.dockbox.hartshorn.hsl.ast.statement.Statement;
import org.dockbox.hartshorn.hsl.ast.statement.TestStatement;
import org.dockbox.hartshorn.hsl.parser.TokenParser;
import org.dockbox.hartshorn.hsl.parser.TokenStepValidator;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.type.TokenTypePair;
import org.dockbox.hartshorn.hsl.token.type.AssertTokenType;
import org.dockbox.hartshorn.hsl.token.type.LiteralTokenType;
import org.dockbox.hartshorn.util.CollectionUtilities;
import org.dockbox.hartshorn.util.option.Option;

/**
 * TODO: #1061 Add documentation
 *
 * @since 0.4.13
 *
 * @author Guus Lieben
 */
public class TestStatementParser extends AbstractBodyStatementParser<TestStatement> {

    @Override
    public Option<? extends TestStatement> parse(TokenParser parser, TokenStepValidator validator) {
        TokenTypePair parameter = parser.tokenRegistry().tokenPairs().parameters();
        TokenTypePair block = parser.tokenRegistry().tokenPairs().block();
        if (parser.match(AssertTokenType.TEST)) {
            validator.expectAfter(parameter.open(), "test statement");

            Token name = validator.expect(LiteralTokenType.STRING, "test name");
            validator.expectAfter(parameter.close(), "test statement name value");
            validator.expectBefore(block.open(), "test body");

            List<Statement> statements = new ArrayList<>();
            Token bodyStart = parser.peek();
            while (!parser.match(block.close()) && !parser.isAtEnd()) {
                Statement statement = parser.statement();
                statements.add(statement);
            }

            if (statements.isEmpty()) {
                throw new IllegalStateException("Test body cannot be empty");
            }
            else if (!(CollectionUtilities.last(statements) instanceof ReturnStatement)) {
                throw new IllegalStateException("Test body must end with a return statement");
            }

            BlockStatement body = new BlockStatement(bodyStart, statements);
            return Option.of(new TestStatement(name, body));
        }
        return Option.empty();
    }

    @Override
    public Set<Class<? extends TestStatement>> types() {
        return Set.of(TestStatement.class);
    }
}
