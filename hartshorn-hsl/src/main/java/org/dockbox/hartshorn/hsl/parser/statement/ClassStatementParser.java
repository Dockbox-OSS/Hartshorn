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
import org.dockbox.hartshorn.hsl.ast.expression.VariableExpression;
import org.dockbox.hartshorn.hsl.ast.statement.ClassStatement;
import org.dockbox.hartshorn.hsl.ast.statement.ConstructorStatement;
import org.dockbox.hartshorn.hsl.ast.statement.FieldStatement;
import org.dockbox.hartshorn.hsl.ast.statement.FunctionStatement;
import org.dockbox.hartshorn.hsl.ast.statement.Statement;
import org.dockbox.hartshorn.hsl.parser.ASTNodeParser;
import org.dockbox.hartshorn.hsl.parser.TokenParser;
import org.dockbox.hartshorn.hsl.parser.TokenStepValidator;
import org.dockbox.hartshorn.hsl.runtime.Phase;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.type.TokenTypePair;
import org.dockbox.hartshorn.hsl.token.type.BaseTokenType;
import org.dockbox.hartshorn.hsl.token.type.ClassTokenType;
import org.dockbox.hartshorn.hsl.token.type.FunctionTokenType;
import org.dockbox.hartshorn.hsl.token.type.LiteralTokenType;
import org.dockbox.hartshorn.util.option.Option;

import jakarta.inject.Inject;

public class ClassStatementParser implements ASTNodeParser<ClassStatement> {

    private final FieldStatementParser fieldParser;

    @Inject
    public ClassStatementParser(final FieldStatementParser fieldParser) {
        this.fieldParser = fieldParser;
    }

    @Override
    public Option<ClassStatement> parse(final TokenParser parser, final TokenStepValidator validator) {
        TokenTypePair block = parser.tokenSet().tokenPairs().block();
        if (parser.match(ClassTokenType.CLASS)) {
            final Token name = validator.expect(LiteralTokenType.IDENTIFIER, "class name");

            final boolean isDynamic = parser.match(BaseTokenType.QUESTION_MARK);

            VariableExpression superClass = null;
            if (parser.match(ClassTokenType.EXTENDS)) {
                validator.expect(LiteralTokenType.IDENTIFIER, "super class name");
                superClass = new VariableExpression(parser.previous());
            }

            validator.expectBefore(block.open(), "class body");

            final List<FunctionStatement> methods = new ArrayList<>();
            final List<FieldStatement> fields = new ArrayList<>();
            ConstructorStatement constructor = null;
            while (!parser.check(block.close()) && !parser.isAtEnd()) {
                final Statement declaration = this.classBodyStatement(parser, validator);
                if (declaration instanceof ConstructorStatement constructorStatement) {
                    constructor = constructorStatement;
                }
                else if (declaration instanceof FunctionStatement function) {
                    methods.add(function);
                }
                else if (declaration instanceof FieldStatement field) {
                    fields.add(field);
                }
                else {
                    throw new ScriptEvaluationError("Unsupported class body statement type: " + declaration.getClass()
                            .getSimpleName(), Phase.PARSING, parser.peek());
                }
            }

            validator.expectAfter(block.close(), "class body");

            return Option.of(new ClassStatement(name, superClass, constructor, methods, fields, isDynamic));
        }
        return Option.empty();
    }

    private Statement classBodyStatement(final TokenParser parser, final TokenStepValidator validator) {
        if (parser.check(FunctionTokenType.CONSTRUCTOR)) {
            return this.handleDelegate(parser, validator, parser.firstCompatibleParser(ConstructorStatement.class));
        }
        else if (parser.check(FunctionTokenType.FUNCTION)) {
            return this.handleDelegate(parser, validator, parser.firstCompatibleParser(FunctionStatement.class));
        }
        else {
            return this.handleDelegate(parser, validator, Option.of(this.fieldParser));
        }
    }

    private <T extends Statement> T handleDelegate(final TokenParser parser, final TokenStepValidator validator,
                                                   final Option<ASTNodeParser<T>> statement) {
        return statement
                .flatMap(nodeParser -> nodeParser.parse(parser, validator))
                .attempt(ScriptEvaluationError.class)
                .rethrow()
                .orNull();
    }

    @Override
    public Set<Class<? extends ClassStatement>> types() {
        return Set.of(ClassStatement.class);
    }
}
