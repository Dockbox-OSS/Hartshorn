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

import org.checkerframework.checker.nullness.qual.NonNull;
import org.dockbox.hartshorn.hsl.ScriptEvaluationError;
import org.dockbox.hartshorn.hsl.ast.expression.Expression;
import org.dockbox.hartshorn.hsl.ast.statement.BlockStatement;
import org.dockbox.hartshorn.hsl.ast.statement.BodyStatement;
import org.dockbox.hartshorn.hsl.ast.statement.ForEachStatement;
import org.dockbox.hartshorn.hsl.ast.statement.ForStatement;
import org.dockbox.hartshorn.hsl.ast.statement.Statement;
import org.dockbox.hartshorn.hsl.ast.statement.VariableStatement;
import org.dockbox.hartshorn.hsl.parser.TokenParser;
import org.dockbox.hartshorn.hsl.parser.TokenStepValidator;
import org.dockbox.hartshorn.hsl.runtime.Phase;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.TokenType;
import org.dockbox.hartshorn.util.option.Option;

public class ForStatementParser extends AbstractBodyStatementParser<BodyStatement> {

    @Override
    public Option<BodyStatement> parse(final TokenParser parser, final TokenStepValidator validator) {
        if (parser.check(TokenType.FOR)) {
            final Token forToken = parser.advance();
            validator.expectAfter(TokenType.LEFT_PAREN, TokenType.FOR);

            final VariableStatement initializer = parser.firstCompatibleParser(VariableStatement.class)
                    .flatMap(nodeParser -> nodeParser.parse(parser, validator))
                    .orElseThrow(() -> new ScriptEvaluationError("Expected variable statement in for-each loop", Phase.PARSING, forToken));

            if (parser.match(TokenType.IN)) {
                return this.parseForEachStatement(forToken, parser, validator, initializer);

            } else {
                return this.parseForStatement(forToken, parser, validator, initializer);
            }
        }
        return Option.empty();
    }

    @Override
    public Set<Class<? extends BodyStatement>> types() {
        return Set.of(ForStatement.class, ForEachStatement.class);
    }

    @NonNull
    private Option<BodyStatement> parseForStatement(final Token forToken, final TokenParser parser, final TokenStepValidator validator, final VariableStatement initializer) {
        validator.expectAfter(TokenType.SEMICOLON, "for assignment");

        final Expression condition = parser.expression();
        validator.expectAfter(TokenType.SEMICOLON, "for condition");

        final Statement increment = parser.expressionStatement();
        validator.expectAfter(TokenType.RIGHT_PAREN, "for increment");

        final BlockStatement loopBody = this.blockStatement("for", forToken, parser, validator);
        return Option.of(new ForStatement(initializer, condition, increment, loopBody));
    }

    @NonNull
    private Option<BodyStatement> parseForEachStatement(final Token forToken, final TokenParser parser, final TokenStepValidator validator, final VariableStatement initializer) {
        final Expression collection = parser.expression();
        validator.expectAfter(TokenType.RIGHT_PAREN, "for collection");

        final BlockStatement loopBody = this.blockStatement("for", forToken, parser, validator);
        return Option.of(new ForEachStatement(initializer, collection, loopBody));
    }
}
