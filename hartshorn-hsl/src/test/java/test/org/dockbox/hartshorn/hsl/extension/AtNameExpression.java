package test.org.dockbox.hartshorn.hsl.extension;

import org.dockbox.hartshorn.hsl.ast.ASTNode;
import org.dockbox.hartshorn.hsl.extension.CustomExpression;
import org.dockbox.hartshorn.hsl.token.Token;

public class AtNameExpression extends CustomExpression<AtNameExpression> {

    private final Token identifier;

    protected AtNameExpression(ASTNode at, AtNameModule module, Token identifier) {
        super(at, module);
        this.identifier = identifier;
    }

    public Token identifier() {
        return this.identifier;
    }
}
