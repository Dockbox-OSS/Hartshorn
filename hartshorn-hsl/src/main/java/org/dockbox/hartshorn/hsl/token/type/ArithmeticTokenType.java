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

/**
 * Represents the different types of arithmetic tokens that can be used in the HSL language.
 *
 * @see TokenType
 *
 * @since 0.6.0
 *
 * @author Guus Lieben
 */
public enum ArithmeticTokenType implements EnumTokenType {
    /**
     * '+' token, representing addition in a {@link org.dockbox.hartshorn.hsl.ast.expression.BinaryExpression
     * binary} or {@link org.dockbox.hartshorn.hsl.ast.expression.UnaryExpression unary} expression.
     */
    PLUS(DefaultTokenCharacter.PLUS),
    /**
     * '-' token, representing subtraction in a {@link org.dockbox.hartshorn.hsl.ast.expression.BinaryExpression
     * binary} or {@link org.dockbox.hartshorn.hsl.ast.expression.UnaryExpression unary} expression.
     */
    MINUS(DefaultTokenCharacter.MINUS),
    /**
     * '*' token, representing multiplication in a {@link org.dockbox.hartshorn.hsl.ast.expression.BinaryExpression
     * binary} expression.
     */
    STAR(DefaultTokenCharacter.STAR),
    /**
     * '/' token, representing division in a {@link org.dockbox.hartshorn.hsl.ast.expression.BinaryExpression
     * binary} expression.
     */
    SLASH(DefaultTokenCharacter.SLASH),
    /**
     * '%' token, representing modulo in a {@link org.dockbox.hartshorn.hsl.ast.expression.BinaryExpression
     * binary} expression.
     */
    MODULO(DefaultTokenCharacter.MODULO),

    /**
     * '++' token, representing increment in a {@link org.dockbox.hartshorn.hsl.ast.expression.UnaryExpression
     * unary} or {@link org.dockbox.hartshorn.hsl.ast.expression.PostfixExpression postfix} expression.
     */
    PLUS_PLUS(builder -> builder.repeats(PLUS)),
    /**
     * '--' token, representing decrement in a {@link org.dockbox.hartshorn.hsl.ast.expression.UnaryExpression
     * unary} or {@link org.dockbox.hartshorn.hsl.ast.expression.PostfixExpression postfix} expression.
     */
    MINUS_MINUS(builder -> builder.repeats(MINUS)),
    ;

    private final TokenMetaData metaData;

    ArithmeticTokenType(DefaultTokenCharacter character) {
        this(builder -> builder
                .representation(String.valueOf(character.character()))
                .characters(character)
        );
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
