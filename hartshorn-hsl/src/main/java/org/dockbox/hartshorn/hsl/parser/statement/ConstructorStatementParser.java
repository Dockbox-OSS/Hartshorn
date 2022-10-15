package org.dockbox.hartshorn.hsl.parser.statement;

import org.dockbox.hartshorn.hsl.ast.statement.BlockStatement;
import org.dockbox.hartshorn.hsl.ast.statement.ConstructorStatement;
import org.dockbox.hartshorn.hsl.ast.statement.ParametricExecutableStatement.Parameter;
import org.dockbox.hartshorn.hsl.parser.TokenParser;
import org.dockbox.hartshorn.hsl.parser.TokenStepValidator;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.TokenType;
import org.dockbox.hartshorn.util.Result;

import java.util.List;
import java.util.Set;

public class ConstructorStatementParser extends AbstractBodyStatementParser<ConstructorStatement> implements ParametricStatementParser {

    @Override
    public Result<ConstructorStatement> parse(final TokenParser parser, final TokenStepValidator validator) {
        final Token keyword = parser.peek();
        if (keyword.type() == TokenType.CONSTRUCTOR) {
            parser.advance();
            final List<Parameter> parameters = this.parameters(parser, validator, "constructor", Integer.MAX_VALUE, keyword.type());
            final BlockStatement body = this.blockStatement("constructor", keyword, parser, validator);
            return Result.of(new ConstructorStatement(keyword, parameters, body));
        }
        return Result.empty();
    }

    @Override
    public Set<Class<? extends ConstructorStatement>> types() {
        return Set.of(ConstructorStatement.class);
    }
}
