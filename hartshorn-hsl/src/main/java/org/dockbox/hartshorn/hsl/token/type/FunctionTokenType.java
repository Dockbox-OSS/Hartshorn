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

/**
 * Represents various token types that are related to functions, such as constructors, operators, and overrides.
 *
 * @see TokenType
 *
 * @since 0.6.0
 *
 * @author Guus Lieben
 */
public enum FunctionTokenType implements EnumTokenType {

    /**
     * Represents a function, which should be part of a class or interface.
     */
    FUNCTION(false),
    /**
     * Represents the constructor of a class. Constructors are unnamed initializer functions that are used
     * to create new instances of a class. As constructors are unnamed, they are represented by the default
     * lexical meaning '{@code <init>}'.
     */
    CONSTRUCTOR(builder -> builder.keyword(true).standaloneStatement(false).defaultLexeme("<init>")),

    /**
     * Represents a prefix function, which is a function that is used as a prefix to an expression. Prefix
     * functions are comparable to unary operators, but are not limited to the set of predefined operators.
     */
    PREFIX(false),
    /**
     * Represents an infix function, which is a function that is used as an infix to an expression. Infix
     * functions are comparable to binary operators, but are not limited to the set of predefined operators.
     */
    INFIX(false),
    /**
     * Represents a native function, which is a function that is implemented in the runtime environment, and
     * is not part of the script itself.
     */
    NATIVE(false),
    /**
     * Represents an operator function, which is a function that is used to override the behavior of an operator
     * for a specific type.
     */
    OPERATOR(false),
    /**
     * Represents an override function, which is a function that is used to override the behavior of a function
     * that is defined in a superclass or interface.
     */
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
