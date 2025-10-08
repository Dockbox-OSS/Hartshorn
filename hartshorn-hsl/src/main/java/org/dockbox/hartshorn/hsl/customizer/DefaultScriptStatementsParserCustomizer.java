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

package org.dockbox.hartshorn.hsl.customizer;

import org.dockbox.hartshorn.hsl.ParserCustomizer;
import org.dockbox.hartshorn.hsl.ScriptLanguageConfiguration;
import org.dockbox.hartshorn.hsl.parser.TokenParser;
import org.dockbox.hartshorn.hsl.parser.expression.AssignExpressionParser;
import org.dockbox.hartshorn.hsl.parser.expression.BinaryAdditionExpressionParser;
import org.dockbox.hartshorn.hsl.parser.expression.BinaryComparisonExpressionParser;
import org.dockbox.hartshorn.hsl.parser.expression.BinaryEqualityExpressionParser;
import org.dockbox.hartshorn.hsl.parser.expression.BinaryMultiplicationExpressionParser;
import org.dockbox.hartshorn.hsl.parser.expression.BitwiseExpressionParser;
import org.dockbox.hartshorn.hsl.parser.expression.CallExpressionParser;
import org.dockbox.hartshorn.hsl.parser.expression.ComplexArrayExpressionParser;
import org.dockbox.hartshorn.hsl.parser.expression.ElvisExpressionParser;
import org.dockbox.hartshorn.hsl.parser.expression.GroupingExpressionParser;
import org.dockbox.hartshorn.hsl.parser.expression.IdentifierExpressionParser;
import org.dockbox.hartshorn.hsl.parser.expression.InfixExpressionParser;
import org.dockbox.hartshorn.hsl.parser.expression.LiteralExpressionParser;
import org.dockbox.hartshorn.hsl.parser.expression.LogicalAssignExpressionParser;
import org.dockbox.hartshorn.hsl.parser.expression.LogicalExpressionParser;
import org.dockbox.hartshorn.hsl.parser.expression.PrefixExpressionParser;
import org.dockbox.hartshorn.hsl.parser.expression.RangeExpressionParser;
import org.dockbox.hartshorn.hsl.parser.expression.TernaryExpressionParser;
import org.dockbox.hartshorn.hsl.parser.expression.UnaryExpressionParser;
import org.dockbox.hartshorn.hsl.parser.statement.BlockStatementParser;
import org.dockbox.hartshorn.hsl.parser.statement.BreakStatementParser;
import org.dockbox.hartshorn.hsl.parser.statement.CaseBodyStatementParser;
import org.dockbox.hartshorn.hsl.parser.statement.ClassStatementParser;
import org.dockbox.hartshorn.hsl.parser.statement.ConstructorStatementParser;
import org.dockbox.hartshorn.hsl.parser.statement.ContinueStatementParser;
import org.dockbox.hartshorn.hsl.parser.statement.DoWhileStatementParser;
import org.dockbox.hartshorn.hsl.parser.statement.FieldStatementParser;
import org.dockbox.hartshorn.hsl.parser.statement.FinalDeclarationStatementParser;
import org.dockbox.hartshorn.hsl.parser.statement.ForStatementParser;
import org.dockbox.hartshorn.hsl.parser.statement.FunctionStatementParser;
import org.dockbox.hartshorn.hsl.parser.statement.IfStatementParser;
import org.dockbox.hartshorn.hsl.parser.statement.ModuleStatementParser;
import org.dockbox.hartshorn.hsl.parser.statement.NativeFunctionStatementParser;
import org.dockbox.hartshorn.hsl.parser.statement.RepeatStatementParser;
import org.dockbox.hartshorn.hsl.parser.statement.ReturnStatementParser;
import org.dockbox.hartshorn.hsl.parser.statement.SwitchStatementParser;
import org.dockbox.hartshorn.hsl.parser.statement.TestStatementParser;
import org.dockbox.hartshorn.hsl.parser.statement.VariableDeclarationParser;
import org.dockbox.hartshorn.hsl.parser.statement.WhileStatementParser;

/**
 * A default implementation of {@link ParserCustomizer} that configures the {@link TokenParser} with the default
 * statement parsers. This customizer is used by default by the {@link ScriptLanguageConfiguration}.
 *
 * @see ScriptLanguageConfiguration
 * @see ParserCustomizer
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public class DefaultScriptStatementsParserCustomizer implements ParserCustomizer {

    @Override
    public void configure(TokenParser target) {
        // Statement parsers
        target.statementParser(new BlockStatementParser());
        target.statementParser(new BreakStatementParser());
        target.statementParser(new ClassStatementParser(new FieldStatementParser()));
        target.statementParser(new ConstructorStatementParser());
        target.statementParser(new ContinueStatementParser());
        target.statementParser(new DoWhileStatementParser());
        target.statementParser(new FinalDeclarationStatementParser());
        target.statementParser(new ForStatementParser());
        target.statementParser(new FunctionStatementParser());
        target.statementParser(new IfStatementParser());
        target.statementParser(new ModuleStatementParser());
        target.statementParser(new NativeFunctionStatementParser());
        target.statementParser(new RepeatStatementParser());
        target.statementParser(new ReturnStatementParser());
        target.statementParser(new SwitchStatementParser(new CaseBodyStatementParser()));
        target.statementParser(new TestStatementParser());
        target.statementParser(new VariableDeclarationParser());
        target.statementParser(new WhileStatementParser());

        // Expression parsers
        target.expressionParser(new AssignExpressionParser());
        target.expressionParser(new ElvisExpressionParser());
        target.expressionParser(new TernaryExpressionParser());
        target.expressionParser(new BitwiseExpressionParser());
        target.expressionParser(new LogicalExpressionParser());
        target.expressionParser(new BinaryEqualityExpressionParser());
        target.expressionParser(new RangeExpressionParser());
        target.expressionParser(new LogicalAssignExpressionParser(target.tokenRegistry()));
        target.expressionParser(new PrefixExpressionParser());
        target.expressionParser(new BinaryComparisonExpressionParser());
        target.expressionParser(new BinaryAdditionExpressionParser());
        target.expressionParser(new BinaryMultiplicationExpressionParser());
        target.expressionParser(new InfixExpressionParser());
        target.expressionParser(new UnaryExpressionParser());
        target.expressionParser(new CallExpressionParser());
        target.expressionParser(new LiteralExpressionParser());
        target.expressionParser(new IdentifierExpressionParser());
        target.expressionParser(new GroupingExpressionParser());
        target.expressionParser(new ComplexArrayExpressionParser());
    }
}
