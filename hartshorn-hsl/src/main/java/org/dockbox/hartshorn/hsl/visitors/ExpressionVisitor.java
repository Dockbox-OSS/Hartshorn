package org.dockbox.hartshorn.hsl.visitors;

import org.dockbox.hartshorn.hsl.ast.BinaryExp;
import org.dockbox.hartshorn.hsl.ast.GroupingExp;
import org.dockbox.hartshorn.hsl.ast.LiteralExp;
import org.dockbox.hartshorn.hsl.ast.AssignExp;
import org.dockbox.hartshorn.hsl.ast.UnaryExp;
import org.dockbox.hartshorn.hsl.ast.LogicalExp;
import org.dockbox.hartshorn.hsl.ast.BitwiseExp;
import org.dockbox.hartshorn.hsl.ast.CallExp;
import org.dockbox.hartshorn.hsl.ast.GetExp;
import org.dockbox.hartshorn.hsl.ast.SetExp;
import org.dockbox.hartshorn.hsl.ast.ThisExp;
import org.dockbox.hartshorn.hsl.ast.SuperExp;
import org.dockbox.hartshorn.hsl.ast.Variable;
import org.dockbox.hartshorn.hsl.ast.ElvisExp;
import org.dockbox.hartshorn.hsl.ast.TernaryExp;
import org.dockbox.hartshorn.hsl.ast.ArraySetExp;
import org.dockbox.hartshorn.hsl.ast.ArrayGetExp;
import org.dockbox.hartshorn.hsl.ast.ArrayVariable;
import org.dockbox.hartshorn.hsl.ast.PrefixExpression;
import org.dockbox.hartshorn.hsl.ast.InfixExpression;

public interface ExpressionVisitor<R> {
    R visit(BinaryExp expr);

    R visit(GroupingExp expr);

    R visit(LiteralExp expr);

    R visit(AssignExp expr);

    R visit(UnaryExp expr);

    R visit(LogicalExp expr);

    R visit(BitwiseExp expr);

    R visit(CallExp expr);

    R visit(GetExp expr);

    R visit(SetExp expr);

    R visit(ThisExp expr);

    R visit(SuperExp expr);

    R visit(Variable expr);

    R visit(ElvisExp expr);

    R visit(TernaryExp expr);

    R visit(ArraySetExp expr);

    R visit(ArrayGetExp expr);

    R visit(ArrayVariable expr);

    R visit(PrefixExpression expr);

    R visit(InfixExpression expr);
}
