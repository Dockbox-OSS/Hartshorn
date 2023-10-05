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

import java.util.function.Consumer;

import org.dockbox.hartshorn.hsl.token.TokenMetaData;
import org.dockbox.hartshorn.hsl.token.TokenMetaDataBuilder;

public enum FunctionTokenType implements EnumTokenType {
    FUNCTION(false),
    CONSTRUCTOR(builder -> builder.keyword(true).standaloneStatement(false).defaultLexeme("<init>")),

    PREFIX(false),
    INFIX(false),
    NATIVE(false),
    OPERATOR(false),
    OVERRIDE(false),
    ;

    private final TokenMetaData metaData;

    FunctionTokenType(boolean standalone) {
        this(builder -> builder.keyword(true).standaloneStatement(standalone));
    }

    FunctionTokenType(Consumer<TokenMetaDataBuilder> metaData) {
        TokenMetaDataBuilder builder = TokenMetaData.builder(this);
        metaData.accept(builder);
        this.metaData = builder.ok();
    }

    @Override
    public TokenType delegate() {
        return metaData;
    }
}
