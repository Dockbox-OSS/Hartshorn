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

package org.dockbox.hartshorn.hsl.ast;

/**
 * TODO: #1061 Add documentation
 *
 * @since 0.4.12
 *
 * @author Guus Lieben
 */
public abstract class ASTNode {

    private final int line;
    private final int column;

    protected ASTNode(ASTNode at) {
        this(at.line, at.column);
    }

    protected ASTNode(int line, int column) {
        this.line = line;
        this.column = column;
    }

    public int line() {
        return this.line;
    }

    public int column() {
        return this.column;
    }
}
