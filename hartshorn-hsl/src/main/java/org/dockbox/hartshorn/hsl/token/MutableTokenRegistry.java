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

import org.dockbox.hartshorn.hsl.token.type.TokenType;

/**
 * Represents a registry of tokens that can be mutated.
 *
 * @since 0.6.0
 *
 * @see TokenRegistry
 *
 * @author Guus Lieben
 */
public interface MutableTokenRegistry extends TokenRegistry {

    /**
     * Adds the given token types to the registry.
     *
     * @param types The token types to add
     */
    void addTokens(TokenType... types);
}
