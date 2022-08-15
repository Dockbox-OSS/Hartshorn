package org.dockbox.hartshorn.hsl.ast.statement;

import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.TokenType;

public interface MemberStatement {
    Token name();

    Token modifier();

    default boolean isPublic() {
        return this.modifier() == null || this.modifier().type() == TokenType.PUBLIC;
    }

    default boolean isPrivate() {
        return this.modifier() != null && this.modifier().type() == TokenType.PRIVATE;
    }
}
