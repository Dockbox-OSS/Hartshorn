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

import org.dockbox.hartshorn.hsl.ast.statement.RepeatStatement;
import org.dockbox.hartshorn.hsl.compiler.CompilationChain;
import org.dockbox.hartshorn.hsl.compiler.CompilationContext;

import java.lang.classfile.CodeBuilder;
import java.lang.classfile.Label;
import java.lang.constant.ConstantDescs;
import java.lang.constant.MethodTypeDesc;

/**
 * Compiler for {@link RepeatStatement} nodes.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public class RepeatStatementCompiler implements StatementCompiler<RepeatStatement> {

    @Override
    public void compile(RepeatStatement node, CodeBuilder code, CompilationContext context, CompilationChain visitor) {
        visitor.compile(node.value());
        code.checkcast(CD_Number);
        code.invokevirtual(CD_Number, "intValue", MethodTypeDesc.of(ConstantDescs.CD_int));

        int countSlot = context.allocateSlot();
        int indexSlot = context.allocateSlot();
        code.istore(countSlot);
        code.iconst_0();
        code.istore(indexSlot);

        Label startLoop = code.newLabel();
        Label continueLoop = code.newLabel();
        Label endLoop = code.newLabel();

        context.pushLoop(endLoop, continueLoop);
        code.labelBinding(startLoop);

        code.iload(indexSlot);
        code.iload(countSlot);
        code.if_icmpge(endLoop);

        visitor.compile(node.body());

        code.labelBinding(continueLoop);
        code.iinc(indexSlot, 1);
        code.goto_(startLoop);

        code.labelBinding(endLoop);
        context.popLoop();
    }
}
