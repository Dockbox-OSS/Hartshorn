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

import org.dockbox.hartshorn.hsl.ast.expression.ArrayComprehensionExpression;
import org.dockbox.hartshorn.hsl.compiler.CompilationChain;
import org.dockbox.hartshorn.hsl.compiler.CompilationContext;

import java.lang.classfile.CodeBuilder;

/**
 * Compiler for {@link ArrayComprehensionExpression} nodes.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public class ArrayComprehensionExpressionCompiler implements ExpressionCompiler<ArrayComprehensionExpression> {

    @Override
    public void compile(ArrayComprehensionExpression node, CodeBuilder code, CompilationContext context, CompilationChain visitor) {
        // Not currently implemented in compilation
    }
}
