/*
 * Copyright 2019-2022 the original author or authors.
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
import org.dockbox.hartshorn.hsl.ast.expression.LiteralExpression;
import org.dockbox.hartshorn.hsl.ast.statement.Statement;
import org.dockbox.hartshorn.hsl.ast.statement.SwitchCase;
import org.dockbox.hartshorn.hsl.ast.statement.SwitchStatement;
import org.dockbox.hartshorn.hsl.parser.ASTNodeParser;
import org.dockbox.hartshorn.hsl.parser.TokenParser;
import org.dockbox.hartshorn.hsl.parser.TokenStepValidator;
import org.dockbox.hartshorn.hsl.runtime.Phase;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.TokenType;
import org.dockbox.hartshorn.inject.binding.Bound;
import org.dockbox.hartshorn.util.option.Attempt;
import org.dockbox.hartshorn.util.option.Option;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.inject.Inject;

public class SwitchStatementParser implements ASTNodeParser<SwitchStatement> {

    private final CaseBodyStatementParser caseBodyStatementParser;

    @Inject
    @Bound
    public SwitchStatementParser(final CaseBodyStatementParser caseBodyStatementParser) {
        this.caseBodyStatementParser = caseBodyStatementParser;
    }

    @Override
    public Option<SwitchStatement> parse(final TokenParser parser, final TokenStepValidator validator) {
        if (parser.match(TokenType.SWITCH)) {
            final Token switchToken = parser.previous();
            validator.expectAfter(TokenType.LEFT_PAREN, "switch");
            final Expression expr = parser.expression();
            validator.expectAfter(TokenType.RIGHT_PAREN, "expression");

            validator.expectAfter(TokenType.LEFT_BRACE, "switch");

            SwitchCase defaultBody = null;
            final List<SwitchCase> cases = new ArrayList<>();
            final Set<Object> matchedLiterals = new HashSet<>();

            while (parser.match(TokenType.CASE, TokenType.DEFAULT)) {
                final Token caseToken = parser.previous();

                if (caseToken.type() == TokenType.CASE) {
                    final Expression caseExpr = parser.expression();
                    if (!(caseExpr instanceof final LiteralExpression literal)) {
                        throw new ScriptEvaluationError("Case expression must be a literal.", Phase.PARSING, caseToken);
                    }

                    if (matchedLiterals.contains(literal.value())) {
                        throw new ScriptEvaluationError("Duplicate case expression '" + literal.value() + "'.", Phase.PARSING, caseToken);
                    }
                    matchedLiterals.add(literal.value());

                    final Attempt<Statement, ScriptEvaluationError> body = this.caseBodyStatementParser.parse(parser, validator);
                    if (body.errorPresent()) {
                        return Attempt.of(body.error());
                    }
                    cases.add(new SwitchCase(caseToken, body.get(), literal, false));
                }
                else {
                    final Attempt<Statement, ScriptEvaluationError> body = this.caseBodyStatementParser.parse(parser, validator);
                    if (body.errorPresent()) {
                        return Attempt.of(body.error());
                    }
                    defaultBody = new SwitchCase(caseToken, body.get(), null, true);
                }
            }

            validator.expectAfter(TokenType.RIGHT_BRACE, "switch");

            return Option.of(new SwitchStatement(switchToken, expr, cases, defaultBody));
        }
        return Option.empty();
    }

    @Override
    public Set<Class<? extends SwitchStatement>> types() {
        return Set.of(SwitchStatement.class);
    }
}