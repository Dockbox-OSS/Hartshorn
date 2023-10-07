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

package org.dockbox.hartshorn.hsl.interpreter;

import org.dockbox.hartshorn.hsl.ast.MoveKeyword;
import org.dockbox.hartshorn.hsl.ast.expression.ArrayComprehensionExpression;
import org.dockbox.hartshorn.hsl.ast.expression.ArrayGetExpression;
import org.dockbox.hartshorn.hsl.ast.expression.ArrayLiteralExpression;
import org.dockbox.hartshorn.hsl.ast.expression.ArraySetExpression;
import org.dockbox.hartshorn.hsl.ast.expression.AssignExpression;
import org.dockbox.hartshorn.hsl.ast.expression.BinaryExpression;
import org.dockbox.hartshorn.hsl.ast.expression.BitwiseExpression;
import org.dockbox.hartshorn.hsl.ast.expression.ElvisExpression;
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
import org.dockbox.hartshorn.hsl.ast.statement.SwitchCase;
import org.dockbox.hartshorn.hsl.ast.statement.SwitchStatement;
import org.dockbox.hartshorn.hsl.ast.statement.TestStatement;
import org.dockbox.hartshorn.hsl.ast.statement.VariableStatement;
import org.dockbox.hartshorn.hsl.ast.statement.WhileStatement;
import org.dockbox.hartshorn.hsl.interpreter.expression.ArrayComprehensionExpressionInterpreter;
import org.dockbox.hartshorn.hsl.interpreter.expression.ArrayGetExpressionInterpreter;
import org.dockbox.hartshorn.hsl.interpreter.expression.ArrayLiteralExpressionInterpreter;
import org.dockbox.hartshorn.hsl.interpreter.expression.ArraySetExpressionInterpreter;
import org.dockbox.hartshorn.hsl.interpreter.expression.AssignExpressionInterpreter;
import org.dockbox.hartshorn.hsl.interpreter.expression.BinaryExpressionInterpreter;
import org.dockbox.hartshorn.hsl.interpreter.expression.BitwiseExpressionInterpreter;
import org.dockbox.hartshorn.hsl.interpreter.expression.ElvisExpressionInterpreter;
import org.dockbox.hartshorn.hsl.interpreter.expression.FunctionCallExpressionInterpreter;
import org.dockbox.hartshorn.hsl.interpreter.expression.GetExpressionInterpreter;
import org.dockbox.hartshorn.hsl.interpreter.expression.InfixExpressionInterpreter;
import org.dockbox.hartshorn.hsl.interpreter.expression.LogicalAssignExpressionInterpreter;
import org.dockbox.hartshorn.hsl.interpreter.expression.LogicalExpressionInterpreter;
import org.dockbox.hartshorn.hsl.interpreter.expression.PostfixExpressionInterpreter;
import org.dockbox.hartshorn.hsl.interpreter.expression.PrefixExpressionInterpreter;
import org.dockbox.hartshorn.hsl.interpreter.expression.RangeExpressionInterpreter;
import org.dockbox.hartshorn.hsl.interpreter.expression.SetExpressionInterpreter;
import org.dockbox.hartshorn.hsl.interpreter.expression.SuperExpressionInterpreter;
import org.dockbox.hartshorn.hsl.interpreter.expression.TernaryExpressionInterpreter;
import org.dockbox.hartshorn.hsl.interpreter.expression.UnaryExpressionInterpreter;
import org.dockbox.hartshorn.hsl.interpreter.statement.ClassStatementInterpreter;
import org.dockbox.hartshorn.hsl.interpreter.statement.ConstructorStatementInterpreter;
import org.dockbox.hartshorn.hsl.interpreter.statement.DoWhileStatementInterpreter;
import org.dockbox.hartshorn.hsl.interpreter.statement.FieldStatementInterpreter;
import org.dockbox.hartshorn.hsl.interpreter.statement.ForEachStatementInterpreter;
import org.dockbox.hartshorn.hsl.interpreter.statement.ForStatementInterpreter;
import org.dockbox.hartshorn.hsl.interpreter.statement.FunctionStatementInterpreter;
import org.dockbox.hartshorn.hsl.interpreter.statement.IfStatementInterpreter;
import org.dockbox.hartshorn.hsl.interpreter.statement.ModuleStatementInterpreter;
import org.dockbox.hartshorn.hsl.interpreter.statement.NativeFunctionStatementInterpreter;
import org.dockbox.hartshorn.hsl.interpreter.statement.RepeatStatementInterpreter;
import org.dockbox.hartshorn.hsl.interpreter.statement.ReturnStatementInterpreter;
import org.dockbox.hartshorn.hsl.interpreter.statement.SwitchCaseInterpreter;
import org.dockbox.hartshorn.hsl.interpreter.statement.SwitchStatementInterpreter;
import org.dockbox.hartshorn.hsl.interpreter.statement.TestStatementInterpreter;
import org.dockbox.hartshorn.hsl.interpreter.statement.VariableStatementInterpreter;
import org.dockbox.hartshorn.hsl.interpreter.statement.WhileStatementInterpreter;

