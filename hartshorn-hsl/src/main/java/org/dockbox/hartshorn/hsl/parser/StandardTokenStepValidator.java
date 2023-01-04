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

package org.dockbox.hartshorn.hsl.parser;

import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.TokenType;

import jakarta.inject.Inject;

public class StandardTokenStepValidator implements TokenStepValidator {

    private final TokenParser parser;

    @Inject
    public StandardTokenStepValidator(final TokenParser parser) {
        this.parser = parser;
    }

    @Override
    public Token expect(final TokenType type) {
        return this.expect(type, type.representation() + (type.keyword() ? " keyword" : ""));
    }

    @Override
    public Token expect(final TokenType type, final String what) {
        return this.parser.consume(type, "Expected " + what + ".");
    }

    @Override
    public Token expectBefore(final TokenType type, final String before) {
        return this.expectAround(type, before, "before");
    }

    @Override
    public Token expectAfter(final TokenType type, final TokenType after) {
        return this.expectAround(type, after.representation(), "after");
    }

    @Override
    public Token expectAfter(final TokenType type, final String after) {
        return this.expectAround(type, after, "after");
    }

    @Override
    public Token expectAround(final TokenType type, final String where, final String position) {
        return this.parser.consume(type, "Expected '%s' %s %s.".formatted(type.representation(), position, where));
    }
}
