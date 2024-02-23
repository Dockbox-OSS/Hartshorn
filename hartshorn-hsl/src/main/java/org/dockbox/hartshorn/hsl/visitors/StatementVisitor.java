/*
 * Copyright 2019-2024 the original author or authors.
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

import org.dockbox.hartshorn.hsl.ast.statement.BlockStatement;
import org.dockbox.hartshorn.hsl.ast.statement.BreakStatement;
import org.dockbox.hartshorn.hsl.ast.statement.ClassStatement;
import org.dockbox.hartshorn.hsl.ast.statement.ConstructorStatement;
import org.dockbox.hartshorn.hsl.ast.statement.ContinueStatement;
import org.dockbox.hartshorn.hsl.ast.statement.DoWhileStatement;
import org.dockbox.hartshorn.hsl.ast.statement.ExpressionStatement;
import org.dockbox.hartshorn.hsl.ast.statement.FieldStatement;
import org.dockbox.hartshorn.hsl.ast.statement.ForEachStatement;
import org.dockbox.hartshorn.hsl.ast.statement.ForStatement;
import org.dockbox.hartshorn.hsl.ast.statement.FunctionStatement;
import org.dockbox.hartshorn.hsl.ast.statement.IfStatement;
import org.dockbox.hartshorn.hsl.ast.statement.ModuleStatement;
import org.dockbox.hartshorn.hsl.ast.statement.NativeFunctionStatement;
import org.dockbox.hartshorn.hsl.ast.statement.PrintStatement;
import org.dockbox.hartshorn.hsl.ast.statement.RepeatStatement;
import org.dockbox.hartshorn.hsl.ast.statement.ReturnStatement;
import org.dockbox.hartshorn.hsl.ast.statement.SwitchCase;
import org.dockbox.hartshorn.hsl.ast.statement.SwitchStatement;
import org.dockbox.hartshorn.hsl.ast.statement.TestStatement;
import org.dockbox.hartshorn.hsl.ast.statement.VariableStatement;
import org.dockbox.hartshorn.hsl.ast.statement.WhileStatement;

/**
 * Visitor interface for all supported {@link org.dockbox.hartshorn.hsl.ast.statement.Statement}s.
 * @param <R> The return type for the visitor.
 *
 * @author Guus Lieben
 * @since 0.4.12
 */
public interface StatementVisitor<R> {

    /**
     * Visits the given {@link ExpressionStatement expression statement}.
     *
     * @param statement The statement to visit
     *
     * @return The result of the visit
     */
    R visit(ExpressionStatement statement);

    /**
     * Visits the given {@link PrintStatement print statement}.
     *
     * @param statement The statement to visit
     *
     * @return The result of the visit
     */
    R visit(PrintStatement statement);

    /**
     * Visits the given {@link BlockStatement block statement}.
     *
     * @param statement The statement to visit
     *
     * @return The result of the visit
     */
    R visit(BlockStatement statement);

    /**
     * Visits the given {@link IfStatement if statement}.
     *
     * @param statement The statement to visit
     *
     * @return The result of the visit
     */
    R visit(IfStatement statement);

    /**
     * Visits the given {@link WhileStatement while statement}.
     *
     * @param statement The statement to visit
     *
     * @return The result of the visit
     */
    R visit(WhileStatement statement);

    /**
     * Visits the given {@link DoWhileStatement do-while statement}.
     *
     * @param statement The statement to visit
     *
     * @return The result of the visit
     */
    R visit(DoWhileStatement statement);

    /**
     * Visits the given {@link ForStatement for statement}.
     *
     * @param statement The statement to visit
     *
     * @return The result of the visit
     */
    R visit(ForStatement statement);

    /**
     * Visits the given {@link ForEachStatement for-each statement}.
     *
     * @param statement The statement to visit
     *
     * @return The result of the visit
     */
    R visit(ForEachStatement statement);

    /**
     * Visits the given {@link RepeatStatement repeat statement}.
     *
     * @param statement The statement to visit
     *
     * @return The result of the visit
     */
    R visit(RepeatStatement statement);

    /**
     * Visits the given {@link BreakStatement break statement}.
     *
     * @param statement The statement to visit
     *
     * @return The result of the visit
     */
    R visit(BreakStatement statement);

    /**
     * Visits the given {@link ContinueStatement continue statement}.
     *
     * @param statement The statement to visit
     *
     * @return The result of the visit
     */
    R visit(ContinueStatement statement);

    /**
     * Visits the given {@link FunctionStatement function statement}.
     *
     * @param statement The statement to visit
     *
     * @return The result of the visit
     */
    R visit(FunctionStatement statement);

    /**
     * Visits the given {@link FieldStatement field statement}.
     *
     * @param statement The statement to visit
     *
     * @return The result of the visit
     */
    R visit(FieldStatement statement);

    /**
     * Visits the given {@link ConstructorStatement constructor statement}.
     *
     * @param statement The statement to visit
     *
     * @return The result of the visit
     */
    R visit(ConstructorStatement statement);

    /**
     * Visits the given {@link VariableStatement variable statement}.
     *
     * @param statement The statement to visit
     *
     * @return The result of the visit
     */
    R visit(VariableStatement statement);

    /**
     * Visits the given {@link ReturnStatement return statement}.
     *
     * @param statement The statement to visit
     *
     * @return The result of the visit
     */
    R visit(ReturnStatement statement);

    /**
     * Visits the given {@link ClassStatement class statement}.
     *
     * @param statement The statement to visit
     *
     * @return The result of the visit
     */
    R visit(ClassStatement statement);

    /**
     * Visits the given {@link NativeFunctionStatement native function statement}.
     *
     * @param statement The statement to visit
     *
     * @return The result of the visit
     */
    R visit(NativeFunctionStatement statement);

    /**
     * Visits the given {@link TestStatement test statement}.
     *
     * @param statement The statement to visit
     *
     * @return The result of the visit
     */
    R visit(TestStatement statement);

    /**
     * Visits the given {@link ModuleStatement module statement}.
     *
     * @param statement The statement to visit
     *
     * @return The result of the visit
     */
    R visit(ModuleStatement statement);

    /**
     * Visits the given {@link SwitchStatement switch statement}.
     *
     * @param statement The statement to visit
     *
     * @return The result of the visit
     */
    R visit(SwitchStatement statement);

    /**
     * Visits the given {@link SwitchCase switch case statement}.
     *
     * @param statement The statement to visit
     *
     * @return The result of the visit
     */
    R visit(SwitchCase statement);
}
