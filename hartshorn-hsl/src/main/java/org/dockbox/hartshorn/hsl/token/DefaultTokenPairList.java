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

import org.dockbox.hartshorn.hsl.token.type.PairTokenType;
import org.dockbox.hartshorn.hsl.token.type.TokenTypePair;

/**
 * Default implementation of {@link TokenPairList} using {@link PairTokenType} definitions.
 *
 * @see PairTokenType
 *
 * @since 0.6.0
 *
 * @author Guus Lieben
 */
public final class DefaultTokenPairList implements TokenPairList {

    @Override
    public TokenTypePair block() {
        return PairTokenType.LEFT_BRACE.pair();
    }

    @Override
    public TokenTypePair parameters() {
        return PairTokenType.LEFT_PAREN.pair();
    }

    @Override
    public TokenTypePair array() {
        return PairTokenType.ARRAY_OPEN.pair();
    }
}
