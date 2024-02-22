/*
 * Copyright 2019-2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dockbox.hartshorn.hsl.token;

import java.util.Collection;
import java.util.EnumMap;
import java.util.LinkedHashSet;

import org.dockbox.hartshorn.hsl.token.type.SimpleTokenType;
import org.dockbox.hartshorn.hsl.token.type.TokenType;
import org.dockbox.hartshorn.hsl.token.type.TokenTypePair;
import org.dockbox.hartshorn.util.collections.MultiMap;
import org.dockbox.hartshorn.util.option.Option;

public final class DefaultCommentTokenList implements CommentTokenList {

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

    private static final TokenType BLOCK_COMMENT_END = buildCommentType(
            "BLOCK_COMMENT_END",
            DefaultTokenCharacter.STAR, DefaultTokenCharacter.SLASH
    );

    private static final MultiMap<CommentType, TokenTypePair> COMMENT_TYPES = MultiMap.<CommentType, TokenTypePair>builder()
            .mapSupplier(() -> new EnumMap<>(CommentType.class))
            .collectionSupplier(LinkedHashSet::new)
            .build();

    static {
        COMMENT_TYPES.put(CommentType.LINE, new TokenTypePair(LINE_COMMENT_START, null));
        COMMENT_TYPES.put(CommentType.LINE, new TokenTypePair(HASH_COMMENT_START, null));
        COMMENT_TYPES.put(CommentType.BLOCK, new TokenTypePair(BLOCK_COMMENT_START, BLOCK_COMMENT_END));
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
    public MultiMap<CommentType, TokenTypePair> commentTypes() {
        return COMMENT_TYPES;
    }

    @Override
    public Option<CommentType> resolveFromOpenToken(TokenType tokenType) {
        return Option.of(COMMENT_TYPES.keySet().stream()
                .filter(type -> {
                    Collection<TokenTypePair> tokenTypePairs = COMMENT_TYPES.get(type);
                    return tokenTypePairs.stream().anyMatch(pair -> pair.open().equals(tokenType));
                })
                .findFirst());
    }

    @Override
    public Option<TokenTypePair> resolveTokenPairFromOpen(TokenType tokenType) {
        return Option.of(COMMENT_TYPES.allValues().stream()
                .filter(pair -> pair.open().equals(tokenType))
                .findFirst());
    }
}
