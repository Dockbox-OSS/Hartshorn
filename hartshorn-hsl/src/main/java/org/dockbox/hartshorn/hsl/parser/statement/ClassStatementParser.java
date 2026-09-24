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
import org.dockbox.hartshorn.hsl.ast.expression.VariableExpression;
import org.dockbox.hartshorn.hsl.ast.statement.ClassMemberStatement;
import org.dockbox.hartshorn.hsl.ast.statement.ClassStatement;
import org.dockbox.hartshorn.hsl.parser.TokenParser;
import org.dockbox.hartshorn.hsl.parser.TokenStepValidator;
import org.dockbox.hartshorn.hsl.runtime.DiagnosticMessage;
import org.dockbox.hartshorn.hsl.runtime.Phase;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.type.BaseTokenType;
import org.dockbox.hartshorn.hsl.token.type.ClassTokenType;
import org.dockbox.hartshorn.hsl.token.type.TokenType;
import org.dockbox.hartshorn.hsl.token.type.TokenTypePair;
import org.dockbox.hartshorn.util.option.Option;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * A parser for class statements, which dynamically delegates parsing of class body members
 * to registered {@link ClassMemberParser} instances.
 *
 * @since 0.4.13
 * 
 * @author Guus Lieben
 */
public class ClassStatementParser implements StatementParser<ClassStatement> {

    @Override
    public Option<? extends ClassStatement> parse(
        TokenParser parser,
        TokenStepValidator validator
    ) {
        TokenTypePair block = parser.tokenRegistry().tokenPairs().block();
        if (parser.match(ClassTokenType.CLASS)) {
            TokenType identifier = parser.tokenRegistry().literals().identifier();
            Token name = validator.expect(identifier, "class name");

            boolean isDynamic = parser.match(BaseTokenType.QUESTION_MARK);

            VariableExpression superClass = null;
            if (parser.match(ClassTokenType.EXTENDS)) {
                validator.expect(identifier, "super class name");
                superClass = new VariableExpression(parser.previous());
            }

            validator.expectBefore(block.open(), "class body");

            List<ClassMemberStatement> members = new ArrayList<>();
            while (!parser.check(block.close()) && !parser.isAtEnd()) {
                ClassMemberStatement member = this.parseMember(parser, validator);
                if (member == null) {
                    throw ScriptEvaluationError.builder(Phase.PARSING)
                        .message(DiagnosticMessage.UNSUPPORTED_BODY_STATEMENT,
                            parser.peek().type().representation())
                        .at(parser.peek())
                        .build();
                }
                members.add(member);
            }

            validator.expectAfter(block.close(), "class body");

            return Option.of(new ClassStatement(
                name,
                superClass,
                members,
                isDynamic
            ));
        }
        return Option.empty();
    }

    private ClassMemberStatement parseMember(
        TokenParser parser,
        TokenStepValidator validator
    ) throws ScriptEvaluationError {
        for (ClassMemberParser<?> memberParser : parser.classMemberParsers()) {
            Option<?> statement = memberParser.parse(parser, validator);
            if (statement.present() && statement.get() instanceof ClassMemberStatement member) {
                return member;
            }
        }
        return null;
    }

    @Override
    public Set<Class<? extends ClassStatement>> types() {
        return Set.of(ClassStatement.class);
    }
}
