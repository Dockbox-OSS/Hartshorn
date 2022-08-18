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
 * A base class for executable statements that can have parameters, such as functions and
 * constructors.
 *
 * @since 0.4.12
 *
 * @author Guus Lieben
 */
public abstract class ParametricExecutableStatement extends Function {

    private final List<Parameter> params;

    protected ParametricExecutableStatement(final Token token, final List<Parameter> params) {
        super(token);
        this.params = params;
    }

    public List<Parameter> parameters() {
        return this.params;
    }

    public abstract List<Statement> statements();

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
