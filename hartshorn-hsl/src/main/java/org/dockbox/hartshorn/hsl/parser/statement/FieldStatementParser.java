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

import org.dockbox.hartshorn.hsl.ScriptEvaluationError;
import org.dockbox.hartshorn.hsl.ast.expression.Expression;
import org.dockbox.hartshorn.hsl.ast.statement.BlockStatement;
import org.dockbox.hartshorn.hsl.ast.statement.FieldGetStatement;
import org.dockbox.hartshorn.hsl.ast.statement.FieldSetStatement;
import org.dockbox.hartshorn.hsl.ast.statement.FieldStatement;
import org.dockbox.hartshorn.hsl.ast.statement.ParametricExecutableStatement;
import org.dockbox.hartshorn.hsl.ast.statement.VariableStatement;
import org.dockbox.hartshorn.hsl.parser.TokenParser;
import org.dockbox.hartshorn.hsl.parser.TokenStepValidator;
import org.dockbox.hartshorn.hsl.runtime.Phase;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.type.BaseTokenType;
import org.dockbox.hartshorn.hsl.token.type.ClassTokenType;
import org.dockbox.hartshorn.hsl.token.type.MemberModifierTokenType;
import org.dockbox.hartshorn.hsl.token.type.TokenType;
import org.dockbox.hartshorn.hsl.token.type.TokenTypePair;
import org.dockbox.hartshorn.util.option.Option;

import java.util.Set;

/**
 * TODO: #1061 Add documentation
 *
 * @since 0.4.13
 *
 * @author Guus Lieben
 */
public class FieldStatementParser extends AbstractBodyStatementParser<FieldStatement> {

    @Override
    public Option<? extends FieldStatement> parse(TokenParser parser, TokenStepValidator validator) {
        Token modifier = parser.find(MemberModifierTokenType.PUBLIC, MemberModifierTokenType.PRIVATE);
        boolean isFinal = parser.match(MemberModifierTokenType.FINAL);
        TokenType identifier = parser.tokenRegistry().literals().identifier();
        Token name = validator.expect(identifier, "variable name");

        Expression initializer = null;
        if(parser.match(BaseTokenType.EQUAL)) {
            initializer = parser.expression();
        }

        validator.expectAfter(parser.tokenRegistry().statementEnd(), "variable declaration");
        VariableStatement variable = new VariableStatement(name, initializer);
        FieldStatement fieldStatement = new FieldStatement(modifier, variable.name(), variable.initializer(), isFinal);

        TokenTypePair block = parser.tokenRegistry().tokenPairs().block();
        if (parser.match(block.open())) {
            this.fieldMemberStatement(parser, validator, fieldStatement); // Get or set
            this.fieldMemberStatement(parser, validator, fieldStatement); // Get or set
            validator.expectAfter(block.close(), "field members");
        }

        return Option.of(fieldStatement);
    }

    private void fieldMemberStatement(TokenParser parser, TokenStepValidator validator, FieldStatement fieldStatement) {
        Token modifier = parser.find(MemberModifierTokenType.PUBLIC, MemberModifierTokenType.PRIVATE);
        if (parser.match(ClassTokenType.GET, ClassTokenType.SET)) {
            Token member = parser.previous();
            switch (member.type()) {
                case ClassTokenType.GET -> {
                    final FieldGetStatement statement = this.fieldGetStatement(parser, validator, modifier, member, fieldStatement);
                    fieldStatement.withGetter(statement);
                }
                case ClassTokenType.SET -> {
                    final FieldSetStatement statement = this.fieldSetStatement(parser, validator, modifier, member, fieldStatement);
                    fieldStatement.withSetter(statement);
                }
                default -> throw new ScriptEvaluationError("Unsupported field member type: " + member.type(), Phase.PARSING, member);
            };
        }
    }

    private FieldGetStatement fieldGetStatement(TokenParser parser, TokenStepValidator validator, Token modifier, final Token get, final FieldStatement field) {
        TokenTypePair parameters = parser.tokenRegistry().tokenPairs().parameters();
        final BlockStatement body;
        if (parser.check(parameters.open())) {
            body = blockStatement("field get declaration", get, parser, validator);
        }
        else {
            validator.expectAfter(parser.tokenRegistry().statementEnd(), "field get declaration");
            body = null;
        }
        return new FieldGetStatement(modifier, get, field, body);
    }

    private FieldSetStatement fieldSetStatement(TokenParser parser, TokenStepValidator validator,  Token modifier, final Token set, final FieldStatement field) {
        TokenTypePair parameters = parser.tokenRegistry().tokenPairs().parameters();
        if (parser.match(parameters.open())) {
            final Token parameterName = validator.expect(parser.tokenRegistry().literals().identifier(), "parameter name");
            final ParametricExecutableStatement.Parameter parameter = new ParametricExecutableStatement.Parameter(parameterName);
            validator.expectAfter(parameters.close(), "field set declaration");

            BlockStatement body = blockStatement("field set declaration", set, parser, validator);
            return new FieldSetStatement(modifier, set, field, body, parameter);
        }
        else{
            validator.expectAfter(parser.tokenRegistry().statementEnd(), "field set declaration");
            return new FieldSetStatement(modifier, set, field, null, null);
        }
    }

    @Override
    public Set<Class<? extends FieldStatement>> types() {
        // Only for direct use, should not be used for dynamic parsing
        return Set.of();
    }
}
