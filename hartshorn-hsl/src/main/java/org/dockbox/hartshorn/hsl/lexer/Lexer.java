/*
 * Copyright 2019-2023 the original author or authors.
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

package org.dockbox.hartshorn.hsl.lexer;

import java.util.List;

import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.type.LiteralTokenType;

public interface Lexer {

    /**
     * Transforms the configured source into valid {@link Token}s. If an invalid token is
     * encountered, an error is reported. When an error is reported, the lexer will attempt
     * to proceed to the next token, skipping the invalid token(s). The collection of
     * tokens will always end with a single {@link LiteralTokenType#EOF EndOfFile token}.
     *
     * @return The scanned tokens.
     */
    List<Token> scanTokens();

    List<Comment> comments();
}
