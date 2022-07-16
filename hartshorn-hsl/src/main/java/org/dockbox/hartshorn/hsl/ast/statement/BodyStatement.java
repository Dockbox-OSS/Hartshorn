/*
 * Copyright 2019-2022 the original author or authors.
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

package org.dockbox.hartshorn.hsl.ast.statement;

import org.dockbox.hartshorn.hsl.token.Token;

public abstract class BodyStatement extends Statement {

    private final BlockStatement body;

    protected BodyStatement(final int line, final BlockStatement body) {
        super(line);
        this.body = body;
    }

    protected BodyStatement(final Token at, final BlockStatement body) {
        super(at);
        this.body = body;
    }

    public BlockStatement body() {
        return this.body;
    }
}