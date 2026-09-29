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

package org.dockbox.hartshorn.hsl.semantic;

import org.dockbox.hartshorn.hsl.ast.expression.Expression;
import org.dockbox.hartshorn.hsl.modules.NativeModule;

import java.util.Map;

/**
 * Contract representing a symbol table and scope resolution state for expressions and modules.
 * Decouples semantic analysis from direct interpreter instances.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public interface SymbolTable {

    /**
     * Resolves the given expression to the specified depth in the scope chain.
     *
     * @param expression the expression to resolve
     * @param depth the depth in the scope chain
     */
    void resolve(Expression expression, int depth);

    /**
     * Returns the resolution depth distance for the given expression, or {@code null} if unresolved.
     *
     * @param expression the expression to check
     * @return the depth distance, or null
     */
    Integer distance(Expression expression);

    /**
     * Returns the external modules available in this symbol table.
     *
     * @return map of module names to native module instances
     */
    Map<String, NativeModule> externalModules();

    /**
     * Checks if the given module is available in this symbol table.
     *
     * @param moduleName the name of the module
     * @return true if the module exists, false otherwise
     */
    default boolean hasModule(String moduleName) {
        return this.externalModules().containsKey(moduleName);
    }
}
