package org.dockbox.hartshorn.hsl.token;

import org.dockbox.hartshorn.hsl.token.type.TokenType;
import org.dockbox.hartshorn.util.collections.MultiMap;
import org.dockbox.hartshorn.util.option.Option;

public interface CommentTokenList {

    MultiMap<CommentType, TokenType> commentTypes();

    Option<CommentType> commentType(TokenType tokenType);

    public enum CommentType {
        LINE,
        BLOCK,
    }
}
