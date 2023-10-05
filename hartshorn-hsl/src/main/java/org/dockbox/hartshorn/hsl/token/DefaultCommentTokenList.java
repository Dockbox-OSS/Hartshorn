package org.dockbox.hartshorn.hsl.token;

import java.util.EnumMap;
import java.util.LinkedHashSet;

import org.dockbox.hartshorn.hsl.token.type.SimpleTokenType;
import org.dockbox.hartshorn.hsl.token.type.TokenType;
import org.dockbox.hartshorn.util.collections.MultiMap;
import org.dockbox.hartshorn.util.option.Option;

public class DefaultCommentTokenList implements CommentTokenList {

    public static final DefaultCommentTokenList INSTANCE = new DefaultCommentTokenList();

    private static final TokenType LINE_COMMENT_START = buildCommentType(
            "LINE_COMMENT_START",
            DefaultTokenCharacter.SLASH, DefaultTokenCharacter.SLASH
    );

    private static final TokenType HASH_COMMENT_START = buildCommentType(
            "LINE_COMMENT_START",
            DefaultTokenCharacter.HASH
    );

    private static final TokenType BLOCK_COMMENT_START = buildCommentType(
            "BLOCK_COMMENT_START",
            DefaultTokenCharacter.SLASH, DefaultTokenCharacter.STAR
    );

    private static final MultiMap<CommentType, TokenType> COMMENT_TYPES = MultiMap.<CommentType, TokenType>builder()
            .mapSupplier(() -> new EnumMap<>(CommentType.class))
            .collectionSupplier(LinkedHashSet::new)
            .build();

    static {
        COMMENT_TYPES.put(CommentType.LINE, LINE_COMMENT_START);
        COMMENT_TYPES.put(CommentType.LINE, HASH_COMMENT_START);
        COMMENT_TYPES.put(CommentType.BLOCK, BLOCK_COMMENT_START);
    }

    private static TokenType buildCommentType(String name, TokenCharacter... characters) {
        return SimpleTokenType.builder()
                .tokenName(name)
                .keyword(false)
                .standaloneStatement(false)
                .reserved(false)
                .assignsWith(null)
                .defaultLexeme(null)
                .characters(characters)
                .build();
    }

    @Override
    public MultiMap<CommentType, TokenType> commentTypes() {
        return COMMENT_TYPES;
    }

    @Override
    public Option<CommentType> commentType(TokenType tokenType) {
        return Option.of(COMMENT_TYPES.keySet().stream()
                .filter(type -> COMMENT_TYPES.get(type).contains(tokenType))
                .findFirst());
    }
}
