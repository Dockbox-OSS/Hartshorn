package org.dockbox.hartshorn.hsl.extension;

import org.dockbox.hartshorn.hsl.ast.ASTNode;
import org.dockbox.hartshorn.hsl.interpreter.ASTNodeInterpreter;
import org.dockbox.hartshorn.hsl.parser.ASTNodeParser;
import org.dockbox.hartshorn.hsl.token.type.TokenType;

public sealed interface ASTExtensionModule<T extends ASTNode & CustomASTNode<T, R>, R> permits ExpressionModule, StatementModule {

    TokenType tokenType();

    ASTNodeParser<T> parser();

    default ResolverExtension<T> resolver() {
        return (node, resolver) -> {};
    }

    ASTNodeInterpreter<R, T> interpreter();

}
