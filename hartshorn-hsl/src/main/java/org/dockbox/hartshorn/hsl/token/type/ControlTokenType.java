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
 * Represents a control token type, which is a keyword that controls the flow of a script. These
 * tokens are used to define the structure of a script, and are not used to represent values or
 * operations.
 *
 * @see TokenType
 *
 * @since 0.6.0
 *
 * @author Guus Lieben
 */
public enum ControlTokenType implements EnumTokenType {
    /**
     * Represents the 'if' keyword. 'if' keywords indicate the start of a conditional block. This
     * is a standalone statement, and is not required to be combined with other statements.
     */
    IF(true),
    /**
     * Represents the 'else' keyword. 'else' keywords indicate the start of an alternative block
     * that is executed when the condition of an 'if' statement is not met. This is not a standalone
     * statement, and is required to be combined with an 'if' statement.
     */
    ELSE(false),
    /**
     * Represents the 'switch' keyword. 'switch' keywords indicate the start of a conditional block
     * that can be used to compare a single value against multiple conditions. This is a standalone
     * statement and is not required to be combined with other statements, though it should always
     * be followed by at least one 'case' or 'default' statement.
     */
    SWITCH(true),
    /**
     * Represents the 'case' keyword. 'case' keywords indicate a condition that is compared to the
     * value of a 'switch' statement. This is not a standalone statement, and is required to be
     * combined with a 'switch' statement.
     */
    CASE(false),
    /**
     * Represents the 'break' keyword. 'break' keywords indicate the end of a block, and are used to
     * exit a loop or switch statement. This is a standalone statement and is not required to be
     * combined with other statements.
     */
    BREAK(true),
    /**
     * Represents the 'continue' keyword. 'continue' keywords indicate the end of a block, and are
     * used to skip the current iteration of a loop. This is a standalone statement and is not
     * required to be combined with other statements.
     */
    CONTINUE(true),
    /**
     * Represents the 'return' keyword. 'return' keywords indicate the end of a block, and are used
     * to exit a function and optionally return a value. This is a standalone statement and is not
     * required to be combined with other statements.
     */
    RETURN(true),
    /**
     * Represents the 'default' keyword. 'default' keywords indicate the default condition of a
     * 'switch' statement. This is not a standalone statement, and is required to be combined with a
     * 'switch' statement.
     */
    DEFAULT(false),

    /**
     * Represents the opening of a 'case' or 'default' block which only contains a single expression
     * statement. This is not a standalone statement, and is required to be combined with a 'case' or
     * 'default' statement.
     */
    ARROW(builder -> builder.combines(ArithmeticTokenType.MINUS, ConditionTokenType.GREATER).ok()),
    ;

    private final TokenMetaData metaData;

    ControlTokenType(boolean standalone) {
        this(builder -> builder
                .standaloneStatement(standalone)
                .keyword(true)
        );
    }

    ControlTokenType(Consumer<TokenMetaDataBuilder> metaData) {
        TokenMetaDataBuilder builder = TokenMetaData.builder(this);
        metaData.accept(builder);
        this.metaData = builder.ok();
    }

    @Override
    public TokenType delegate() {
        return metaData;
    }
}
