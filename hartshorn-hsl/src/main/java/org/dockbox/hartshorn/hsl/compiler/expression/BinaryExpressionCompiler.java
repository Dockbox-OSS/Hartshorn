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

import org.dockbox.hartshorn.hsl.ast.expression.BinaryExpression;
import org.dockbox.hartshorn.hsl.compiler.CompilationChain;
import org.dockbox.hartshorn.hsl.compiler.CompilationContext;
import org.dockbox.hartshorn.hsl.compiler.runtime.HslRuntimeSupport;
import org.dockbox.hartshorn.hsl.token.type.ArithmeticTokenType;
import org.dockbox.hartshorn.hsl.token.type.ConditionTokenType;

import java.lang.classfile.CodeBuilder;
import java.lang.constant.ConstantDescs;
import java.lang.constant.MethodTypeDesc;

/**
 * Compiler for {@link BinaryExpression} nodes.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public class BinaryExpressionCompiler implements ExpressionCompiler<BinaryExpression> {

    private static final MethodTypeDesc MTD_BooleanValueOf = MethodTypeDesc.of(CD_Boolean, ConstantDescs.CD_boolean);

    @Override
    public void compile(BinaryExpression node, CodeBuilder code, CompilationContext context, CompilationChain visitor) {
        visitor.compile(node.leftExpression());
        visitor.compile(node.rightExpression());

        switch (node.operator().type()) {
            case ArithmeticTokenType.PLUS ->
                    code.invokestatic(CD_HslRuntimeSupport, "add", HslRuntimeSupport.MTD_Add);
            case ArithmeticTokenType.MINUS ->
                    code.invokestatic(CD_HslRuntimeSupport, "subtract", HslRuntimeSupport.MTD_Subtract);
            case ArithmeticTokenType.STAR ->
                    code.invokestatic(CD_HslRuntimeSupport, "multiply", HslRuntimeSupport.MTD_Multiply);
            case ArithmeticTokenType.SLASH ->
                    code.invokestatic(CD_HslRuntimeSupport, "divide", HslRuntimeSupport.MTD_Divide);
            case ArithmeticTokenType.MODULO ->
                    code.invokestatic(CD_HslRuntimeSupport, "modulo", HslRuntimeSupport.MTD_Modulo);
            case ConditionTokenType.EQUAL_EQUAL -> {
                code.invokestatic(CD_HslRuntimeSupport, "equal", HslRuntimeSupport.MTD_Equal);
                code.invokestatic(CD_Boolean, "valueOf", MTD_BooleanValueOf);
            }
            case ConditionTokenType.BANG_EQUAL -> {
                code.invokestatic(CD_HslRuntimeSupport, "notEqual", HslRuntimeSupport.MTD_NotEqual);
                code.invokestatic(CD_Boolean, "valueOf", MTD_BooleanValueOf);
            }
            case ConditionTokenType.GREATER -> {
                code.invokestatic(CD_HslRuntimeSupport, "greater", HslRuntimeSupport.MTD_Greater);
                code.invokestatic(CD_Boolean, "valueOf", MTD_BooleanValueOf);
            }
            case ConditionTokenType.GREATER_EQUAL -> {
                code.invokestatic(CD_HslRuntimeSupport, "greaterEqual", HslRuntimeSupport.MTD_GreaterEqual);
                code.invokestatic(CD_Boolean, "valueOf", MTD_BooleanValueOf);
            }
            case ConditionTokenType.LESS -> {
                code.invokestatic(CD_HslRuntimeSupport, "less", HslRuntimeSupport.MTD_Less);
                code.invokestatic(CD_Boolean, "valueOf", MTD_BooleanValueOf);
            }
            case ConditionTokenType.LESS_EQUAL -> {
                code.invokestatic(CD_HslRuntimeSupport, "lessEqual", HslRuntimeSupport.MTD_LessEqual);
                code.invokestatic(CD_Boolean, "valueOf", MTD_BooleanValueOf);
            }
            case null, default -> {
            }
        }
    }
}
