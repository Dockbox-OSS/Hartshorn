/*
 * Copyright 2019-2026 the original author or authors.
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

import org.dockbox.hartshorn.hsl.ast.NamedNode;
import org.dockbox.hartshorn.hsl.ast.statement.ParametricExecutableStatement.Parameter;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.visitors.StatementVisitor;
import org.dockbox.hartshorn.util.introspect.view.MethodView;

import java.util.List;

/**
 * A statement representing a native function declaration, which defines a function that is
 * implemented in the host language (e.g., Java) rather than in the scripting language itself, but
 * can be called from the scripting language.
 *
 * <p>Native functions declarations are always prepended with their module name, which is used
 * to resolve the function at runtime. Invoking a native function is done without the module name,
 * as if it were a regular function defined in the script.
 *
 * <p>For example, the statement below defines a native function named {@code calculateSum}
 * that takes two parameters and is implemented in a module named {@code math} (where the
 * {@code math} module is not imported in the script, but is available to the runtime):
 * <pre>{@code
 * native function math:calculateSum(a, b);
 * var sum = calculateSum(5, 10)
 * }</pre>
 *
 * <p>Module names can be hierarchical, using dot notation to represent submodules. For example,
 * a native function could be defined in a submodule like {@code utils.string}:
 * <pre>{@code
 * native function utils.string:toUpperCase(str);
 * var upper = toUpperCase("hello");
 * }</pre>
 *
 * @since 0.4.12
 * 
 * @author Guus Lieben
 */
public class NativeFunctionStatement extends Function implements NamedNode {

    private final Token name;
    private final Token moduleName;
    private final MethodView<?, ?> method;
    private final List<Parameter> params;

    public NativeFunctionStatement(
        Token name,
        Token moduleName,
        MethodView<?, ?> method,
        List<Parameter> params
    ) {
        super(name);
        this.name = name;
        this.moduleName = moduleName;
        this.method = method;
        this.params = params;
    }

    @Override
    public Token name() {
        return this.name;
    }

    /**
     * Returns the token representing the module name where this native function is implemented.
     *
     * @return the module name token
     */
    public Token moduleName() {
        return this.moduleName;
    }

    /**
     * Returns the list of parameters for this native function.
     *
     * @return the list of parameters
     */
    public List<Parameter> params() {
        return this.params;
    }

    /**
     * Returns the method view representing the native function implementation.
     *
     * @return the method view
     */
    public MethodView<?, ?> method() {
        return this.method;
    }

    @Override
    public <R> R accept(StatementVisitor<R> visitor) {
        return visitor.visit(this);
    }
}
