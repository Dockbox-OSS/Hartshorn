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

import org.dockbox.hartshorn.hsl.ast.expression.Expression;
import org.dockbox.hartshorn.hsl.ast.expression.FunctionCallExpression;
import org.dockbox.hartshorn.hsl.compiler.CompilationChain;
import org.dockbox.hartshorn.hsl.compiler.CompilationContext;
import org.dockbox.hartshorn.hsl.compiler.runtime.HslRuntimeSupport;

import java.lang.classfile.CodeBuilder;
import java.lang.constant.ConstantDescs;
import java.util.List;

/**
 * Compiler for {@link FunctionCallExpression} nodes.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public class FunctionCallExpressionCompiler implements ExpressionCompiler<FunctionCallExpression> {

    @Override
    public void compile(FunctionCallExpression node, CodeBuilder code, CompilationContext context, CompilationChain visitor) {
        visitor.compile(node.callee());

        List<Expression> args = node.arguments();
        code.ldc(args.size());
        code.anewarray(ConstantDescs.CD_Object);

        for (int i = 0; i < args.size(); i++) {
            code.dup();
            code.ldc(i);
            visitor.compile(args.get(i));
            code.aastore();
        }

        code.aload(CompilationContext.CONTEXT_SLOT);
        code.invokestatic(CD_HslRuntimeSupport, "invokeFunction", HslRuntimeSupport.MTD_InvokeFunction);
    }
}
