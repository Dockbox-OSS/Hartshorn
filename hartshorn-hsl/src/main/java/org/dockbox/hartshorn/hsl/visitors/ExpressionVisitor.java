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

    R visit(PrefixExpression expr);

    R visit(InfixExpression expr);
}
