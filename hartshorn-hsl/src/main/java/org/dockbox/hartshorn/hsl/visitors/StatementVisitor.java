package org.dockbox.hartshorn.hsl.visitors;

import org.dockbox.hartshorn.hsl.ast.ExpressionStatement;
import org.dockbox.hartshorn.hsl.ast.ModuleStatement;
import org.dockbox.hartshorn.hsl.ast.PrintStatement;
import org.dockbox.hartshorn.hsl.ast.BlockStatement;
import org.dockbox.hartshorn.hsl.ast.IfStatement;
import org.dockbox.hartshorn.hsl.ast.WhileStatement;
import org.dockbox.hartshorn.hsl.ast.DoWhileStatement;
import org.dockbox.hartshorn.hsl.ast.RepeatStatement;
import org.dockbox.hartshorn.hsl.ast.BreakStatement;
import org.dockbox.hartshorn.hsl.ast.ContinueStatement;
import org.dockbox.hartshorn.hsl.ast.FunctionStatement;
import org.dockbox.hartshorn.hsl.ast.ExtensionStatement;
import org.dockbox.hartshorn.hsl.ast.Var;
import org.dockbox.hartshorn.hsl.ast.ReturnStatement;
import org.dockbox.hartshorn.hsl.ast.ClassStatement;
import org.dockbox.hartshorn.hsl.ast.NativeFunctionStatement;
import org.dockbox.hartshorn.hsl.ast.TestStatement;

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

    R visit(Var statement);

    R visit(ReturnStatement statement);

    R visit(ClassStatement statement);

    R visit(NativeFunctionStatement statement);

    R visit(TestStatement statement);

    R visit(ModuleStatement statement);
}
