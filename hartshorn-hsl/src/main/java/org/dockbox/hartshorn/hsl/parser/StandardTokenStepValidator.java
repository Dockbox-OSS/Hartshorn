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

import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.type.TokenType;

/**
 * Default implementation of {@link TokenStepValidator}, based around the {@link TokenParser#consume(TokenType, String)}
 * method.
 *
 * @since 0.4.13
 *
 * @author Guus Lieben
 */
public class StandardTokenStepValidator implements TokenStepValidator {

    private static final String EXPECTED_X = "Expected %s.";
    private static final String EXPECTED_X_AROUND_Y = "Expected '%s' %s %s.";

    private static final String BEFORE = "before";
    private static final String AFTER = "after";
    private static final String KEYWORD = "keyword";

    private final TokenParser parser;

    public StandardTokenStepValidator(TokenParser parser) {
        this.parser = parser;
    }

    @Override
    public Token expect(TokenType type) {
        return this.expect(type, type.representation() + (type.keyword() ? " " + KEYWORD : ""));
    }

    @Override
    public Token expect(TokenType type, String what) {
        return this.parser.consume(type, EXPECTED_X.formatted(what));
    }

    @Override
    public Token expectBefore(TokenType type, String before) {
        return this.expectAround(type, before, BEFORE);
    }

    @Override
    public Token expectAfter(TokenType type, TokenType after) {
        return this.expectAround(type, after.representation(), AFTER);
    }

    @Override
    public Token expectAfter(TokenType type, String after) {
        return this.expectAround(type, after, AFTER);
    }

    @Override
    public Token expectAround(TokenType type, String where, String position) {
        return this.parser.consume(type, EXPECTED_X_AROUND_Y.formatted(type.representation(), position, where));
    }
}
