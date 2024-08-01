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

import org.dockbox.hartshorn.hsl.token.TokenMetaData;

/**
 * Represents the different types of exception handling tokens.
 *
 * <b>Note</b>: Exception handling is a feature that is not yet implemented in the HSL language,
 * but is planned for a future release. This enum is a placeholder for that feature. All tokens
 * in this enum are reserved keywords that will not be used in the current version of the language.
 *
 * @see TokenType
 *
 * @since 0.6.0
 *
 * @author Guus Lieben
 */
public enum ExceptionTokenType implements EnumTokenType {
    /**
     * The 'throw' keyword, used to throw an exception. This is a standalone statement, and
     * can be used anywhere in the script to indicate a problem.
     */
    THROW(true),
    /**
     * The 'try' keyword, used to start a try-catch block. This is a standalone statement, and
     * can be used anywhere in the script to indicate that a block of code should be executed
     * and that exceptions may be thrown during that execution. This does not require a catch-
     * or finally block, but it is recommended to have at least one of the two.
     */
    TRY(true),
    /**
     * The 'catch' keyword, used to catch exceptions thrown in a try block. This should always
     * come directly after a try block, and should be followed by a block of code that should
     * be executed when an exception is thrown in the try block.
     */
    CATCH(false),
    /**
     * The 'finally' keyword, used to execute a block of code after a try block, regardless of
     * whether an exception was thrown or not. This should always come directly after a try- or
     * catch block, and should be followed by a block of code that should be executed after the
     * try- or catch block has been completely executed.
     */
    FINALLY(false),
    ;

    private final TokenType metaData;

    ExceptionTokenType(boolean standalone) {
        this.metaData = TokenMetaData.builder(this)
                .reserved(true)
                .standaloneStatement(standalone)
                .keyword(true)
                .build();
    }

    @Override
    public TokenType delegate() {
        return metaData;
    }
}
