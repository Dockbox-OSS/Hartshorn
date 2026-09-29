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

import org.dockbox.hartshorn.hsl.ast.statement.SwitchCase;
import org.dockbox.hartshorn.hsl.ast.statement.SwitchStatement;
import org.dockbox.hartshorn.hsl.compiler.CompilationChain;
import org.dockbox.hartshorn.hsl.compiler.CompilationContext;

import java.lang.classfile.CodeBuilder;
import java.lang.classfile.Label;
import java.lang.constant.ConstantDescs;
import java.lang.constant.MethodTypeDesc;
import java.util.List;

/**
 * Compiler for {@link SwitchStatement} nodes.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public class SwitchStatementCompiler implements StatementCompiler<SwitchStatement> {

    @Override
    public void compile(SwitchStatement node, CodeBuilder code, CompilationContext context, CompilationChain visitor) {
        visitor.compile(node.expression());
        int switchValSlot = context.allocateSlot();
        code.astore(switchValSlot);

        Label endSwitch = code.newLabel();
        context.pushLoop(endSwitch, endSwitch);

        List<Label> caseLabels = node.cases().stream().map(c -> code.newLabel()).toList();
        Label defaultLabel = code.newLabel();

        for (int i = 0; i < node.cases().size(); i++) {
            SwitchCase switchCase = node.cases().get(i);
            Label caseLabel = caseLabels.get(i);

            code.aload(switchValSlot);
            visitor.compile(switchCase.expression());
            code.invokestatic(CD_HslRuntimeSupport, "equal", MethodTypeDesc.of(ConstantDescs.CD_boolean, CD_Object, CD_Object));
            code.ifne(caseLabel);
        }

        code.goto_(defaultLabel);

        for (int i = 0; i < node.cases().size(); i++) {
            SwitchCase switchCase = node.cases().get(i);
            code.labelBinding(caseLabels.get(i));
            visitor.compile(switchCase.body());
            code.goto_(endSwitch);
        }

        code.labelBinding(defaultLabel);
        if (node.defaultCase() != null) {
            visitor.compile(node.defaultCase().body());
        }

        code.labelBinding(endSwitch);
        context.popLoop();
    }
}
