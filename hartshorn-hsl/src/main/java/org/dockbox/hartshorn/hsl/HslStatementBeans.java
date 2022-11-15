package org.dockbox.hartshorn.hsl;

import org.dockbox.hartshorn.beans.Bean;
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
public class HslStatementBeans {

    public static final String STATEMENT_BEAN = "statement";
    
    @Bean(id = STATEMENT_BEAN)
    public static ASTNodeParser<BlockStatement> blockStatementParser() {
        return new BlockStatementParser();
    }

    @Bean(id = STATEMENT_BEAN)
    public static ASTNodeParser<BreakStatement> breakStatementParser() {
        return new BreakStatementParser();
    }

    @Bean(id = STATEMENT_BEAN)
    public static ASTNodeParser<ClassStatement> classStatementParser() {
        return new ClassStatementParser(new FieldStatementParser());
    }

    @Bean(id = STATEMENT_BEAN)
    public static ASTNodeParser<ConstructorStatement> constructorStatementParser() {
        return new ConstructorStatementParser();
    }

    @Bean(id = STATEMENT_BEAN)
    public static ASTNodeParser<ContinueStatement> continueStatementParser() {
        return new ContinueStatementParser();
    }

    @Bean(id = STATEMENT_BEAN)
    public static ASTNodeParser<DoWhileStatement> doWhileStatementParser() {
        return new DoWhileStatementParser();
    }

    @Bean(id = STATEMENT_BEAN)
    public static ASTNodeParser<FinalizableStatement> finalDeclarationStatementParser() {
        return new FinalDeclarationStatementParser();
    }

    @Bean(id = STATEMENT_BEAN)
    public static ASTNodeParser<BodyStatement> forStatementParser() {
        return new ForStatementParser();
    }

    @Bean(id = STATEMENT_BEAN)
    public static ASTNodeParser<Function> functionStatementParser() {
        return new FunctionStatementParser();
    }

    @Bean(id = STATEMENT_BEAN)
    public static ASTNodeParser<IfStatement> ifStatementParser() {
        return new IfStatementParser();
    }

    @Bean(id = STATEMENT_BEAN)
    public static ASTNodeParser<ModuleStatement> moduleStatementParser() {
        return new ModuleStatementParser();
    }

    @Bean(id = STATEMENT_BEAN)
    public static ASTNodeParser<NativeFunctionStatement> nativeFunctionStatementParser() {
        return new NativeFunctionStatementParser();
    }

    @Bean(id = STATEMENT_BEAN)
    public static ASTNodeParser<RepeatStatement> repeatStatementParser() {
        return new RepeatStatementParser();
    }

    @Bean(id = STATEMENT_BEAN)
    public static ASTNodeParser<ReturnStatement> returnStatementParser() {
        return new ReturnStatementParser();
    }

    @Bean(id = STATEMENT_BEAN)
    public static ASTNodeParser<SwitchStatement> switchStatementParser() {
        return new SwitchStatementParser(new CaseBodyStatementParser());
    }

    @Bean(id = STATEMENT_BEAN)
    public static ASTNodeParser<TestStatement> testStatementParser() {
        return new TestStatementParser();
    }

    @Bean(id = STATEMENT_BEAN)
    public static ASTNodeParser<VariableStatement> variableDeclarationParser() {
        return new VariableDeclarationParser();
    }

    @Bean(id = STATEMENT_BEAN)
    public static ASTNodeParser<WhileStatement> whileStatementParser() {
        return new WhileStatementParser();
    }
}
