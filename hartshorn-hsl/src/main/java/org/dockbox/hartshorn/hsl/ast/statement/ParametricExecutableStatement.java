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

package org.dockbox.hartshorn.hsl.ast.statement;

import org.dockbox.hartshorn.hsl.token.Token;

import java.util.List;

/**
 * TODO: #1061 Add documentation
 *
 * @since 0.4.12
 *
 * @author Guus Lieben
 */
public abstract class ParametricExecutableStatement extends Function {

    private final List<Parameter> params;
    private final BlockStatement body;

    protected ParametricExecutableStatement(Token token, List<Parameter> params, BlockStatement body) {
        super(token);
        this.params = params;
        this.body = body;
    }

    public List<Parameter> parameters() {
        return this.params;
    }

    public List<Statement> statements() {
        return this.body.statements();
    }

    public BlockStatement body() {
        return this.body;
    }

    /**
     * Simple record to represent a parameter in a function declaration.
     *
     * @param name the name of the parameter
     *
     * @since 0.4.12
     *
     * @author Guus Lieben
     */
    public record Parameter(Token name) {
    }
}
