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

import org.dockbox.hartshorn.hsl.ast.expression.ArrayComprehensionExpression;
import org.dockbox.hartshorn.hsl.ast.expression.ArrayGetExpression;
import org.dockbox.hartshorn.hsl.ast.expression.ArrayLiteralExpression;
import org.dockbox.hartshorn.hsl.ast.expression.ArraySetExpression;
import org.dockbox.hartshorn.hsl.ast.expression.AssignExpression;
import org.dockbox.hartshorn.hsl.ast.expression.BinaryExpression;
import org.dockbox.hartshorn.hsl.ast.expression.LogicalAssignExpression;
import org.dockbox.hartshorn.hsl.ast.expression.BitwiseExpression;
import org.dockbox.hartshorn.hsl.ast.expression.ElvisExpression;
import org.dockbox.hartshorn.hsl.ast.expression.FunctionCallExpression;
import org.dockbox.hartshorn.hsl.ast.expression.GetExpression;
import org.dockbox.hartshorn.hsl.ast.expression.GroupingExpression;
import org.dockbox.hartshorn.hsl.ast.expression.InfixExpression;
import org.dockbox.hartshorn.hsl.ast.expression.LiteralExpression;
import org.dockbox.hartshorn.hsl.ast.expression.LogicalExpression;
import org.dockbox.hartshorn.hsl.ast.expression.PostfixExpression;
import org.dockbox.hartshorn.hsl.ast.expression.PrefixExpression;
import org.dockbox.hartshorn.hsl.ast.expression.RangeExpression;
import org.dockbox.hartshorn.hsl.ast.expression.SetExpression;
import org.dockbox.hartshorn.hsl.ast.expression.SuperExpression;
import org.dockbox.hartshorn.hsl.ast.expression.TernaryExpression;
import org.dockbox.hartshorn.hsl.ast.expression.ThisExpression;
import org.dockbox.hartshorn.hsl.ast.expression.UnaryExpression;
import org.dockbox.hartshorn.hsl.ast.expression.VariableExpression;

/**
 * Visitor interface for all supported {@link org.dockbox.hartshorn.hsl.ast.expression.Expression}s.
 * @param <R> The return type for the visitor.
 *
 * @since 0.4.12
 *
 * @author Guus Lieben
 */
public interface ExpressionVisitor<R> {

    /**
     * Visits the given {@link BinaryExpression binary expression}.
     *
     * @param expression The expression to visit
     *
     * @return The result of the visit
     */
    R visit(BinaryExpression expression);

    /**
     * Visits the given {@link RangeExpression range expression}.
     *
     * @param expression The expression to visit
     *
     * @return The result of the visit
     */
    R visit(RangeExpression expression);

    /**
     * Visits the given {@link GroupingExpression grouping expression}.
     *
     * @param expression The expression to visit
     *
     * @return The result of the visit
     */
    R visit(GroupingExpression expression);

    /**
     * Visits the given {@link LiteralExpression literal expression}.
     *
     * @param expression The expression to visit
     *
     * @return The result of the visit
     */
    R visit(LiteralExpression expression);

    /**
     * Visits the given {@link AssignExpression assign expression}.
     *
     * @param expression The expression to visit
     *
     * @return The result of the visit
     */
    R visit(AssignExpression expression);

    /**
     * Visits the given {@link LogicalAssignExpression logical assign expression}.
     *
     * @param expression The expression to visit
     *
     * @return The result of the visit
     */
    R visit(LogicalAssignExpression expression);

    /**
     * Visits the given {@link UnaryExpression unary expression}.
     *
     * @param expression The expression to visit
     *
     * @return The result of the visit
     */
    R visit(UnaryExpression expression);

    /**
     * Visits the given {@link PostfixExpression postfix expression}.
     *
     * @param expression The expression to visit
     *
     * @return The result of the visit
     */
    R visit(PostfixExpression expression);

    /**
     * Visits the given {@link LogicalExpression logical expression}.
     *
     * @param expression The expression to visit
     *
     * @return The result of the visit
     */
    R visit(LogicalExpression expression);

    /**
     * Visits the given {@link BitwiseExpression bitwise expression}.
     *
     * @param expression The expression to visit
     *
     * @return The result of the visit
     */
    R visit(BitwiseExpression expression);

    /**
     * Visits the given {@link FunctionCallExpression function call expression}.
     *
     * @param expression The expression to visit
     *
     * @return The result of the visit
     */
    R visit(FunctionCallExpression expression);

    /**
     * Visits the given {@link GetExpression get expression}.
     *
     * @param expression The expression to visit
     *
     * @return The result of the visit
     */
    R visit(GetExpression expression);

    /**
     * Visits the given {@link SetExpression set expression}.
     *
     * @param expression The expression to visit
     *
     * @return The result of the visit
     */
    R visit(SetExpression expression);

    /**
     * Visits the given {@link ThisExpression 'this' expression}.
     *
     * @param expression The expression to visit
     *
     * @return The result of the visit
     */
    R visit(ThisExpression expression);

    /**
     * Visits the given {@link SuperExpression 'super' expression}.
     *
     * @param expression The expression to visit
     *
     * @return The result of the visit
     */
    R visit(SuperExpression expression);

    /**
     * Visits the given {@link VariableExpression variable expression}.
     *
     * @param expression The expression to visit
     *
     * @return The result of the visit
     */
    R visit(VariableExpression expression);

    /**
     * Visits the given {@link ElvisExpression elvis expression}.
     *
     * @param expression The expression to visit
     *
     * @return The result of the visit
     */
    R visit(ElvisExpression expression);

    /**
     * Visits the given {@link TernaryExpression ternary expression}.
     *
     * @param expression The expression to visit
     *
     * @return The result of the visit
     */
    R visit(TernaryExpression expression);

    /**
     * Visits the given {@link ArraySetExpression array set expression}.
     *
     * @param expression The expression to visit
     *
     * @return The result of the visit
     */
    R visit(ArraySetExpression expression);

    /**
     * Visits the given {@link ArrayGetExpression array get expression}.
     *
     * @param expression The expression to visit
     *
     * @return The result of the visit
     */
    R visit(ArrayGetExpression expression);

    /**
     * Visits the given {@link ArrayLiteralExpression array literal expression}.
     *
     * @param expression The expression to visit
     *
     * @return The result of the visit
     */
    R visit(ArrayLiteralExpression expression);

    /**
     * Visits the given {@link ArrayComprehensionExpression array comprehension expression}.
     *
     * @param expression The expression to visit
     *
     * @return The result of the visit
     */
    R visit(ArrayComprehensionExpression expression);

    /**
     * Visits the given {@link PrefixExpression prefix expression}.
     *
     * @param expression The expression to visit
     *
     * @return The result of the visit
     */
    R visit(PrefixExpression expression);

    /**
     * Visits the given {@link InfixExpression infix expression}.
     *
     * @param expression The expression to visit
     *
     * @return The result of the visit
     */
    R visit(InfixExpression expression);
}
