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

package org.dockbox.hartshorn.hsl.interpreter;

import java.util.List;
import java.util.Map;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.context.ContextCarrier;
import org.dockbox.hartshorn.hsl.ScriptEvaluationError;
import org.dockbox.hartshorn.hsl.ast.MoveKeyword;
import org.dockbox.hartshorn.hsl.extension.CustomASTNode;
import org.dockbox.hartshorn.hsl.ast.expression.Expression;
import org.dockbox.hartshorn.hsl.ast.statement.BlockStatement;
import org.dockbox.hartshorn.hsl.ast.statement.Statement;
import org.dockbox.hartshorn.hsl.modules.NativeModule;
import org.dockbox.hartshorn.hsl.runtime.ExecutionOptions;
import org.dockbox.hartshorn.hsl.runtime.Phase;
import org.dockbox.hartshorn.hsl.runtime.RuntimeError;
import org.dockbox.hartshorn.hsl.token.Token;
import org.slf4j.Logger;

/**
 * Standard interpreter for HSL. This interpreter is capable of executing HSL code by visiting the AST
 * step by step. The interpreter is capable of handling all types of statements and expressions. The
 * interpreter is also capable of handling external classes and modules, as well as virtual classes and
 * functions.
 *
 * <p>During the execution of a script, the interpreter will track its global variables in a
 * {@link VariableScope}, and report any results to the configured {@link ResultCollector}.
 *
 * <p>{@code print} statements are handled by the configured {@link Logger}, and are not persisted
 * in a local state.
 *
 * <p>Any interpreter instance can only be used <b>once</b>, and should be disposed of after use. This
 * is to prevent scope pollution, and potential leaking of errors and results.
 *
 * <p>Interpretation starts with the {@link #interpret(List)} method, which takes a list of statements
 * which have been previously parsed by a {@link org.dockbox.hartshorn.hsl.parser.ASTNodeParser}, and
 * preferably resolved by a {@link org.dockbox.hartshorn.hsl.semantic.Resolver}.
 *
 * @author Guus Lieben
 * @since 22.4
 */
public class Interpreter implements ContextCarrier, InterpreterAdapter {

    private final DelegatingInterpreterVisitor visitor = new DelegatingInterpreterVisitor(this);

    private final ApplicationContext applicationContext;
    private final ResultCollector resultCollector;
    private final InterpreterState state;


    private ExecutionOptions executionOptions;
    private boolean isRunning;

    public Interpreter(ResultCollector resultCollector,
                       Map<String, NativeModule> externalModules,
                       ApplicationContext applicationContext) {
        this(resultCollector, externalModules, new ExecutionOptions(), applicationContext);
    }

    public Interpreter(ResultCollector resultCollector,
                       Map<String, NativeModule> externalModules,
                       ExecutionOptions executionOptions,
                       ApplicationContext applicationContext) {
        this.resultCollector = resultCollector;
        this.applicationContext = applicationContext;
        this.state = new InterpreterState(externalModules, this);
        this.executionOptions = executionOptions;
    }

    /**
     * Restores the interpreter to its initial state. This is to prevent scope pollution, and potential
     * leaking of errors and results. This does not clear the external modules and variables, nor the
     * dynamic imports, as these can be reused safely.
     *
     * <p>This method should be called before starting a new runtime. This should be at least before a
     * potential {@link org.dockbox.hartshorn.hsl.semantic.Resolver} is called, as the resolver will
     * typically modify the {@link InterpreterState}.
     */
    public void restore() {
        this.state.restore();
        this.resultCollector.clear();
    }

    public InterpreterState state() {
        return this.state;
    }

    public ResultCollector resultCollector() {
        return this.resultCollector;
    }

    public Interpreter executionOptions(ExecutionOptions options) {
        this.executionOptions = options;
        return this;
    }

    public ExecutionOptions executionOptions() {
        return this.executionOptions;
    }

    public void interpret(List<Statement> statements) {
        if (this.isRunning) {
            throw new IllegalAccessException("Cannot reuse the same interpreter instance for multiple executions");
        }
        this.isRunning = true;
        try {
            for (Statement statement : statements) {
                this.execute(statement);
            }
        }
        catch (RuntimeError error) {
            throw new ScriptEvaluationError(error, Phase.INTERPRETING, error.token());
        }
        finally {
            this.isRunning = false;
        }
    }

    @Override
    public Object evaluate(Expression expression) {
        if (expression instanceof CustomASTNode<?,?> customASTNode) {
            return customASTNode.interpret(this.visitor.adapter());
        }
        else {
            return expression.accept(this.visitor);
        }
    }

    @Override
    public void execute(final Statement statement) {
        if (statement instanceof CustomASTNode<?,?> customASTNode) {
            customASTNode.interpret(this.visitor.adapter());
        }
        else {
            statement.accept(this.visitor);
        }
    }

    @Override
    public void execute(BlockStatement blockStatement, VariableScope localVariableScope) {
        this.execute(blockStatement.statements(), localVariableScope);
    }

    @Override
    public void execute(List<Statement> statementList, VariableScope localVariableScope) {
        this.state().withScope(localVariableScope, () -> {
            for (Statement statement : statementList) {
                try {
                    this.execute(statement);
                }
                catch (MoveKeyword type) {
                    if (type.moveType() == MoveKeyword.MoveType.CONTINUE) {
                        break;
                    }
                    // Handle in higher visitor call
                    throw type;
                }
            }
        });
    }

    @Override
    public Object lookUpVariable(Token name, Expression expr) {
        return this.state().lookUpVariable(name, expr);
    }

    @Override
    public void resolve(Expression expr, int depth) {
        this.state().resolve(expr, depth);
    }

    @Override
    public VariableScope global() {
        return this.state().global();
    }

    @Override
    public VariableScope visitingScope() {
        return this.state().visitingScope();
    }

    @Override
    public void withNextScope(Runnable runnable) {
        this.state().withNextScope(runnable);
    }

    @Override
    public void enterScope(VariableScope scope) {
        this.state().enterScope(scope);
    }

    @Override
    public Integer distance(Expression expr) {
        return this.state().distance(expr);
    }

    @Override
    public Interpreter interpreter() {
        return this;
    }

    @Override
    public ApplicationContext applicationContext() {
        return this.applicationContext;
    }
}
