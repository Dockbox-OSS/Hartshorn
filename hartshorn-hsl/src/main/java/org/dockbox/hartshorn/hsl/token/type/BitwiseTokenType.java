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

package org.dockbox.hartshorn.hsl.token.type;

import org.dockbox.hartshorn.hsl.token.DefaultTokenCharacter;
import org.dockbox.hartshorn.hsl.token.TokenMetaData;
import org.dockbox.hartshorn.hsl.token.TokenMetaDataBuilder;
import org.dockbox.hartshorn.util.Customizer;

public enum BitwiseTokenType implements EnumTokenType {
    XOR(DefaultTokenCharacter.CARET),
    BITWISE_AND(DefaultTokenCharacter.AMPERSAND),
    BITWISE_OR(DefaultTokenCharacter.PIPE),
    COMPLEMENT(DefaultTokenCharacter.TILDE),

    SHIFT_RIGHT(builder -> builder.repeats(DefaultTokenCharacter.GREATER)),
    SHIFT_LEFT(builder -> builder.repeats(DefaultTokenCharacter.LESS)),
    LOGICAL_SHIFT_RIGHT(builder -> builder.combines(DefaultTokenCharacter.GREATER, DefaultTokenCharacter.GREATER, DefaultTokenCharacter.GREATER)),

    ;

    private final TokenMetaData metaData;

    BitwiseTokenType(DefaultTokenCharacter character) {
        this(builder -> builder
                .representation(String.valueOf(character.character()))
                .characters(character)
        );
    }

    BitwiseTokenType(Customizer<TokenMetaDataBuilder> customizer) {
        TokenMetaDataBuilder builder = TokenMetaData.builder(this);
        customizer.configure(builder);
        this.metaData = builder.ok();
    }

    @Override
    public TokenType delegate() {
        return metaData;
    }
}
