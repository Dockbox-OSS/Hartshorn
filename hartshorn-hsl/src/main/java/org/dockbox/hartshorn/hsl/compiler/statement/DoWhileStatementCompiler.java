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

import org.dockbox.hartshorn.hsl.ast.statement.DoWhileStatement;
import org.dockbox.hartshorn.hsl.compiler.CompilationChain;
import org.dockbox.hartshorn.hsl.compiler.CompilationContext;
import org.dockbox.hartshorn.hsl.compiler.runtime.HslRuntimeSupport;

import java.lang.classfile.CodeBuilder;
import java.lang.classfile.Label;

/**
 * Compiler for {@link DoWhileStatement} nodes.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public class DoWhileStatementCompiler implements StatementCompiler<DoWhileStatement> {

    @Override
    public void compile(DoWhileStatement node, CodeBuilder code, CompilationContext context, CompilationChain visitor) {
        int loopScopeSlot = context.allocateSlot();
        code.aload(CompilationContext.SCOPE_SLOT);
        code.astore(loopScopeSlot);

        Label startLoop = code.newLabel();
        Label continueLoop = code.newLabel();
        Label endLoop = code.newLabel();

        context.pushLoop(endLoop, continueLoop);
        code.labelBinding(startLoop);

        visitor.compile(node.body());

        code.labelBinding(continueLoop);
        code.aload(loopScopeSlot);
        code.astore(CompilationContext.SCOPE_SLOT);

        visitor.compile(node.condition());
        code.invokestatic(CD_HslRuntimeSupport, "isTruthy", HslRuntimeSupport.MTD_IsTruthy);
        code.ifne(startLoop);

        code.labelBinding(endLoop);
        context.popLoop();

        code.aload(loopScopeSlot);
        code.astore(CompilationContext.SCOPE_SLOT);
    }
}
