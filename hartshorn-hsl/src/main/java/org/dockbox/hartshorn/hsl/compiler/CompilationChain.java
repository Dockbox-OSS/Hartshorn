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

import java.lang.classfile.CodeBuilder;
import org.dockbox.hartshorn.hsl.ast.expression.Expression;
import org.dockbox.hartshorn.hsl.ast.statement.Statement;

/**
 * Represents a chain of compilation steps that can be used to compile statements and expressions. This interface is
 * typically implemented by the owning {@link ScriptCompiler}, and provides a way to compile statements and expressions
 * in a chainable manner.
 *
 * <p>Individual compilation steps are not expected to prepare any code for compilation, and should only be responsible
 * for compiling the provided statement or expression.
 *
 * @author Guus Lieben
 *
 * @since 0.7.0
 */
public interface CompilationChain {

    /**
     * Compiles the provided statement.
     *
     * @param statement The statement to compile.
     */
    void compile(Statement statement);

    /**
     * Compiles the provided expression.
     *
     * @param expression The expression to compile.
     */
    void compile(Expression expression);

    /**
     * Forks the compilation chain, shifting the current compilation context to the provided code builder. The forked
     * execution is only active within the provided runnable, and the code builder is restored to its previous state
     * after the runnable has finished executing.
     *
     * @param code The code builder to use for compilation.
     * @param runnable The runnable to execute during compilation.
     */
    void fork(CodeBuilder code, Runnable runnable);
}
