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

package org.dockbox.hartshorn.hsl.compiler.statement;

import org.dockbox.hartshorn.hsl.ast.statement.VariableStatement;
import org.dockbox.hartshorn.hsl.compiler.CompilationChain;
import org.dockbox.hartshorn.hsl.compiler.CompilationContext;
import org.dockbox.hartshorn.hsl.compiler.runtime.HslRuntimeSupport;

import java.lang.classfile.CodeBuilder;

/**
 * Compiler for {@link VariableStatement} nodes.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public class VariableStatementCompiler implements StatementCompiler<VariableStatement> {

    @Override
    public void compile(VariableStatement node, CodeBuilder code, CompilationContext context, CompilationChain visitor) {
        if (node.initializer() != null) {
            visitor.compile(node.initializer());
        }
        else {
            code.aconst_null();
        }

        int valSlot = context.allocateSlot();
        code.astore(valSlot);

        // Declare in scope
        code.aload(CompilationContext.SCOPE_SLOT);
        code.ldc(node.name().lexeme());
        code.aload(valSlot);
        code.iconst_0();
        code.invokestatic(CD_HslRuntimeSupport, "declareVariable", HslRuntimeSupport.MTD_DeclareVariable);

        // Add result
        code.aload(CompilationContext.CONTEXT_SLOT);
        code.ldc(node.name().lexeme());
        code.aload(valSlot);
        code.invokestatic(CD_HslRuntimeSupport, "addResult", HslRuntimeSupport.MTD_AddResult);
    }
}
