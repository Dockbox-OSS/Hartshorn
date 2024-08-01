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

import java.util.Set;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.dockbox.hartshorn.hsl.ScriptEvaluationError;
import org.dockbox.hartshorn.hsl.ast.statement.ClassStatement;
import org.dockbox.hartshorn.hsl.ast.statement.FinalizableStatement;
import org.dockbox.hartshorn.hsl.ast.statement.Function;
import org.dockbox.hartshorn.hsl.ast.statement.NativeFunctionStatement;
import org.dockbox.hartshorn.hsl.ast.statement.VariableStatement;
import org.dockbox.hartshorn.hsl.parser.ASTNodeParser;
import org.dockbox.hartshorn.hsl.parser.TokenParser;
import org.dockbox.hartshorn.hsl.parser.TokenStepValidator;
import org.dockbox.hartshorn.hsl.runtime.Phase;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.type.ClassTokenType;
import org.dockbox.hartshorn.hsl.token.type.FunctionTokenType;
import org.dockbox.hartshorn.hsl.token.type.MemberModifierTokenType;
import org.dockbox.hartshorn.hsl.token.type.VariableTokenType;
import org.dockbox.hartshorn.util.option.Option;

/**
 * TODO: #1061 Add documentation
 *
 * @since 0.4.13
 *
 * @author Guus Lieben
 */
public class FinalDeclarationStatementParser implements ASTNodeParser<FinalizableStatement> {

    @Override
    public Option<? extends FinalizableStatement> parse(TokenParser parser, TokenStepValidator validator) {
        if (parser.match(MemberModifierTokenType.FINAL)) {

            Token current = parser.peek();
            FinalizableStatement finalizable;
            if (current.type() instanceof FunctionTokenType functionTokenType) {
                 finalizable = switch(functionTokenType) {
                    case PREFIX, INFIX -> {
                        parser.advance();
                        if(parser.check(FunctionTokenType.FUNCTION)) {
                            yield lookupFinalizableFunction(parser, validator, parser.peek());
                        }
                        else {
                            throw new ScriptEvaluationError(
                                    "Unexpected token '" + current.lexeme() + "' at line " + current.line() + ", column "
                                            + current.column(), Phase.PARSING, current);
                        }
                    }
                    case FUNCTION -> lookupFinalizableFunction(parser, validator, current);
                    case NATIVE -> delegateParseStatement(parser, validator, NativeFunctionStatement.class, "native function", current);
                    default -> throw new ScriptEvaluationError("Illegal use of %s. Expected valid keyword to follow, but got %s".formatted(
                            MemberModifierTokenType.FINAL.representation(), current.type()), Phase.PARSING, current);
                };
            }
            else if (current.type() == VariableTokenType.VAR) {
                finalizable = delegateParseStatement(parser, validator, VariableStatement.class, "variable", current);
            }
            else if (current.type() == ClassTokenType.CLASS) {
                finalizable = delegateParseStatement(parser, validator, ClassStatement.class, "class", current);
            }
            else {
                throw new ScriptEvaluationError("Illegal use of %s. Expected valid keyword to follow, but got %s".formatted(
                        MemberModifierTokenType.FINAL.representation(), current.type()), Phase.PARSING, current);
            }
            return Option.of(finalizable).peek(FinalizableStatement::makeFinal);
        }
        return Option.empty();
    }

    @NonNull
    private static FinalizableStatement delegateParseStatement(TokenParser parser, TokenStepValidator validator, Class<? extends FinalizableStatement> statement, String statementType, Token current) {
        return parser.firstCompatibleParser(statement)
                .flatMap(nodeParser -> nodeParser.parse(parser, validator))
                .orElseThrow(() -> new ScriptEvaluationError("Failed to parse %s statement".formatted(statementType), Phase.PARSING, current));
    }

    @NonNull
    private static Function lookupFinalizableFunction(TokenParser parser, TokenStepValidator validator, Token current) {
        return parser.firstCompatibleParser(Function.class)
                .flatMap(functionParser -> functionParser.parse(parser, validator))
                .orElseThrow(() -> new ScriptEvaluationError("Failed to parse function", Phase.PARSING, current));
    }

    @Override
    public Set<Class<? extends FinalizableStatement>> types() {
        return Set.of(FinalizableStatement.class);
    }
}
