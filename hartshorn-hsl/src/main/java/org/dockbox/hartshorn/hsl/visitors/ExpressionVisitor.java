/*
 * Copyright 2019-2023 the original author or authors.
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
 * @author Guus Lieben
 * @since 0.4.12
 */
public interface ExpressionVisitor<R> {

    R visit(BinaryExpression expression);

    R visit(RangeExpression expression);

    R visit(GroupingExpression expression);

    R visit(LiteralExpression expression);

    R visit(AssignExpression expression);

    R visit(LogicalAssignExpression expression);

    R visit(UnaryExpression expression);

    R visit(PostfixExpression expression);

    R visit(LogicalExpression expression);

    R visit(BitwiseExpression expression);

    R visit(FunctionCallExpression expression);

    R visit(GetExpression expression);

    R visit(SetExpression expression);

    R visit(ThisExpression expression);

    R visit(SuperExpression expression);

    R visit(VariableExpression expression);

    R visit(ElvisExpression expression);

    R visit(TernaryExpression expression);

    R visit(ArraySetExpression expression);

    R visit(ArrayGetExpression expression);

    R visit(ArrayLiteralExpression expression);

    R visit(ArrayComprehensionExpression expression);

    R visit(PrefixExpression expression);

    R visit(InfixExpression expression);
}
