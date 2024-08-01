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

import java.util.function.Consumer;

import org.dockbox.hartshorn.hsl.token.TokenMetaData;
import org.dockbox.hartshorn.hsl.token.TokenMetaDataBuilder;

/**
 * Represents keyword tokens that are related to class definitions in the HSL language.
 *
 * @see TokenType
 *
 * @since 0.6.0
 *
 * @author Guus Lieben
 */
public enum ClassTokenType implements EnumTokenType {
    /**
     * The 'class' keyword representing a class definition.
     */
    CLASS,
    /**
     * The 'interface' keyword representing an interface definition.
     */
    INTERFACE,
    /**
     * The 'enum' keyword representing an enum definition.
     */
    ENUM,
    /**
     * The 'extends' keyword representing an extension of a class or interface.
     */
    EXTENDS,
    /**
     * The 'implements' keyword representing an implementation of an interface.
     */
    IMPLEMENTS,
    ;

    private final TokenMetaData metaData;

    ClassTokenType() {
        this(builder -> builder.keyword(true));
    }

    ClassTokenType(Consumer<TokenMetaDataBuilder> metaData) {
        TokenMetaDataBuilder builder = TokenMetaData.builder(this);
        metaData.accept(builder);
        this.metaData = builder.build();
    }

    @Override
    public TokenType delegate() {
        return metaData;
    }
}
