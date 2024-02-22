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
import org.dockbox.hartshorn.hsl.ast.expression.BitwiseExpression;
import org.dockbox.hartshorn.hsl.ast.expression.ElvisExpression;
import org.dockbox.hartshorn.hsl.ast.expression.Expression;
import org.dockbox.hartshorn.hsl.ast.expression.FunctionCallExpression;
import org.dockbox.hartshorn.hsl.ast.expression.GetExpression;
import org.dockbox.hartshorn.hsl.ast.expression.GroupingExpression;
import org.dockbox.hartshorn.hsl.ast.expression.InfixExpression;
import org.dockbox.hartshorn.hsl.ast.expression.LiteralExpression;
import org.dockbox.hartshorn.hsl.ast.expression.LogicalAssignExpression;
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
import org.dockbox.hartshorn.hsl.ast.statement.Statement;
import org.dockbox.hartshorn.hsl.ast.statement.SwitchCase;
import org.dockbox.hartshorn.hsl.ast.statement.SwitchStatement;
import org.dockbox.hartshorn.hsl.ast.statement.TestStatement;
import org.dockbox.hartshorn.hsl.ast.statement.VariableStatement;
import org.dockbox.hartshorn.hsl.ast.statement.WhileStatement;

/**
 * A visitor that walks the AST and visits all nodes. This visitor can be extended
 * to implement custom logic for each node type, without having to manually walk
 * the AST.
 *
 * @param <R> the return type of the visitor
 */
public abstract class AbstractASTWalker<R> implements ExpressionVisitor<R>, StatementVisitor<R> {

    @Override
    public R visit(BinaryExpression expression) {
        expression.leftExpression().accept(this);
        expression.rightExpression().accept(this);
        return null;
    }

    @Override
    public R visit(RangeExpression expression) {
        expression.leftExpression().accept(this);
        expression.rightExpression().accept(this);
        return null;
    }

    @Override
    public R visit(GroupingExpression expression) {
        expression.expression().accept(this);
        return null;
    }

    @Override
    public R visit(LiteralExpression expression) {
        return null;
    }

    @Override
    public R visit(AssignExpression expression) {
        expression.value().accept(this);
        return null;
    }

    @Override
    public R visit(LogicalAssignExpression expression) {
        expression.value().accept(this);
        return null;
    }

    @Override
    public R visit(UnaryExpression expression) {
        expression.rightExpression().accept(this);
        return null;
    }

    @Override
    public R visit(PostfixExpression expression) {
        expression.leftExpression().accept(this);
        return null;
    }

    @Override
    public R visit(LogicalExpression expression) {
        expression.leftExpression().accept(this);
        expression.rightExpression().accept(this);
        return null;
    }

    @Override
    public R visit(BitwiseExpression expression) {
        expression.leftExpression().accept(this);
        expression.rightExpression().accept(this);
        return null;
    }

    @Override
    public R visit(FunctionCallExpression expression) {
        expression.callee().accept(this);
        for(Expression arg : expression.arguments()) {
            arg.accept(this);
        }
        return null;
    }

    @Override
    public R visit(GetExpression expression) {
        expression.object().accept(this);
        return null;
    }

    @Override
    public R visit(SetExpression expression) {
        expression.object().accept(this);
        expression.value().accept(this);
        return null;
    }

    @Override
    public R visit(ThisExpression expression) {
        return null;
    }

    @Override
    public R visit(SuperExpression expression) {
        return null;
    }

    @Override
    public R visit(VariableExpression expression) {
        return null;
    }

    @Override
    public R visit(ElvisExpression expression) {
        expression.condition().accept(this);
        expression.rightExpression().accept(this);
        return null;
    }

    @Override
    public R visit(TernaryExpression expression) {
        expression.condition().accept(this);
        expression.firstExpression().accept(this);
        expression.secondExpression().accept(this);
        return null;
    }

