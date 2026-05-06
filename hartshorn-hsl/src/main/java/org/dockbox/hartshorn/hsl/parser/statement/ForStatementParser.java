/*
 * Copyright 2019-2026 the original author or authors.
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
import org.dockbox.hartshorn.hsl.runtime.DiagnosticMessage;
import org.dockbox.hartshorn.hsl.runtime.Phase;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.type.LoopTokenType;
import org.dockbox.hartshorn.util.option.Option;
import org.jspecify.annotations.NonNull;

import java.util.Set;

/**
 * A parser for {@link ForStatement} and {@link ForEachStatement} nodes.
 *
 * @since 0.4.13
 * 
 * @author Guus Lieben
 */
public class ForStatementParser extends AbstractBodyStatementParser<BodyStatement> {

    @Override
    public Option<? extends BodyStatement> parse(TokenParser parser, TokenStepValidator validator) {
        if (parser.check(LoopTokenType.FOR)) {
            Token forToken = parser.advance();
            validator.expectAfter(parser.tokenRegistry().tokenPairs().parameters().open(),
                LoopTokenType.FOR);

            VariableStatement initializer = parser.firstCompatibleParser(VariableStatement.class)
                .flatMap(nodeParser -> nodeParser.parse(parser, validator))
                .orElseThrow(() -> ScriptEvaluationError.builder(Phase.PARSING)
                    .message(DiagnosticMessage.EXPECTED_VARIABLE_STATEMENT_FOR_EACH)
                    .at(forToken)
                    .build());

            if (parser.match(LoopTokenType.IN)) {
                return this.parseForEachStatement(forToken, parser, validator, initializer);
            }
            else {
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
    private Option<BodyStatement> parseForStatement(
        Token forToken,
        TokenParser parser,
        TokenStepValidator validator,
        VariableStatement initializer
    ) {
        validator.expectAfter(parser.tokenRegistry().statementEnd(), "for assignment");

        Expression condition = parser.expression();
        validator.expectAfter(parser.tokenRegistry().statementEnd(), "for condition");

        Statement increment = parser.expressionStatement();
        validator.expectAfter(parser.tokenRegistry().tokenPairs().parameters().close(),
            "for increment");

        BlockStatement loopBody = this.blockStatement("for", forToken, parser, validator);
        return Option.of(new ForStatement(initializer, condition, increment, loopBody));
    }

    @NonNull
    private Option<BodyStatement> parseForEachStatement(
        Token forToken,
        TokenParser parser,
        TokenStepValidator validator,
        VariableStatement initializer
    ) {
        Expression collection = parser.expression();
        validator.expectAfter(parser.tokenRegistry().tokenPairs().parameters().close(),
            "for collection");

        BlockStatement loopBody = this.blockStatement("for", forToken, parser, validator);
        return Option.of(new ForEachStatement(initializer, collection, loopBody));
    }
}