public record DelegatingInterpreterVisitor(Interpreter interpreter) implements InterpreterVisitor {

    @Override
    public Object visit(final BinaryExpression expression) {
        return new BinaryExpressionInterpreter().interpret(expression, this.interpreter);
    }

    @Override
    public Object visit(final RangeExpression expression) {
        return new RangeExpressionInterpreter().interpret(expression, this.interpreter);
    }

    @Override
    public Object visit(final GroupingExpression expression) {
        return this.interpreter.evaluate(expression.expression());
    }

    @Override
    public Object visit(final LiteralExpression expression) {
        return expression.value();
    }

    @Override
    public Object visit(final AssignExpression expression) {
        return new AssignExpressionInterpreter().interpret(expression, this.interpreter);
    }

    @Override
    public Object visit(final LogicalAssignExpression expression) {
        return new LogicalAssignExpressionInterpreter().interpret(expression, this.interpreter);
    }

    @Override
    public Object visit(final UnaryExpression expression) {
        return new UnaryExpressionInterpreter().interpret(expression, this.interpreter);
    }

    @Override
    public Object visit(final PostfixExpression expression) {
        return new PostfixExpressionInterpreter().interpret(expression, this.interpreter);
    }

    @Override
    public Object visit(final LogicalExpression expression) {
        return new LogicalExpressionInterpreter().interpret(expression, this.interpreter);
    }

    @Override
    public Object visit(final BitwiseExpression expression) {
        return new BitwiseExpressionInterpreter().interpret(expression, this.interpreter);
    }

    @Override
    public Object visit(final ElvisExpression expression) {
        return new ElvisExpressionInterpreter().interpret(expression, this.interpreter);
    }

    @Override
    public Object visit(final TernaryExpression expression) {
        return new TernaryExpressionInterpreter().interpret(expression, this.interpreter);
    }

    @Override
    public Object visit(final ArraySetExpression expression) {
        return new ArraySetExpressionInterpreter().interpret(expression, this.interpreter);
    }

    @Override
    public Object visit(final ArrayGetExpression expression) {
        return new ArrayGetExpressionInterpreter().interpret(expression, this.interpreter);
    }

    @Override
    public Object visit(final ArrayLiteralExpression expression) {
        return new ArrayLiteralExpressionInterpreter().interpret(expression, this.interpreter);
    }

    @Override
    public Object visit(final ArrayComprehensionExpression expression) {
        return new ArrayComprehensionExpressionInterpreter().interpret(expression, this.interpreter);
    }

    @Override
    public Object visit(final PrefixExpression expression) {
        return new PrefixExpressionInterpreter().interpret(expression, this.interpreter);
    }

    @Override
    public Object visit(final InfixExpression expression) {
        return new InfixExpressionInterpreter().interpret(expression, this.interpreter);
    }

    @Override
    public Object visit(final FunctionCallExpression expression) {
        return new FunctionCallExpressionInterpreter().interpret(expression, this.interpreter);
    }

