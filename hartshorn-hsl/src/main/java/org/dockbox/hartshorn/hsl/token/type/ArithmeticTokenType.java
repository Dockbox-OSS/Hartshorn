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

package org.dockbox.hartshorn.hsl.token.type;

import org.dockbox.hartshorn.hsl.token.DefaultTokenCharacter;
import org.dockbox.hartshorn.hsl.token.TokenMetaData;
import org.dockbox.hartshorn.hsl.token.TokenMetaDataBuilder;
import org.dockbox.hartshorn.util.Customizer;

public enum ArithmeticTokenType implements EnumTokenType {
    PLUS(DefaultTokenCharacter.PLUS),
    MINUS(DefaultTokenCharacter.MINUS),
    STAR(DefaultTokenCharacter.STAR),
    SLASH(DefaultTokenCharacter.SLASH),
    MODULO(DefaultTokenCharacter.MODULO),

    PLUS_PLUS(builder -> builder.repeats(PLUS)),
    MINUS_MINUS(builder -> builder.repeats(MINUS)),
    ;

    private final TokenMetaData metaData;

    ArithmeticTokenType(DefaultTokenCharacter character) {
        this(builder -> builder.representation(String.valueOf(character.character())).ok());
    }

    ArithmeticTokenType(Customizer<TokenMetaDataBuilder> customizer) {
        TokenMetaDataBuilder builder = TokenMetaData.builder(this);
        customizer.configure(builder);
        this.metaData = builder.ok();
    }

    @Override
    public TokenType delegate() {
        return metaData;
    }
}
