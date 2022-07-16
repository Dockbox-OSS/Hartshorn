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

import org.dockbox.hartshorn.hsl.ast.expression.BinaryExpression;
import org.dockbox.hartshorn.hsl.ast.expression.GroupingExpression;
import org.dockbox.hartshorn.hsl.ast.expression.LiteralExpression;
import org.dockbox.hartshorn.hsl.ast.expression.AssignExpression;
import org.dockbox.hartshorn.hsl.ast.expression.UnaryExpression;
import org.dockbox.hartshorn.hsl.ast.expression.LogicalExpression;
import org.dockbox.hartshorn.hsl.ast.expression.BitwiseExpression;
import org.dockbox.hartshorn.hsl.ast.expression.FunctionCallExpression;
import org.dockbox.hartshorn.hsl.ast.expression.GetExpression;
import org.dockbox.hartshorn.hsl.ast.expression.SetExpression;
import org.dockbox.hartshorn.hsl.ast.expression.ThisExpression;
import org.dockbox.hartshorn.hsl.ast.expression.SuperExpression;
import org.dockbox.hartshorn.hsl.ast.expression.VariableExpression;
import org.dockbox.hartshorn.hsl.ast.expression.ElvisExpression;
import org.dockbox.hartshorn.hsl.ast.expression.TernaryExpression;
import org.dockbox.hartshorn.hsl.ast.expression.ArraySetExpression;
import org.dockbox.hartshorn.hsl.ast.expression.ArrayGetExpression;
import org.dockbox.hartshorn.hsl.ast.expression.ArrayVariable;
import org.dockbox.hartshorn.hsl.ast.expression.PrefixExpression;
import org.dockbox.hartshorn.hsl.ast.expression.InfixExpression;
import org.dockbox.hartshorn.hsl.ast.expression.ArrayLiteralExpression;

/**
 * Visitor interface for all supported {@link org.dockbox.hartshorn.hsl.ast.expression.Expression}s.
 * @param <R> The return type for the visitor.
 *
 * @author Guus Lieben
 * @since 22.4
 */
public interface ExpressionVisitor<R> {
    R visit(BinaryExpression expr);

    R visit(GroupingExpression expr);

    R visit(LiteralExpression expr);

    R visit(AssignExpression expr);

    R visit(UnaryExpression expr);

    R visit(LogicalExpression expr);

    R visit(BitwiseExpression expr);

    R visit(FunctionCallExpression expr);

    R visit(GetExpression expr);

    R visit(SetExpression expr);

    R visit(ThisExpression expr);

    R visit(SuperExpression expr);

    R visit(VariableExpression expr);

    R visit(ElvisExpression expr);

    R visit(TernaryExpression expr);

    R visit(ArraySetExpression expr);

    R visit(ArrayGetExpression expr);

    R visit(ArrayVariable expr);

    R visit(ArrayLiteralExpression expr);

    R visit(PrefixExpression expr);

    R visit(InfixExpression expr);
}