    @Override
    public R visit(ArraySetExpression expression) {
        expression.index().accept(this);
        expression.value().accept(this);
        return null;
    }

    @Override
    public R visit(ArrayGetExpression expression) {
        expression.index().accept(this);
        return null;
    }

    @Override
    public R visit(ArrayLiteralExpression expression) {
        for(Expression element : expression.elements()) {
            element.accept(this);
        }
        return null;
    }

    @Override
    public R visit(ArrayComprehensionExpression expression) {
        expression.collection().accept(this);
        expression.condition().accept(this);
        expression.expression().accept(this);
        expression.elseExpression().accept(this);
        return null;
    }

    @Override
    public R visit(PrefixExpression expression) {
        expression.rightExpression().accept(this);
        return null;
    }

    @Override
    public R visit(InfixExpression expression) {
        expression.leftExpression().accept(this);
        expression.rightExpression().accept(this);
        return null;
    }

    @Override
    public R visit(ExpressionStatement statement) {
        statement.expression().accept(this);
        return null;
    }

    @Override
    public R visit(PrintStatement statement) {
        statement.expression().accept(this);
        return null;
    }

    @Override
    public R visit(BlockStatement statement) {
        for(Statement innerStatement : statement.statements()) {
            innerStatement.accept(this);
        }
        return null;
    }

    @Override
    public R visit(IfStatement statement) {
        statement.condition().accept(this);
        statement.thenBranch().accept(this);
        statement.elseBranch().accept(this);
        return null;
    }

    @Override
    public R visit(WhileStatement statement) {
        statement.condition().accept(this);
        statement.body().accept(this);
        return null;
    }

    @Override
    public R visit(DoWhileStatement statement) {
        statement.condition().accept(this);
        statement.body().accept(this);
        return null;
    }

    @Override
    public R visit(ForStatement statement) {
        statement.initializer().accept(this);
        statement.condition().accept(this);
        statement.increment().accept(this);
        statement.body().accept(this);
        return null;
    }

    @Override
    public R visit(ForEachStatement statement) {
        statement.selector().accept(this);
        statement.collection().accept(this);
        statement.body().accept(this);
        return null;
    }

    @Override
    public R visit(RepeatStatement statement) {
        statement.value().accept(this);
        statement.body().accept(this);
        return null;
    }

    @Override
    public R visit(BreakStatement statement) {
        return null;
    }

    @Override
    public R visit(ContinueStatement statement) {
        return null;
    }

    @Override
    public R visit(FunctionStatement statement) {
        statement.body().accept(this);
        return null;
    }

    @Override
    public R visit(FieldStatement statement) {
        statement.initializer().accept(this);
        return null;
    }

    @Override
    public R visit(ConstructorStatement statement) {
        statement.body().accept(this);
        return null;
    }

    @Override
    public R visit(VariableStatement statement) {
        statement.initializer().accept(this);
        return null;
    }

    @Override
    public R visit(ReturnStatement statement) {
        statement.expression().accept(this);
        return null;
    }

    @Override
    public R visit(ClassStatement statement) {
        statement.superClass().accept(this);
        for(FieldStatement field : statement.fields()) {
            field.accept(this);
        }
        statement.constructor().accept(this);
        for(FunctionStatement method : statement.methods()) {
            method.accept(this);
        }
        return null;
    }

    @Override
    public R visit(NativeFunctionStatement statement) {
        return null;
    }

    @Override
    public R visit(TestStatement statement) {
        statement.body().accept(this);
        return null;
    }

    @Override
    public R visit(ModuleStatement statement) {
        return null;
    }

    @Override
    public R visit(SwitchStatement statement) {
        statement.expression().accept(this);
        for(SwitchCase switchCase : statement.cases()) {
            switchCase.accept(this);
        }
        statement.defaultCase().accept(this);
        return null;
    }

    @Override
    public R visit(SwitchCase statement) {
        statement.expression().accept(this);
        statement.body().accept(this);
        return null;
    }
}
