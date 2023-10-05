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

package org.dockbox.hartshorn.hsl.token;

public interface SimpleTokenCharacter extends TokenCharacter {

    static SimpleTokenCharacter of(char character, boolean standalone) {
        return new SimpleTokenCharacter() {

            @Override
            public char character() {
                return character;
            }

            @Override
            public boolean isStandaloneCharacter() {
                return standalone;
            }
        };
    }

    @Override
    default boolean isDigit() {
        char character = this.character();
        return character >= '0' && character <= '9';
    }

    @Override
    default boolean isAlpha() {
        char character = this.character();
        return (character >= 'a' && character <= 'z') ||
                (character >= 'A' && character <= 'Z') ||
                character == '_';
    }

    @Override
    default boolean isAlphaNumeric() {
        return isAlpha() || isDigit();
    }
}
