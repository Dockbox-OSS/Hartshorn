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

import org.dockbox.hartshorn.hsl.ast.ASTNode;
import org.dockbox.hartshorn.hsl.compiler.runtime.CompiledScriptExecutable;
import org.dockbox.hartshorn.hsl.compiler.runtime.HslRuntimeSupport;
import org.dockbox.hartshorn.hsl.customizer.ScriptContext;
import org.dockbox.hartshorn.hsl.interpreter.Array;
import org.dockbox.hartshorn.hsl.interpreter.VariableScope;
import org.dockbox.hartshorn.hsl.runtime.Yield;

import java.lang.classfile.CodeBuilder;
import java.lang.constant.ClassDesc;
import java.lang.constant.ConstantDescs;

/**
 * Interface for compiling AST nodes to JVM bytecode instructions.
 *
 * @param <T> the type of {@link ASTNode} this compiler can handle
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public interface ASTNodeCompiler<T extends ASTNode> {

    ClassDesc CD_Object = ConstantDescs.CD_Object;
    ClassDesc CD_Boolean = ClassDesc.of(Boolean.class.getName());
    ClassDesc CD_Double = ClassDesc.of(Double.class.getName());
    ClassDesc CD_Integer = ClassDesc.of(Integer.class.getName());
    ClassDesc CD_Number = ClassDesc.of(Number.class.getName());
    ClassDesc CD_ScriptContext = ClassDesc.of(ScriptContext.class.getName());
    ClassDesc CD_VariableScope = ClassDesc.of(VariableScope.class.getName());
    ClassDesc CD_HslRuntimeSupport = ClassDesc.of(HslRuntimeSupport.class.getName());
    ClassDesc CD_Array = ClassDesc.of(Array.class.getName());
    ClassDesc CD_CompiledScriptExecutable = ClassDesc.of(CompiledScriptExecutable.class.getName());
    ClassDesc CD_Yield = ClassDesc.of(Yield.class.getName());

    /**
     * Compiles the given AST node into JVM bytecode instructions using the provided code builder,
     * compilation context, and compiler visitor.
     *
     * @param node the AST node to compile
     * @param code the code builder emitting bytecode instructions
     * @param context the active compilation context
     * @param visitor the visitor for compiling child nodes
     */
    void compile(T node, CodeBuilder code, CompilationContext context, CompilationChain visitor);
}
