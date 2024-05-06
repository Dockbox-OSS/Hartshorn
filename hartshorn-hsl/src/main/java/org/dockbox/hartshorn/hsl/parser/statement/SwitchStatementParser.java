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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import org.dockbox.hartshorn.hsl.token.type.ControlTokenType;
import org.dockbox.hartshorn.hsl.token.type.TokenTypePair;
import org.dockbox.hartshorn.util.option.Option;

import jakarta.inject.Inject;

/**
 * TODO: #1061 Add documentation
 *
 * @param <SwitchStatement>> ...
 *
 * @since 0.4.13
 *
 * @author Guus Lieben
 */
public class SwitchStatementParser implements ASTNodeParser<SwitchStatement> {

    private static final String SWITCH = "switch";

    private final CaseBodyStatementParser caseBodyStatementParser;

    @Inject
    public SwitchStatementParser(CaseBodyStatementParser caseBodyStatementParser) {
        this.caseBodyStatementParser = caseBodyStatementParser;
    }

    @Override
    public Option<? extends SwitchStatement> parse(TokenParser parser, TokenStepValidator validator) {
        TokenTypePair parameters = parser.tokenRegistry().tokenPairs().parameters();
        TokenTypePair block = parser.tokenRegistry().tokenPairs().block();
        if (parser.match(ControlTokenType.SWITCH)) {
            Token switchToken = parser.previous();
            validator.expectAfter(parameters.open(), SWITCH);
            Expression expression = parser.expression();
            validator.expectAfter(parameters.close(), "expression");

            validator.expectAfter(block.open(), SWITCH);

            SwitchCase defaultBody = null;
            List<SwitchCase> cases = new ArrayList<>();
            Set<Object> matchedLiterals = new HashSet<>();

            while (parser.match(ControlTokenType.CASE, ControlTokenType.DEFAULT)) {
                Token caseToken = parser.previous();

                if (caseToken.type() == ControlTokenType.CASE) {
                    Expression caseExpression = parser.expression();
                    if (!(caseExpression instanceof LiteralExpression literal)) {
                        throw new ScriptEvaluationError("Case expression must be a literal.", Phase.PARSING, caseToken);
                    }

                    if (matchedLiterals.contains(literal.value())) {
                        throw new ScriptEvaluationError("Duplicate case expression '" + literal.value() + "'.", Phase.PARSING, caseToken);
                    }
                    matchedLiterals.add(literal.value());

                    Option<? extends Statement> body = this.caseBodyStatementParser.parse(parser, validator);
                    if(body.present()) {
                        cases.add(new SwitchCase(caseToken, body.get(), literal, false));
                    }
                }
                else {
                    Option<? extends Statement> body = this.caseBodyStatementParser.parse(parser, validator);
                    if (body.present()) {
                        defaultBody = new SwitchCase(caseToken, body.get(), null, true);
                    }
                }
            }

            validator.expectAfter(block.close(), SWITCH);

            return Option.of(new SwitchStatement(switchToken, expression, cases, defaultBody));
        }
        return Option.empty();
    }

    @Override
    public Set<Class<? extends SwitchStatement>> types() {
        return Set.of(SwitchStatement.class);
    }
}
