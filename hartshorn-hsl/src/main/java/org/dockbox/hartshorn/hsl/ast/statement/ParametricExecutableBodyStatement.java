/*
 * Copyright 2019-2025 the original author or authors.
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

import java.util.List;

/**
 * An abstract executable statement with parameters and a body.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public abstract class ParametricExecutableBodyStatement extends ParametricExecutableStatement {

    private final BlockStatement body;

    protected ParametricExecutableBodyStatement(Token token, List<Parameter> params, BlockStatement body) {
        super(token, params);
        this.body = body;
    }

    /**
     * The body of this executable statement.
     *
     * @return the body block
     */
    public BlockStatement body() {
        return this.body;
    }

    @Override
    public List<Statement> statements() {
        return this.body != null ? this.body.statements() : List.of();
    }
}
