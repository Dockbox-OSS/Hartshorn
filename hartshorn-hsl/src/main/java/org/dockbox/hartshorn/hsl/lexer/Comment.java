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

package org.dockbox.hartshorn.hsl.lexer;

/**
 * Represents a single comment in a script. A comment is a piece of non-code
 * which can be located anywhere in a script.
 *
 * @author Guus Lieben
 * @since 22.4
 */
public class Comment {

    private final int line;
    private final String text;

    public Comment(final int line, final String text) {
        this.line = line;
        this.text = text;
    }

    /**
     * The first line the comment was found on. If a comment spans multiple lines,
     * this number represents the first of these lines.
     * @return The first line number.
     */
    public int line() {
        return this.line;
    }

    /**
     * The untrimmed text content of the comment, excluding any comment tokens.
     * @return The comment content.
     */
    public String text() {
        return this.text;
    }
}
