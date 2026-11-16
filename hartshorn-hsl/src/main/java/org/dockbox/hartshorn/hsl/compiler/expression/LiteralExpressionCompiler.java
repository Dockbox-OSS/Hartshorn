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

import org.dockbox.hartshorn.hsl.ast.expression.LiteralExpression;
import org.dockbox.hartshorn.hsl.compiler.CompilationChain;
import org.dockbox.hartshorn.hsl.compiler.CompilationContext;

import java.lang.classfile.CodeBuilder;
import java.lang.constant.ConstantDescs;
import java.lang.constant.MethodTypeDesc;

/**
 * Compiler for {@link LiteralExpression} nodes.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public class LiteralExpressionCompiler implements ExpressionCompiler<LiteralExpression> {

    @Override
    public void compile(LiteralExpression node, CodeBuilder code, CompilationContext context, CompilationChain visitor) {
        Object val = node.value();
        switch (val) {
            case null -> code.aconst_null();
            case String s -> code.ldc(s);
            case Boolean b -> code.getstatic(CD_Boolean, b ? "TRUE" : "FALSE", CD_Boolean);
            case Double d -> {
                code.ldc(d);
                code.invokestatic(CD_Double, "valueOf", MethodTypeDesc.of(CD_Double, ConstantDescs.CD_double));
            }
            case Integer i -> {
                code.ldc(i);
                code.invokestatic(CD_Integer, "valueOf", MethodTypeDesc.of(CD_Integer, ConstantDescs.CD_int));
            }
            case Number n -> {
                code.ldc(n.doubleValue());
                code.invokestatic(CD_Double, "valueOf", MethodTypeDesc.of(CD_Double, ConstantDescs.CD_double));
            }
            default -> {
            }
        }
    }
}
