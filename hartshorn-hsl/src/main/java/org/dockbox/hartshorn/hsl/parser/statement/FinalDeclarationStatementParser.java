package org.dockbox.hartshorn.hsl.parser.statement;

import java.util.Set;

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
import org.dockbox.hartshorn.hsl.token.TokenType;
import org.dockbox.hartshorn.util.option.Option;
import org.jetbrains.annotations.NotNull;

public class FinalDeclarationStatementParser implements ASTNodeParser<FinalizableStatement> {

    @Override
    public Option<FinalizableStatement> parse(final TokenParser parser, final TokenStepValidator validator) {
        if (parser.match(TokenType.FINAL)) {
            final Token current = parser.peek();
            final FinalizableStatement finalizable = switch (current.type()) {
                case PREFIX, INFIX -> {
                    parser.advance();
                    if (parser.check(TokenType.FUN)) {
                        yield lookupFinalizableFunction(parser, validator, parser.peek());
                    }
                    else {
                        throw new ScriptEvaluationError("Unexpected token '" + current.lexeme() + "' at line " + current.line() + ", column " + current.column(), Phase.PARSING, current);
                    }
                }
                case FUN -> lookupFinalizableFunction(parser, validator, current);
                case VAR -> delegateParseStatement(parser, validator, VariableStatement.class, "variable", current);
                case CLASS -> delegateParseStatement(parser, validator, ClassStatement.class, "class", current);
                case NATIVE -> delegateParseStatement(parser, validator, NativeFunctionStatement.class, "native function", current);
                default -> throw new ScriptEvaluationError("Illegal use of %s. Expected valid keyword to follow, but got %s".formatted(TokenType.FINAL.representation(), current.type()), Phase.PARSING, current);
            };
            return Option.of(finalizable).peek(FinalizableStatement::makeFinal);
        }
        return Option.empty();
    }

    @NotNull
    private static FinalizableStatement delegateParseStatement(TokenParser parser, TokenStepValidator validator, Class<? extends FinalizableStatement> statement, String statementType, Token current) {
        return parser.firstCompatibleParser(statement)
                .flatMap(nodeParser -> nodeParser.parse(parser, validator))
                .orElseThrow(() -> new ScriptEvaluationError("Failed to parse %s statement".formatted(statementType), Phase.PARSING, current));
    }

    @NotNull
    private static Function lookupFinalizableFunction(final TokenParser parser, final TokenStepValidator validator, final Token current) {
        return parser.firstCompatibleParser(Function.class)
                .flatMap(functionParser -> functionParser.parse(parser, validator))
                .orElseThrow(() -> new ScriptEvaluationError("Failed to parse function", Phase.PARSING, current));
    }

    @Override
    public Set<Class<? extends FinalizableStatement>> types() {
        return Set.of(FinalizableStatement.class);
    }
}
