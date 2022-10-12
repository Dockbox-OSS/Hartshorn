package org.dockbox.hartshorn.hsl.parser.statement;

import org.dockbox.hartshorn.hsl.ScriptEvaluationError;
import org.dockbox.hartshorn.hsl.ast.statement.FinalizableStatement;
import org.dockbox.hartshorn.hsl.ast.statement.VariableStatement;
import org.dockbox.hartshorn.hsl.parser.ASTNodeParser;
import org.dockbox.hartshorn.hsl.parser.TokenParser;
import org.dockbox.hartshorn.hsl.parser.TokenStepValidator;
import org.dockbox.hartshorn.hsl.runtime.Phase;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.TokenType;
import org.dockbox.hartshorn.util.Result;

import java.util.Set;

public class FinalDeclarationStatementParser implements ASTNodeParser<FinalizableStatement> {

    @Override
    public Result<FinalizableStatement> parse(final TokenParser parser, final TokenStepValidator validator) {
        final FinalizableStatement finalizable;

        final Token current = parser.peek();

        if (parser.match(TokenType.PREFIX, TokenType.INFIX) && parser.match(TokenType.FUN)) finalizable = this.funcDeclaration(current);
        else if (parser.match(TokenType.FUN)) finalizable = this.funcDeclaration(parser.previous());
        else if (parser.check(TokenType.VAR)) {
            finalizable = parser.firstCompatibleParser(VariableStatement.class)
                    .flatMap(nodeParser -> nodeParser.parse(parser, validator))
                    .orThrow(() -> new ScriptEvaluationError("Failed to parse variable statement", Phase.PARSING, current));
        }
        else if (parser.match(TokenType.CLASS)) finalizable = this.classDeclaration();
        else if (parser.match(TokenType.NATIVE)) finalizable = this.nativeFuncDeclaration();
        else throw new ScriptEvaluationError("Illegal use of %s. Expected valid keyword to follow, but got %s".formatted(TokenType.FINAL.representation(), current.type()), Phase.PARSING, current);

        finalizable.makeFinal();
        return Result.of(finalizable);
    }

    @Override
    public Set<Class<? extends FinalizableStatement>> types() {
        return Set.of(FinalizableStatement.class);
    }
}
