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

import org.dockbox.hartshorn.hsl.ast.expression.BitwiseExpression;
import org.dockbox.hartshorn.hsl.compiler.CompilationChain;
import org.dockbox.hartshorn.hsl.compiler.CompilationContext;
import org.dockbox.hartshorn.hsl.compiler.runtime.HslRuntimeSupport;
import org.dockbox.hartshorn.hsl.token.type.BitwiseTokenType;

import java.lang.classfile.CodeBuilder;

/**
 * Compiler for {@link BitwiseExpression} nodes.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public class BitwiseExpressionCompiler implements ExpressionCompiler<BitwiseExpression> {

    @Override
    public void compile(BitwiseExpression node, CodeBuilder code, CompilationContext context, CompilationChain visitor) {
        visitor.compile(node.leftExpression());
        visitor.compile(node.rightExpression());

        switch (node.operator().type()) {
            case BitwiseTokenType.BITWISE_AND ->
                    code.invokestatic(CD_HslRuntimeSupport, "bitwiseAnd", HslRuntimeSupport.MTD_BitwiseAnd);
            case BitwiseTokenType.BITWISE_OR ->
                    code.invokestatic(CD_HslRuntimeSupport, "bitwiseOr", HslRuntimeSupport.MTD_BitwiseOr);
            case BitwiseTokenType.XOR ->
                    code.invokestatic(CD_HslRuntimeSupport, "bitwiseXor", HslRuntimeSupport.MTD_BitwiseXor);
            case BitwiseTokenType.SHIFT_LEFT ->
                    code.invokestatic(CD_HslRuntimeSupport, "shiftLeft", HslRuntimeSupport.MTD_ShiftLeft);
            case BitwiseTokenType.SHIFT_RIGHT ->
                    code.invokestatic(CD_HslRuntimeSupport, "shiftRight", HslRuntimeSupport.MTD_ShiftRight);
            case BitwiseTokenType.LOGICAL_SHIFT_RIGHT ->
                    code.invokestatic(CD_HslRuntimeSupport, "logicalShiftRight", HslRuntimeSupport.MTD_LogicalShiftRight);
            case null, default -> {
            }
        }
    }
}
