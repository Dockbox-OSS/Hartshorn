package org.dockbox.hartshorn.hsl.extension;

import org.dockbox.hartshorn.hsl.ast.ASTNode;
import org.dockbox.hartshorn.hsl.interpreter.InterpreterAdapter;
import org.dockbox.hartshorn.hsl.semantic.Resolver;

public sealed interface CustomASTNode<T extends ASTNode & CustomASTNode<T, R>, R> permits CustomExpression, CustomStatement {

    ASTExtensionModule<T, R> module();

    default R interpret(InterpreterAdapter adapter) {
        return this.module().interpreter().interpret((T) this, adapter);
    }

    default void resolve(Resolver resolver) {
        this.module().resolver().resolve((T) this, resolver);
    }
}
