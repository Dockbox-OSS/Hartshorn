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

import java.util.List;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.context.ContextCarrier;
import org.dockbox.hartshorn.hsl.ast.MoveKeyword;
import org.dockbox.hartshorn.hsl.ast.expression.Expression;
import org.dockbox.hartshorn.hsl.ast.statement.BlockStatement;
import org.dockbox.hartshorn.hsl.ast.statement.Statement;
import org.dockbox.hartshorn.hsl.extension.CustomASTNode;
import org.dockbox.hartshorn.hsl.runtime.ExecutionOptions;
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
public class SimpleVisitorInterpreter implements ContextCarrier, Interpreter {

    private final InterpreterVisitor visitor = new DelegatingInterpreterVisitor(this);

    private final ApplicationContext applicationContext;
    private final ResultCollector resultCollector;
    private final InterpreterState state;

    private ExecutionOptions executionOptions = new ExecutionOptions();
    private boolean isRunning;

    public SimpleVisitorInterpreter(
            final ResultCollector resultCollector,
            final ApplicationContext applicationContext
    ) {
        this.resultCollector = resultCollector;
        this.applicationContext = applicationContext;
        this.state = new InterpreterState(this);
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
    @Override
    public void restore() {
        this.state.restore();
        this.resultCollector.clear();
    }

    @Override
    public InterpreterState state() {
        return this.state;
    }

    @Override
    public ResultCollector resultCollector() {
        return this.resultCollector;
    }

    @Override
    public Interpreter executionOptions(final ExecutionOptions options) {
        this.executionOptions = options;
        return this;
    }

    @Override
    public ExecutionOptions executionOptions() {
        return this.executionOptions;
    }

    @Override
    public void interpret(final List<Statement> statements) {
        if (this.isRunning) {
            throw new IllegalAccessException("Cannot reuse the same interpreter instance for multiple executions");
        }
        this.isRunning = true;
        try {
            for (final Statement statement : statements) {
                this.execute(statement);
            }
        }
        catch (final RuntimeError error) {
            InterpreterErrorDefinition.RUNTIME_ERROR.define()
                    .cause(error)
                    .at(error.token())
                    .raise();
        }
        finally {
            this.isRunning = false;
        }
    }

    @Override
    public Object evaluate(final Expression expression) {
        if (expression instanceof CustomASTNode<?,?> customASTNode) {
            return customASTNode.interpret(this.visitor.interpreter());
        }
        else {
            return expression.accept(this.visitor);
        }
    }

    @Override
    public void execute(final Statement statement) {
        if (statement instanceof CustomASTNode<?,?> customASTNode) {
            customASTNode.interpret(this.visitor.interpreter());
        }
        else {
            statement.accept(this.visitor);
        }
    }

    @Override
    public void execute(final BlockStatement blockStatement, final VariableScope localVariableScope) {
        this.execute(blockStatement.statements(), localVariableScope);
    }

    @Override
    public void execute(final List<Statement> statementList, final VariableScope localVariableScope) {
        this.state().withScope(localVariableScope, () -> {
            for (final Statement statement : statementList) {
                try {
                    this.execute(statement);
                }
                catch (final MoveKeyword type) {
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
    public Object lookUpVariable(final Token name, final Expression expression) {
        return this.state().lookUpVariable(name, expression);
    }

    @Override
    public void resolve(final Expression expression, final int depth) {
        this.state().resolve(expression, depth);
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
    public void withNextScope(final Runnable runnable) {
        this.state().withNextScope(runnable);
    }

    @Override
    public void enterScope(final VariableScope scope) {
        this.state().enterScope(scope);
    }

    @Override
    public Integer distance(final Expression expression) {
        return this.state().distance(expression);
    }

    @Override
    public ApplicationContext applicationContext() {
        return this.applicationContext;
    }
}
