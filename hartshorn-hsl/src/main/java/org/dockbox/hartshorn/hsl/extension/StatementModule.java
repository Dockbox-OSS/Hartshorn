package org.dockbox.hartshorn.hsl.extension;

import org.dockbox.hartshorn.hsl.ast.statement.Statement;

public non-sealed interface StatementModule<T extends Statement & CustomASTNode<T, Void>> extends ASTExtensionModule<T, Void> {

}
