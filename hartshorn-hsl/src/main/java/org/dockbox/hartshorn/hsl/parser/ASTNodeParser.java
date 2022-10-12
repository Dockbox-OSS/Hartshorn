package org.dockbox.hartshorn.hsl.parser;

import org.dockbox.hartshorn.hsl.ast.ASTNode;
import org.dockbox.hartshorn.util.Result;

import java.util.Set;

public interface ASTNodeParser<T extends ASTNode> {
    Result<T> parse(TokenParser parser, TokenStepValidator validator);
    Set<Class<? extends T>> types();
}
