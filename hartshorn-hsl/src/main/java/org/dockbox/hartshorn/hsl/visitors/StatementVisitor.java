package org.dockbox.hartshorn.hsl.visitors;

import org.dockbox.hartshorn.hsl.ast.statement.ExpressionStatement;
import org.dockbox.hartshorn.hsl.ast.statement.ModuleStatement;
import org.dockbox.hartshorn.hsl.ast.statement.PrintStatement;
import org.dockbox.hartshorn.hsl.ast.statement.BlockStatement;
import org.dockbox.hartshorn.hsl.ast.statement.IfStatement;
import org.dockbox.hartshorn.hsl.ast.statement.WhileStatement;
import org.dockbox.hartshorn.hsl.ast.statement.DoWhileStatement;
import org.dockbox.hartshorn.hsl.ast.statement.RepeatStatement;
import org.dockbox.hartshorn.hsl.ast.statement.BreakStatement;
import org.dockbox.hartshorn.hsl.ast.statement.ContinueStatement;
import org.dockbox.hartshorn.hsl.ast.statement.FunctionStatement;
import org.dockbox.hartshorn.hsl.ast.statement.ExtensionStatement;
import org.dockbox.hartshorn.hsl.ast.statement.VariableStatement;
import org.dockbox.hartshorn.hsl.ast.statement.ReturnStatement;
import org.dockbox.hartshorn.hsl.ast.statement.ClassStatement;
import org.dockbox.hartshorn.hsl.ast.statement.NativeFunctionStatement;
import org.dockbox.hartshorn.hsl.ast.statement.TestStatement;

public interface StatementVisitor<R> {
    R visit(ExpressionStatement statement);

    R visit(PrintStatement statement);

    R visit(BlockStatement statement);

    R visit(IfStatement statement);

    R visit(WhileStatement statement);

    R visit(DoWhileStatement statement);

    R visit(RepeatStatement statement);

    R visit(BreakStatement statement);

    R visit(ContinueStatement statement);

    R visit(FunctionStatement statement);

    R visit(ExtensionStatement statement);

    R visit(VariableStatement statement);

    R visit(ReturnStatement statement);

    R visit(ClassStatement statement);

    R visit(NativeFunctionStatement statement);

    R visit(TestStatement statement);

    R visit(ModuleStatement statement);
}
