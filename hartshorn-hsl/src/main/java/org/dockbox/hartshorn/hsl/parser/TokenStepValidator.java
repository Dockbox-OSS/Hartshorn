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

package org.dockbox.hartshorn.hsl.parser;

import org.dockbox.hartshorn.hsl.ScriptEvaluationError;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.type.TokenType;

/**
 * Represents a validator for a step in the token stream. This is used to validate the
 * token stream while parsing, typically when performed through a {@link ASTNodeParser}.
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public interface TokenStepValidator {

    /**
     * Attempts to consume a token of the given type. If the token is not of the given type,
     * a {@link ScriptEvaluationError} is thrown.
     *
     * @param type the type of token to consume
     * @return the consumed token
     */
    Token expect(TokenType type);

    /**
     * Attempts to consume a token of the given type. If the token is not of the given type,
     * a {@link ScriptEvaluationError} is thrown. The given {@code what} is used to provide
     * context about what kind of token was expected.
     *
     * @param type the type of token to consume
     * @param what the expected value of the token
     * @return the consumed token
     */
    Token expect(TokenType type, String what);

    /**
     * Attempts to consume a token of the given type, which is expected to be before another
     * token. If the token is not of the given type, a {@link ScriptEvaluationError} is thrown.
     *
     * <p>An example of an error may look something like: "Expected '$type' before $what."
     *
     * @param type the type of token to consume
     * @param before the token that the expected token should be before
     * @return the consumed token
     */
    Token expectBefore(TokenType type, String before);

    /**
     * Attempts to consume a token of the given type, which is expected to be after another
     * token. If the token is not of the given type, a {@link ScriptEvaluationError} is thrown.
     *
     * <p>An example of an error may look something like: "Expected '$type' after $after."
     *
     * @param type the type of token to consume
     * @param after the token that the expected token should be after
     * @return the consumed token
     */
    Token expectAfter(TokenType type, TokenType after);

    /**
     * Attempts to consume a token of the given type, which is expected to be after another
     * token. If the token is not of the given type, a {@link ScriptEvaluationError} is thrown.
     *
     * <p>An example of an error may look something like: "Expected '$type' after $what."
     *
     * @param type the type of token to consume
     * @param after the token that the expected token should be after
     * @return the consumed token
     */
    Token expectAfter(TokenType type, String after);

    /**
     * Attempts to consume a token of the given type, which is expected to be around another
     * token. If the token is not of the given type, a {@link ScriptEvaluationError} is thrown.
     *
     * <p>An example of an error may look something like: "Expected '$type' $position $what."
     *
     * @param type the type of token to consume
     * @param where the token that the expected token should be around
     * @param position the position on which the given token is expected to be
     *
     * @return the consumed token
     */
    Token expectAround(TokenType type, String where, String position);

}
