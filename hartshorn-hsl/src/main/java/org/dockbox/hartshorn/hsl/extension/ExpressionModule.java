package org.dockbox.hartshorn.hsl.extension;

import org.dockbox.hartshorn.hsl.ast.expression.Expression;

public non-sealed interface ExpressionModule<T extends Expression & CustomASTNode<T, Object>> extends ASTExtensionModule<T, Object> {

}
