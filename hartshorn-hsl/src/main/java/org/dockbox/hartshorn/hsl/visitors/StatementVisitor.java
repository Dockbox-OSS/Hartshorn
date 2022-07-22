/*
 * Copyright 2019-2022 the original author or authors.
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

package org.dockbox.hartshorn.hsl.visitors;

import org.dockbox.hartshorn.hsl.ast.statement.*;

/**
 * Visitor interface for all supported {@link org.dockbox.hartshorn.hsl.ast.statement.Statement}s.
 * @param <R> The return type for the visitor.
 *
 * @author Guus Lieben
 * @since 22.4
 */
public interface StatementVisitor<R> {
    R visit(ExpressionStatement statement);

    R visit(PrintStatement statement);

    R visit(BlockStatement statement);

    R visit(IfStatement statement);

    R visit(WhileStatement statement);

    R visit(DoWhileStatement statement);

    R visit(ForStatement statement);

    R visit(ForEachStatement statement);

    R visit(RepeatStatement statement);

    R visit(BreakStatement statement);

    R visit(ContinueStatement statement);

    R visit(FunctionStatement statement);

    R visit(ConstructorStatement statement);

    R visit(ExtensionStatement statement);

    R visit(VariableStatement statement);

    R visit(ReturnStatement statement);

    R visit(ClassStatement statement);

    R visit(NativeFunctionStatement statement);

    R visit(TestStatement statement);

    R visit(ModuleStatement statement);

    R visit(SwitchStatement statement);

    R visit(SwitchCase statement);
}
