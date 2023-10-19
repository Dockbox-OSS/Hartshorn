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

import org.dockbox.hartshorn.component.Service;
import org.dockbox.hartshorn.component.condition.RequiresActivator;
import org.dockbox.hartshorn.component.processing.Binds;
import org.dockbox.hartshorn.component.processing.Binds.BindingType;
import org.dockbox.hartshorn.hsl.ast.statement.Statement;
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
public class DefaultScriptStatementsProvider {

    public static final String STATEMENT_BEAN = "statement";
    
    @Binds(value = STATEMENT_BEAN, type = BindingType.COLLECTION)
    public ASTNodeParser<? extends Statement> blockStatementParser() {
        return new BlockStatementParser();
    }

    @Binds(value = STATEMENT_BEAN, type = BindingType.COLLECTION)
    public ASTNodeParser<? extends Statement> breakStatementParser() {
        return new BreakStatementParser();
    }

    @Binds(value = STATEMENT_BEAN, type = BindingType.COLLECTION)
    public ASTNodeParser<? extends Statement> classStatementParser() {
        return new ClassStatementParser(new FieldStatementParser());
    }

    @Binds(value = STATEMENT_BEAN, type = BindingType.COLLECTION)
    public ASTNodeParser<? extends Statement> constructorStatementParser() {
        return new ConstructorStatementParser();
    }

    @Binds(value = STATEMENT_BEAN, type = BindingType.COLLECTION)
    public ASTNodeParser<? extends Statement> continueStatementParser() {
        return new ContinueStatementParser();
    }

    @Binds(value = STATEMENT_BEAN, type = BindingType.COLLECTION)
    public ASTNodeParser<? extends Statement> doWhileStatementParser() {
        return new DoWhileStatementParser();
    }

    @Binds(value = STATEMENT_BEAN, type = BindingType.COLLECTION)
    public ASTNodeParser<? extends Statement> finalDeclarationStatementParser() {
        return new FinalDeclarationStatementParser();
    }

    @Binds(value = STATEMENT_BEAN, type = BindingType.COLLECTION)
    public ASTNodeParser<? extends Statement> forStatementParser() {
        return new ForStatementParser();
    }

    @Binds(value = STATEMENT_BEAN, type = BindingType.COLLECTION)
    public ASTNodeParser<? extends Statement> functionStatementParser() {
        return new FunctionStatementParser();
    }

    @Binds(value = STATEMENT_BEAN, type = BindingType.COLLECTION)
    public ASTNodeParser<? extends Statement> ifStatementParser() {
        return new IfStatementParser();
    }

    @Binds(value = STATEMENT_BEAN, type = BindingType.COLLECTION)
    public ASTNodeParser<? extends Statement> moduleStatementParser() {
        return new ModuleStatementParser();
    }

    @Binds(value = STATEMENT_BEAN, type = BindingType.COLLECTION)
    public ASTNodeParser<? extends Statement> nativeFunctionStatementParser() {
        return new NativeFunctionStatementParser();
    }

    @Binds(value = STATEMENT_BEAN, type = BindingType.COLLECTION)
    public ASTNodeParser<? extends Statement> repeatStatementParser() {
        return new RepeatStatementParser();
    }

    @Binds(value = STATEMENT_BEAN, type = BindingType.COLLECTION)
    public ASTNodeParser<? extends Statement> returnStatementParser() {
        return new ReturnStatementParser();
    }

    @Binds(value = STATEMENT_BEAN, type = BindingType.COLLECTION)
    public ASTNodeParser<? extends Statement> switchStatementParser() {
        return new SwitchStatementParser(new CaseBodyStatementParser());
    }

    @Binds(value = STATEMENT_BEAN, type = BindingType.COLLECTION)
    public ASTNodeParser<? extends Statement> testStatementParser() {
        return new TestStatementParser();
    }

    @Binds(value = STATEMENT_BEAN, type = BindingType.COLLECTION)
    public ASTNodeParser<? extends Statement> variableDeclarationParser() {
        return new VariableDeclarationParser();
    }

    @Binds(value = STATEMENT_BEAN, type = BindingType.COLLECTION)
    public ASTNodeParser<? extends Statement> whileStatementParser() {
        return new WhileStatementParser();
    }
}
