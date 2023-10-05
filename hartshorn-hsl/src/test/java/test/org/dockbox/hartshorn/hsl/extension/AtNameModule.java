package test.org.dockbox.hartshorn.hsl.extension;

import org.dockbox.hartshorn.hsl.interpreter.ASTNodeInterpreter;
import org.dockbox.hartshorn.hsl.parser.ASTNodeParser;
import org.dockbox.hartshorn.hsl.extension.ExpressionModule;
import org.dockbox.hartshorn.hsl.token.SimpleTokenCharacter;
import org.dockbox.hartshorn.hsl.token.type.SimpleTokenType;
import org.dockbox.hartshorn.hsl.token.type.TokenType;

public class AtNameModule implements ExpressionModule<AtNameExpression> {

    public static final TokenType TOKEN_TYPE = SimpleTokenType.builder()
            .characters(SimpleTokenCharacter.of('@', true))
            .tokenName("at")
            .build();

    @Override
    public TokenType tokenType() {
        return TOKEN_TYPE;
    }

    @Override
    public ASTNodeParser<AtNameExpression> parser() {
        return new AtNameExpressionParser(this);
    }

    @Override
    public ASTNodeInterpreter<Object, AtNameExpression> interpreter() {
        return (node, adapter) -> node.identifier().lexeme();
    }
}
