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

package org.dockbox.hartshorn.hsl;

import org.dockbox.hartshorn.component.contextual.StaticBinds;
import org.dockbox.hartshorn.component.Service;
import org.dockbox.hartshorn.component.condition.RequiresActivator;
import org.dockbox.hartshorn.hsl.ast.statement.BlockStatement;
import org.dockbox.hartshorn.hsl.ast.statement.BodyStatement;
import org.dockbox.hartshorn.hsl.ast.statement.BreakStatement;
import org.dockbox.hartshorn.hsl.ast.statement.ClassStatement;
import org.dockbox.hartshorn.hsl.ast.statement.ConstructorStatement;
import org.dockbox.hartshorn.hsl.ast.statement.ContinueStatement;
import org.dockbox.hartshorn.hsl.ast.statement.DoWhileStatement;
import org.dockbox.hartshorn.hsl.ast.statement.FinalizableStatement;
import org.dockbox.hartshorn.hsl.ast.statement.Function;
import org.dockbox.hartshorn.hsl.ast.statement.IfStatement;
import org.dockbox.hartshorn.hsl.ast.statement.ModuleStatement;
import org.dockbox.hartshorn.hsl.ast.statement.NativeFunctionStatement;
import org.dockbox.hartshorn.hsl.ast.statement.RepeatStatement;
import org.dockbox.hartshorn.hsl.ast.statement.ReturnStatement;
import org.dockbox.hartshorn.hsl.ast.statement.SwitchStatement;
import org.dockbox.hartshorn.hsl.ast.statement.TestStatement;
import org.dockbox.hartshorn.hsl.ast.statement.VariableStatement;
import org.dockbox.hartshorn.hsl.ast.statement.WhileStatement;
import org.dockbox.hartshorn.hsl.parser.ASTNodeParser;
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

@Service
@RequiresActivator(UseExpressionValidation.class)
public class StatementStaticProviders {

    public static final String STATEMENT_BEAN = "statement";
    
    @StaticBinds(id = STATEMENT_BEAN)
    public static ASTNodeParser<BlockStatement> blockStatementParser() {
        return new BlockStatementParser();
    }

    @StaticBinds(id = STATEMENT_BEAN)
    public static ASTNodeParser<BreakStatement> breakStatementParser() {
        return new BreakStatementParser();
    }

    @StaticBinds(id = STATEMENT_BEAN)
    public static ASTNodeParser<ClassStatement> classStatementParser() {
        return new ClassStatementParser(new FieldStatementParser());
    }

    @StaticBinds(id = STATEMENT_BEAN)
    public static ASTNodeParser<ConstructorStatement> constructorStatementParser() {
        return new ConstructorStatementParser();
    }

    @StaticBinds(id = STATEMENT_BEAN)
    public static ASTNodeParser<ContinueStatement> continueStatementParser() {
        return new ContinueStatementParser();
    }

    @StaticBinds(id = STATEMENT_BEAN)
    public static ASTNodeParser<DoWhileStatement> doWhileStatementParser() {
        return new DoWhileStatementParser();
    }

    @StaticBinds(id = STATEMENT_BEAN)
    public static ASTNodeParser<FinalizableStatement> finalDeclarationStatementParser() {
        return new FinalDeclarationStatementParser();
    }

    @StaticBinds(id = STATEMENT_BEAN)
    public static ASTNodeParser<BodyStatement> forStatementParser() {
        return new ForStatementParser();
    }

    @StaticBinds(id = STATEMENT_BEAN)
    public static ASTNodeParser<Function> functionStatementParser() {
        return new FunctionStatementParser();
    }

    @StaticBinds(id = STATEMENT_BEAN)
    public static ASTNodeParser<IfStatement> ifStatementParser() {
        return new IfStatementParser();
    }

    @StaticBinds(id = STATEMENT_BEAN)
    public static ASTNodeParser<ModuleStatement> moduleStatementParser() {
        return new ModuleStatementParser();
    }

    @StaticBinds(id = STATEMENT_BEAN)
    public static ASTNodeParser<NativeFunctionStatement> nativeFunctionStatementParser() {
        return new NativeFunctionStatementParser();
    }

    @StaticBinds(id = STATEMENT_BEAN)
    public static ASTNodeParser<RepeatStatement> repeatStatementParser() {
        return new RepeatStatementParser();
    }

    @StaticBinds(id = STATEMENT_BEAN)
    public static ASTNodeParser<ReturnStatement> returnStatementParser() {
        return new ReturnStatementParser();
    }

    @StaticBinds(id = STATEMENT_BEAN)
    public static ASTNodeParser<SwitchStatement> switchStatementParser() {
        return new SwitchStatementParser(new CaseBodyStatementParser());
    }

    @StaticBinds(id = STATEMENT_BEAN)
    public static ASTNodeParser<TestStatement> testStatementParser() {
        return new TestStatementParser();
    }

    @StaticBinds(id = STATEMENT_BEAN)
    public static ASTNodeParser<VariableStatement> variableDeclarationParser() {
        return new VariableDeclarationParser();
    }

    @StaticBinds(id = STATEMENT_BEAN)
    public static ASTNodeParser<WhileStatement> whileStatementParser() {
        return new WhileStatementParser();
    }
}