    @Override
    public Object visit(final GetExpression expression) {
        return new GetExpressionInterpreter().interpret(expression, this.interpreter);
    }

    @Override
    public Object visit(final SetExpression expression) {
        return new SetExpressionInterpreter().interpret(expression, this.interpreter);
    }

    @Override
    public Object visit(final ThisExpression expression) {
        return this.interpreter.lookUpVariable(expression.keyword(), expression);
    }

    @Override
    public Object visit(final VariableExpression expression) {
        return this.interpreter.lookUpVariable(expression.name(), expression);
    }

    @Override
    public Object visit(final SuperExpression expression) {
        return new SuperExpressionInterpreter().interpret(expression, this.interpreter);
    }

    @Override
    public Void visit(final ExpressionStatement statement) {
        this.interpreter.evaluate(statement.expression());
        return null;
    }

    @Override
    public Void visit(PrintStatement statement) {
        return null;
    }

    @Override
    public Void visit(final BlockStatement statement) {
        this.interpreter.execute(statement.statements(), new VariableScope(this.interpreter.visitingScope()));
        return null;
    }

    @Override
    public Void visit(final IfStatement statement) {
        return new IfStatementInterpreter().interpret(statement, this.interpreter);
    }

    @Override
    public Void visit(final WhileStatement statement) {
        return new WhileStatementInterpreter().interpret(statement, this.interpreter);
    }

    @Override
    public Void visit(final DoWhileStatement statement) {
        return new DoWhileStatementInterpreter().interpret(statement, this.interpreter);
    }

    @Override
    public Void visit(final ForStatement statement) {
        return new ForStatementInterpreter().interpret(statement, this.interpreter);
    }

    @Override
    public Void visit(final ForEachStatement statement) {
        return new ForEachStatementInterpreter().interpret(statement, this.interpreter);
    }

    @Override
    public Void visit(final RepeatStatement statement) {
        return new RepeatStatementInterpreter().interpret(statement, this.interpreter);
    }

    @Override
    public Void visit(final VariableStatement statement) {
        return new VariableStatementInterpreter().interpret(statement, this.interpreter);
    }

    @Override
    public Void visit(final ReturnStatement statement) {
        return new ReturnStatementInterpreter().interpret(statement, this.interpreter);
    }

    @Override
    public Void visit(final ClassStatement statement) {
        return new ClassStatementInterpreter().interpret(statement, this.interpreter);
    }

    @Override
    public Void visit(final NativeFunctionStatement statement) {
        return new NativeFunctionStatementInterpreter().interpret(statement, this.interpreter);
    }

    @Override
    public Void visit(final TestStatement statement) {
        if (!this.interpreter.executionOptions().enableAssertions()) {
            return null;
        }
        else return new TestStatementInterpreter().interpret(statement, this.interpreter);
    }

    @Override
    public Void visit(final ModuleStatement statement) {
        return new ModuleStatementInterpreter().interpret(statement, this.interpreter);
    }

    @Override
    public Void visit(BreakStatement statement) {
        throw new MoveKeyword(MoveKeyword.MoveType.BREAK);
    }

    @Override
    public Void visit(ContinueStatement statement) {
        throw new MoveKeyword(MoveKeyword.MoveType.CONTINUE);
    }

    @Override
    public Void visit(final FunctionStatement statement) {
        return new FunctionStatementInterpreter().interpret(statement, this.interpreter);
    }

    @Override
    public Void visit(final FieldStatement statement) {
        return new FieldStatementInterpreter().interpret(statement, this.interpreter);
    }

    @Override
    public Void visit(final ConstructorStatement statement) {
        return new ConstructorStatementInterpreter().interpret(statement, this.interpreter);
    }

    @Override
    public Void visit(final SwitchStatement statement) {
        return new SwitchStatementInterpreter().interpret(statement, this.interpreter);
    }

    @Override
    public Void visit(final SwitchCase statement) {
        return new SwitchCaseInterpreter().interpret(statement, this.interpreter);
    }
}
