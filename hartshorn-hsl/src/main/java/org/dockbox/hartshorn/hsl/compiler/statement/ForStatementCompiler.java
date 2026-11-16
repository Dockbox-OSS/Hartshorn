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

import org.dockbox.hartshorn.hsl.ast.statement.ForStatement;
import org.dockbox.hartshorn.hsl.compiler.CompilationChain;
import org.dockbox.hartshorn.hsl.compiler.CompilationContext;
import org.dockbox.hartshorn.hsl.compiler.runtime.HslRuntimeSupport;

import java.lang.classfile.CodeBuilder;
import java.lang.classfile.Label;

/**
 * Compiler for {@link ForStatement} nodes.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public class ForStatementCompiler implements StatementCompiler<ForStatement> {

    @Override
    public void compile(ForStatement node, CodeBuilder code, CompilationContext context, CompilationChain visitor) {
        // Enclosing scope for loop initializer variable
        code.aload(CompilationContext.SCOPE_SLOT);
        code.invokestatic(CD_HslRuntimeSupport, "pushScope", HslRuntimeSupport.MTD_PushScope);
        code.astore(CompilationContext.SCOPE_SLOT);

        if (node.initializer() != null) {
            visitor.compile(node.initializer());
        }

        int loopScopeSlot = context.allocateSlot();
        code.aload(CompilationContext.SCOPE_SLOT);
        code.astore(loopScopeSlot);

        Label startLoop = code.newLabel();
        Label continueLoop = code.newLabel();
        Label endLoop = code.newLabel();

        context.pushLoop(endLoop, continueLoop);
        code.labelBinding(startLoop);

        code.aload(loopScopeSlot);
        code.astore(CompilationContext.SCOPE_SLOT);

        if (node.condition() != null) {
            visitor.compile(node.condition());
            code.invokestatic(CD_HslRuntimeSupport, "isTruthy", HslRuntimeSupport.MTD_IsTruthy);
            code.ifeq(endLoop);
        }

        visitor.compile(node.body());

        code.labelBinding(continueLoop);
        code.aload(loopScopeSlot);
        code.astore(CompilationContext.SCOPE_SLOT);

        if (node.increment() != null) {
            visitor.compile(node.increment());
        }
        code.goto_(startLoop);

        code.labelBinding(endLoop);
        context.popLoop();

        code.aload(loopScopeSlot);
        code.invokestatic(CD_HslRuntimeSupport, "popScope", HslRuntimeSupport.MTD_PopScope);
        code.astore(CompilationContext.SCOPE_SLOT);
    }
}
