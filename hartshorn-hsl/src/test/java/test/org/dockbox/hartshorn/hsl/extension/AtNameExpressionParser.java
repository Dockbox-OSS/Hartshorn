package test.org.dockbox.hartshorn.hsl.extension;

import java.util.Set;

import org.dockbox.hartshorn.hsl.parser.ASTNodeParser;
import org.dockbox.hartshorn.hsl.parser.TokenParser;
import org.dockbox.hartshorn.hsl.parser.TokenStepValidator;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.type.LiteralTokenType;
import org.dockbox.hartshorn.util.option.Option;

class AtNameExpressionParser implements ASTNodeParser<AtNameExpression> {

    private final AtNameModule atNameModule;

    public AtNameExpressionParser(AtNameModule atNameModule) {
        this.atNameModule = atNameModule;
    }

    @Override
    public Option<? extends AtNameExpression> parse(TokenParser parser, TokenStepValidator validator) {
        if(parser.match(atNameModule.tokenType())) {
            Token identifier = validator.expectAfter(LiteralTokenType.IDENTIFIER, "component lookup");
            return Option.of(new AtNameExpression(parser.previous(), atNameModule, identifier));
        }
        return Option.empty();
    }

    @Override
    public Set<Class<? extends AtNameExpression>> types() {
        return Set.of(AtNameExpression.class);
    }
}
