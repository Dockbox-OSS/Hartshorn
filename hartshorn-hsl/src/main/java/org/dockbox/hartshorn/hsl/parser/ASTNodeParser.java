package org.dockbox.hartshorn.hsl.parser;

import java.util.Set;

import org.dockbox.hartshorn.hsl.ast.ASTNode;
import org.dockbox.hartshorn.util.option.Option;

public interface ASTNodeParser<T extends ASTNode> {
    Option<T> parse(TokenParser parser, TokenStepValidator validator);
    Set<Class<? extends T>> types();
}
