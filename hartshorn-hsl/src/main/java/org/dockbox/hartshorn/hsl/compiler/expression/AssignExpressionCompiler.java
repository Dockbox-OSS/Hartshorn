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

package org.dockbox.hartshorn.hsl.compiler.expression;

import org.dockbox.hartshorn.hsl.ast.expression.AssignExpression;
import org.dockbox.hartshorn.hsl.compiler.CompilationChain;
import org.dockbox.hartshorn.hsl.compiler.CompilationContext;
import org.dockbox.hartshorn.hsl.compiler.runtime.HslRuntimeSupport;

import java.lang.classfile.CodeBuilder;
import java.lang.constant.ConstantDescs;
import java.lang.constant.MethodTypeDesc;

/**
 * Compiler for {@link AssignExpression} nodes.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public class AssignExpressionCompiler implements ExpressionCompiler<AssignExpression> {

    @Override
    public void compile(AssignExpression node, CodeBuilder code, CompilationContext context, CompilationChain visitor) {
        visitor.compile(node.value());
        code.dup();

        int valSlot = context.allocateSlot();
        code.astore(valSlot);

        code.aload(CompilationContext.CONTEXT_SLOT);
        code.aload(CompilationContext.SCOPE_SLOT);
        code.ldc(node.name().lexeme());

        Integer distance = context.distance(node);
        if (distance != null) {
            code.ldc(distance);
            code.invokestatic(CD_Integer, "valueOf", MethodTypeDesc.of(CD_Integer, ConstantDescs.CD_int));
        }
        else {
            code.aconst_null();
        }

        code.aload(valSlot);
        code.invokestatic(CD_HslRuntimeSupport, "assignVariable", HslRuntimeSupport.MTD_AssignVariable);
    }
}
