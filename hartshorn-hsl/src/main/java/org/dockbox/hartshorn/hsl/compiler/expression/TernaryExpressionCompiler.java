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

import org.dockbox.hartshorn.hsl.ast.expression.TernaryExpression;
import org.dockbox.hartshorn.hsl.compiler.CompilationChain;
import org.dockbox.hartshorn.hsl.compiler.CompilationContext;
import org.dockbox.hartshorn.hsl.compiler.runtime.HslRuntimeSupport;

import java.lang.classfile.CodeBuilder;
import java.lang.classfile.Label;

/**
 * Compiler for {@link TernaryExpression} nodes.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public class TernaryExpressionCompiler implements ExpressionCompiler<TernaryExpression> {

    @Override
    public void compile(TernaryExpression node, CodeBuilder code, CompilationContext context, CompilationChain visitor) {
        Label elseLabel = code.newLabel();
        Label endLabel = code.newLabel();

        visitor.compile(node.condition());
        code.invokestatic(CD_HslRuntimeSupport, "isTruthy", HslRuntimeSupport.MTD_IsTruthy);
        code.ifeq(elseLabel);

        visitor.compile(node.firstExpression());
        code.goto_(endLabel);

        code.labelBinding(elseLabel);
        visitor.compile(node.secondExpression());
        code.labelBinding(endLabel);
    }
}
