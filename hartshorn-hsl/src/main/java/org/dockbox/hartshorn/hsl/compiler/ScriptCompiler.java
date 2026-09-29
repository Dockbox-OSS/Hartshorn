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

package org.dockbox.hartshorn.hsl.compiler;

import org.dockbox.hartshorn.hsl.CompiledScript;
import org.dockbox.hartshorn.hsl.ast.statement.Statement;
import org.dockbox.hartshorn.hsl.customizer.ScriptContext;

import java.util.List;

/**
 * Service contract for compiling HSL AST statements into reusable {@link CompiledScript} instances.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public interface ScriptCompiler {

    /**
     * Compiles the given AST statements into a {@link CompiledScript}.
     *
     * @param context the script context
     * @param statements the parsed statements
     * @return the compiled script
     */
    CompiledScript compile(ScriptContext context, List<Statement> statements);
}
