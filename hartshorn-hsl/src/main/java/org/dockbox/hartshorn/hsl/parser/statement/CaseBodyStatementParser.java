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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.dockbox.hartshorn.hsl.ScriptEvaluationError;
import org.dockbox.hartshorn.hsl.ast.statement.BlockStatement;
import org.dockbox.hartshorn.hsl.ast.statement.Statement;
import org.dockbox.hartshorn.hsl.parser.ASTNodeParser;
import org.dockbox.hartshorn.hsl.parser.TokenParser;
import org.dockbox.hartshorn.hsl.parser.TokenStepValidator;
import org.dockbox.hartshorn.hsl.runtime.Phase;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.TokenType;
import org.dockbox.hartshorn.util.option.Option;

public class CaseBodyStatementParser implements ASTNodeParser<Statement> {

    @Override
    public Option<Statement> parse(TokenParser parser, TokenStepValidator validator) throws ScriptEvaluationError {
        if (parser.match(TokenType.COLON)) {
            Token colon = parser.previous();
            List<Statement> statements = new ArrayList<>();
            while (!parser.check(TokenType.CASE, TokenType.DEFAULT, TokenType.RIGHT_BRACE)) {
                statements.add(parser.statement());
            }
            return Option.of(new BlockStatement(colon, statements));
        }
        else if (parser.match(TokenType.ARROW)) {
            return Option.of(parser.expressionStatement());
        }
        else {
            throw new ScriptEvaluationError("Expected '%s' or '%s'".formatted(
                    TokenType.COLON.representation(),
                    TokenType.ARROW.representation()
            ), Phase.PARSING, parser.peek());
        }
    }

    @Override
    public Set<Class<? extends Statement>> types() {
        // Only for direct used, should not be used for dynamic parsing
        return Set.of();
    }
}
