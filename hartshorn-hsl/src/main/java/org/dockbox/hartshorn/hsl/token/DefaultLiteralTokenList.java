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

import java.util.Set;

import org.dockbox.hartshorn.hsl.token.type.LiteralTokenType;
import org.dockbox.hartshorn.hsl.token.type.TokenType;

/**
 * Default implementation of {@link LiteralTokenList} using {@link LiteralTokenType}.
 *
 * @see LiteralTokenType
 *
 * @since 0.6.0
 *
 * @author Guus Lieben
 */
public final class DefaultLiteralTokenList implements LiteralTokenList {

    @Override
    public Set<TokenType> literals() {
        return Set.of(LiteralTokenType.values());
    }

    @Override
    public TokenType eof() {
        return LiteralTokenType.EOF;
    }

    @Override
    public TokenType identifier() {
        return LiteralTokenType.IDENTIFIER;
    }

    @Override
    public TokenType string() {
        return LiteralTokenType.STRING;
    }

    @Override
    public TokenType character() {
        return LiteralTokenType.CHAR;
    }

    @Override
    public TokenType number() {
        return LiteralTokenType.NUMBER;
    }

    @Override
    public TokenType nullLiteral() {
        return LiteralTokenType.NULL;
    }
}
