package org.dockbox.hartshorn.hsl.extension;

import org.dockbox.hartshorn.hsl.ast.ASTNode;
import org.dockbox.hartshorn.hsl.semantic.Resolver;

public interface ResolverExtension<T extends ASTNode & CustomASTNode<?, ?>> {
    void resolve(T node, Resolver resolver);
}
