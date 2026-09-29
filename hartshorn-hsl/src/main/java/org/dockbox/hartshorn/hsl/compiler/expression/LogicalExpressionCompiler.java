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

import org.dockbox.hartshorn.hsl.ast.expression.LogicalExpression;
import org.dockbox.hartshorn.hsl.compiler.CompilationChain;
import org.dockbox.hartshorn.hsl.compiler.CompilationContext;
import org.dockbox.hartshorn.hsl.compiler.runtime.HslRuntimeSupport;
import org.dockbox.hartshorn.hsl.token.type.ConditionTokenType;

import java.lang.classfile.CodeBuilder;
import java.lang.classfile.Label;

/**
 * Compiler for {@link LogicalExpression} nodes.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public class LogicalExpressionCompiler implements ExpressionCompiler<LogicalExpression> {

    @Override
    public void compile(LogicalExpression node, CodeBuilder code, CompilationContext context, CompilationChain visitor) {
        Label endLabel = code.newLabel();

        visitor.compile(node.leftExpression());
        code.dup();
        code.invokestatic(CD_HslRuntimeSupport, "isTruthy", HslRuntimeSupport.MTD_IsTruthy);

        if (node.operator().type() == ConditionTokenType.OR) {
            code.ifne(endLabel);
        }
        else {
            code.ifeq(endLabel);
        }

        code.pop();
        visitor.compile(node.rightExpression());
        code.labelBinding(endLabel);
    }
}
