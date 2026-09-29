/*
 * Copyright 2019-2026 the original author or authors.
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

package org.dockbox.hartshorn.hsl.compiler;

import org.dockbox.hartshorn.hsl.ast.expression.*;
import org.dockbox.hartshorn.hsl.ast.statement.*;
import org.dockbox.hartshorn.hsl.compiler.expression.*;
import org.dockbox.hartshorn.hsl.compiler.runtime.CompiledScriptExecutable;
import org.dockbox.hartshorn.hsl.compiler.runtime.HslRuntimeSupport;
import org.dockbox.hartshorn.hsl.compiler.statement.*;
import org.dockbox.hartshorn.hsl.customizer.ScriptContext;
import org.dockbox.hartshorn.hsl.extension.CustomASTNode;
import org.dockbox.hartshorn.hsl.visitors.ExpressionVisitor;
import org.dockbox.hartshorn.hsl.visitors.StatementVisitor;

import java.lang.classfile.ClassFile;
import java.lang.classfile.CodeBuilder;
import java.lang.constant.ClassDesc;
import java.lang.constant.ConstantDescs;
import java.lang.constant.MethodTypeDesc;
import java.util.List;

/**
 * Delegating AST visitor that compiles HSL statements and expressions into standard JVM bytecode using
 * individual {@link ASTNodeCompiler} implementations for each node type.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public class BytecodeCompilerVisitor implements StatementVisitor<Void>, ExpressionVisitor<Void>, CompilationChain {

    private final CompilationContext context;
    private CodeBuilder code;

    public BytecodeCompilerVisitor(CompilationContext context) {
        this.context = context;
    }

    public CompilationContext context() {
        return this.context;
    }

    public CodeBuilder code() {
        return this.code;
    }

    @Override
    public void fork(CodeBuilder code, Runnable runnable) {
        CodeBuilder outer = this.code;
        this.code = code;
        runnable.run();
        this.code = outer;
    }

    /**
     * Compiles the provided statements into bytecode. The statements are wrapped in a {@link CompiledScriptExecutable},
     * specifically in {@link CompiledScriptExecutable#execute(ScriptContext)}. The compiled bytecode is returned.
     *
     * @param statements The statements to compile.
     * @return The compiled bytecode.
     */
    public byte[] compile(List<Statement> statements) {
        return ClassFile.of().build(
            ClassDesc.of(this.context.className()),
            classBuilder -> {
                classBuilder.withFlags(ClassFile.ACC_PUBLIC);
                classBuilder.withInterfaceSymbols(ASTNodeCompiler.CD_CompiledScriptExecutable);

                // Default constructor
                classBuilder.withMethod(
                    ConstantDescs.INIT_NAME,
                    ConstantDescs.MTD_void,
                    ClassFile.ACC_PUBLIC,
                    methodBuilder -> methodBuilder.withCode(codeBuilder -> {
                        codeBuilder.aload(0);
                        codeBuilder.invokespecial(ConstantDescs.CD_Object, ConstantDescs.INIT_NAME, ConstantDescs.MTD_void);
                        codeBuilder.return_();
                    })
                );

                // execute(ScriptContext context)
                classBuilder.withMethod(
                    "execute",
                    MethodTypeDesc.of(ConstantDescs.CD_Object, ASTNodeCompiler.CD_ScriptContext),
                    ClassFile.ACC_PUBLIC,
                    methodBuilder -> methodBuilder.withCode(codeBuilder -> {
                        this.code = codeBuilder;

                        // Initialize root scope: VariableScope scope = HslRuntimeSupport.createRootScope(context);
                        codeBuilder.aload(CompilationContext.CONTEXT_SLOT);
                        codeBuilder.invokestatic(ASTNodeCompiler.CD_HslRuntimeSupport, "createRootScope", HslRuntimeSupport.MTD_CreateRootScope);
                        codeBuilder.astore(CompilationContext.SCOPE_SLOT);

                        for (Statement statement : statements) {
                            this.compile(statement);
                        }

                        // Return null if execute didn't explicitly return
                        codeBuilder.aconst_null();
                        codeBuilder.areturn();
                    })
                );
            }
        );
    }

    @Override
    public Void visit(BlockStatement statement) {
        new BlockStatementCompiler().compile(statement, this.code, this.context, this);
        return null;
    }

    @Override
    public Void visit(VariableStatement statement) {
        new VariableStatementCompiler().compile(statement, this.code, this.context, this);
        return null;
    }

    @Override
    public Void visit(ExpressionStatement statement) {
        new ExpressionStatementCompiler().compile(statement, this.code, this.context, this);
        return null;
    }

    @Override
    public Void visit(IfStatement statement) {
        new IfStatementCompiler().compile(statement, this.code, this.context, this);
        return null;
    }

    @Override
    public Void visit(WhileStatement statement) {
        new WhileStatementCompiler().compile(statement, this.code, this.context, this);
        return null;
    }

    @Override
    public Void visit(DoWhileStatement statement) {
        new DoWhileStatementCompiler().compile(statement, this.code, this.context, this);
        return null;
    }

    @Override
    public Void visit(ForStatement statement) {
        new ForStatementCompiler().compile(statement, this.code, this.context, this);
        return null;
    }

    @Override
    public Void visit(RepeatStatement statement) {
        new RepeatStatementCompiler().compile(statement, this.code, this.context, this);
        return null;
    }

    @Override
    public Void visit(BreakStatement statement) {
        new BreakStatementCompiler().compile(statement, this.code, this.context, this);
        return null;
    }

    @Override
    public Void visit(ContinueStatement statement) {
        new ContinueStatementCompiler().compile(statement, this.code, this.context, this);
        return null;
    }

    @Override
    public Void visit(ReturnStatement statement) {
        new ReturnStatementCompiler().compile(statement, this.code, this.context, this);
        return null;
    }

    @Override
    public Void visit(FunctionStatement statement) {
        new FunctionStatementCompiler().compile(statement, this.code, this.context, this);
        return null;
    }

    @Override
    public Void visit(ClassStatement statement) {
        new ClassStatementCompiler().compile(statement, this.code, this.context, this);
        return null;
    }

    @Override
    public Void visit(ConstructorStatement statement) {
        new ConstructorStatementCompiler().compile(statement, this.code, this.context, this);
        return null;
    }

    @Override
    public Void visit(FieldStatement statement) {
        new FieldStatementCompiler().compile(statement, this.code, this.context, this);
        return null;
    }

    @Override
    public Void visit(FieldGetStatement statement) {
        new FieldGetStatementCompiler().compile(statement, this.code, this.context, this);
        return null;
    }

    @Override
    public Void visit(FieldSetStatement statement) {
        new FieldSetStatementCompiler().compile(statement, this.code, this.context, this);
        return null;
    }

    @Override
    public Void visit(ForEachStatement statement) {
        new ForEachStatementCompiler().compile(statement, this.code, this.context, this);
        return null;
    }

    @Override
    public Void visit(NativeFunctionStatement statement) {
        new NativeFunctionStatementCompiler().compile(statement, this.code, this.context, this);
        return null;
    }

    @Override
    public Void visit(TestStatement statement) {
        new TestStatementCompiler().compile(statement, this.code, this.context, this);
        return null;
    }

    @Override
    public Void visit(ModuleStatement statement) {
        new ModuleStatementCompiler().compile(statement, this.code, this.context, this);
        return null;
    }

    @Override
    public Void visit(SwitchStatement statement) {
        new SwitchStatementCompiler().compile(statement, this.code, this.context, this);
        return null;
    }

    @Override
    public Void visit(SwitchCase statement) {
        new SwitchCaseCompiler().compile(statement, this.code, this.context, this);
        return null;
    }

    @Override
    public Void visit(LiteralExpression expression) {
        new LiteralExpressionCompiler().compile(expression, this.code, this.context, this);
        return null;
    }

    @Override
    public Void visit(VariableExpression expression) {
        new VariableExpressionCompiler().compile(expression, this.code, this.context, this);
        return null;
    }

    @Override
    public Void visit(AssignExpression expression) {
        new AssignExpressionCompiler().compile(expression, this.code, this.context, this);
        return null;
    }

    @Override
    public Void visit(BinaryExpression expression) {
        new BinaryExpressionCompiler().compile(expression, this.code, this.context, this);
        return null;
    }

    @Override
    public Void visit(BitwiseExpression expression) {
        new BitwiseExpressionCompiler().compile(expression, this.code, this.context, this);
        return null;
    }

    @Override
    public Void visit(LogicalExpression expression) {
        new LogicalExpressionCompiler().compile(expression, this.code, this.context, this);
        return null;
    }

    @Override
    public Void visit(LogicalAssignExpression expression) {
        new LogicalAssignExpressionCompiler().compile(expression, this.code, this.context, this);
        return null;
    }

    @Override
    public Void visit(ElvisExpression expression) {
        new ElvisExpressionCompiler().compile(expression, this.code, this.context, this);
        return null;
    }

    @Override
    public Void visit(TernaryExpression expression) {
        new TernaryExpressionCompiler().compile(expression, this.code, this.context, this);
        return null;
    }

    @Override
    public Void visit(UnaryExpression expression) {
        new UnaryExpressionCompiler().compile(expression, this.code, this.context, this);
        return null;
    }

    @Override
    public Void visit(RangeExpression expression) {
        new RangeExpressionCompiler().compile(expression, this.code, this.context, this);
        return null;
    }

    @Override
    public Void visit(FunctionCallExpression expression) {
        new FunctionCallExpressionCompiler().compile(expression, this.code, this.context, this);
        return null;
    }

    @Override
    public Void visit(GetExpression expression) {
        new GetExpressionCompiler().compile(expression, this.code, this.context, this);
        return null;
    }

    @Override
    public Void visit(SetExpression expression) {
        new SetExpressionCompiler().compile(expression, this.code, this.context, this);
        return null;
    }

    @Override
    public Void visit(ArrayGetExpression expression) {
        new ArrayGetExpressionCompiler().compile(expression, this.code, this.context, this);
        return null;
    }

    @Override
    public Void visit(ArraySetExpression expression) {
        new ArraySetExpressionCompiler().compile(expression, this.code, this.context, this);
        return null;
    }

    @Override
    public Void visit(ArrayLiteralExpression expression) {
        new ArrayLiteralExpressionCompiler().compile(expression, this.code, this.context, this);
        return null;
    }

    @Override
    public Void visit(ArrayComprehensionExpression expression) {
        new ArrayComprehensionExpressionCompiler().compile(expression, this.code, this.context, this);
        return null;
    }

    @Override
    public Void visit(GroupingExpression expression) {
        new GroupingExpressionCompiler().compile(expression, this.code, this.context, this);
        return null;
    }

    @Override
    public Void visit(PostfixExpression expression) {
        new PostfixExpressionCompiler().compile(expression, this.code, this.context, this);
        return null;
    }

    @Override
    public Void visit(PrefixExpression expression) {
        new PrefixExpressionCompiler().compile(expression, this.code, this.context, this);
        return null;
    }

    @Override
    public Void visit(InfixExpression expression) {
        new InfixExpressionCompiler().compile(expression, this.code, this.context, this);
        return null;
    }

    @Override
    public Void visit(ThisExpression expression) {
        new ThisExpressionCompiler().compile(expression, this.code, this.context, this);
        return null;
    }

    @Override
    public Void visit(SuperExpression expression) {
        new SuperExpressionCompiler().compile(expression, this.code, this.context, this);
        return null;
    }

    @Override
    public void compile(Statement statement) {
        if (statement instanceof CustomASTNode<?, ?> customASTNode) {
            customASTNode.compile(this.code, this.context, this);
        }
        else {
            statement.accept(this);
        }
    }

    @Override
    public void compile(Expression expression) {
        if (expression instanceof CustomASTNode<?, ?> customASTNode) {
            customASTNode.compile(this.code, this.context, this);
        }
        else {
            expression.accept(this);
        }
    }
}
