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
import org.dockbox.hartshorn.hsl.ast.statement.ClassStatement;
import org.dockbox.hartshorn.hsl.ast.statement.FinalizableStatement;
import org.dockbox.hartshorn.hsl.ast.statement.Function;
import org.dockbox.hartshorn.hsl.ast.statement.NativeFunctionStatement;
import org.dockbox.hartshorn.hsl.ast.statement.VariableStatement;
import org.dockbox.hartshorn.hsl.parser.TokenParser;
import org.dockbox.hartshorn.hsl.parser.TokenStepValidator;
import org.dockbox.hartshorn.hsl.runtime.DiagnosticMessage;
import org.dockbox.hartshorn.hsl.runtime.Phase;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.type.ClassTokenType;
import org.dockbox.hartshorn.hsl.token.type.FunctionTokenType;
import org.dockbox.hartshorn.hsl.token.type.MemberModifierTokenType;
import org.dockbox.hartshorn.hsl.token.type.VariableTokenType;
import org.dockbox.hartshorn.util.option.Option;
import org.jspecify.annotations.NonNull;

import java.util.Set;

/**
 * A parser for {@link FinalizableStatement} nodes, supporting final declarations for functions,
 * variables, and classes.
 *
 * @since 0.4.13
 *
 * @author Guus Lieben
 */
public class FinalDeclarationStatementParser implements StatementParser<FinalizableStatement> {

    @Override
    public Option<? extends FinalizableStatement> parse(
        TokenParser parser,
        TokenStepValidator validator
    ) {
        if (parser.match(MemberModifierTokenType.FINAL)) {

            Token current = parser.peek();
            FinalizableStatement finalizable;
            if (current.type() instanceof FunctionTokenType functionTokenType) {
                finalizable = switch (functionTokenType) {
                    case PREFIX, INFIX -> {
                        parser.advance();
                        if (parser.check(FunctionTokenType.FUNCTION)) {
                            yield lookupFinalizableFunction(parser, validator, parser.peek());
                        }
                        else {
                            throw ScriptEvaluationError.builder(Phase.PARSING)
                                .message(DiagnosticMessage.UNEXPECTED_TOKEN, current.lexeme())
                                .at(current)
                                .build();
                        }
                    }
                    case FUNCTION -> lookupFinalizableFunction(parser, validator, current);
                    case NATIVE -> delegateParseStatement(parser,
                        validator,
                        NativeFunctionStatement.class,
                        "native function",
                        current);
                    default -> throw ScriptEvaluationError.builder(Phase.PARSING)
                        .message(DiagnosticMessage.ILLEGAL_USE_OF_X,
                            MemberModifierTokenType.FINAL.representation(),
                            current.type())
                        .at(current)
                        .build();
                };
            }
            else if (current.type() == VariableTokenType.VAR) {
                finalizable = delegateParseStatement(parser,
                    validator,
                    VariableStatement.class,
                    "variable",
                    current);
            }
            else if (current.type() == ClassTokenType.CLASS) {
                finalizable = delegateParseStatement(parser,
                    validator,
                    ClassStatement.class,
                    "class",
                    current);
            }
            else {
                throw ScriptEvaluationError.builder(Phase.PARSING)
                    .message(DiagnosticMessage.ILLEGAL_USE_OF_X,
                        MemberModifierTokenType.FINAL.representation(),
                        current.type())
                    .at(current)
                    .build();
            }
            return Option.of(finalizable).peek(FinalizableStatement::makeFinal);
        }
        return Option.empty();
    }

    @NonNull
    private static FinalizableStatement delegateParseStatement(
        TokenParser parser,
        TokenStepValidator validator,
        Class<? extends FinalizableStatement> statement,
        String statementType,
        Token current
    ) {
        return parser.firstCompatibleParser(statement)
            .flatMap(nodeParser -> nodeParser.parse(parser, validator))
            .orElseThrow(() -> ScriptEvaluationError.builder(Phase.PARSING)
                .message(DiagnosticMessage.FAILED_TO_PARSE_X_STATEMENT, statementType)
                .at(current)
                .build());
    }

    @NonNull
    private static Function lookupFinalizableFunction(
        TokenParser parser,
        TokenStepValidator validator,
        Token current
    ) {
        return parser.firstCompatibleParser(Function.class)
            .flatMap(functionParser -> functionParser.parse(parser, validator))
            .orElseThrow(() -> ScriptEvaluationError.builder(Phase.PARSING)
                .message(DiagnosticMessage.FAILED_TO_PARSE_X_STATEMENT, "function")
                .at(current)
                .build());
    }

    @Override
    public Set<Class<? extends FinalizableStatement>> types() {
        return Set.of(FinalizableStatement.class);
    }
}
