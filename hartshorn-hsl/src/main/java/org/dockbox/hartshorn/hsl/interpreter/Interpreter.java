/*
 * Copyright 2019-2025 the original author or authors.
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

import org.dockbox.hartshorn.hsl.ast.expression.Expression;
import org.dockbox.hartshorn.hsl.ast.statement.BlockStatement;
import org.dockbox.hartshorn.hsl.ast.statement.Statement;
import org.dockbox.hartshorn.hsl.runtime.ExecutionOptions;
import org.dockbox.hartshorn.hsl.semantic.Resolver;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.TokenRegistry;
import org.dockbox.hartshorn.launchpad.ApplicationContext;

import java.util.List;

/**
 * Interpreters are responsible for evaluating expressions and executing statements. They maintain
 * the state of the current execution, including variable scopes and function call stacks.
 *
 * <p>Interpreters can be configured with different {@link ExecutionOptions options} to modify
 * their behavior, such as enabling or disabling certain language features. Interpreters also
 * provide mechanisms for error handling and reporting, allowing for graceful recovery from runtime
 * errors.
 *
 * <p>Interpreters are stateful and should not be shared between different execution contexts.
 * Each interpreter maintains its own state, including variable scopes and function call stacks.
 *
 * @since 0.4.12
 * 
 * @author Guus Lieben
 */
public interface Interpreter {

    /**
     * Evaluates the given expression and returns the result. The type of the result depends on the
     * expression being evaluated.
     *
     * @param expression the expression to evaluate
     *
     * @return the result of the evaluation
     */
    Object evaluate(Expression expression);

    /**
     * Executes the given statement. The behavior of this method depends on the type of statement
     * being executed. For example, executing a variable declaration statement will create a new
     * variable in the current scope, while executing a function call statement will invoke the
     * specified function.
     *
     * @param statement the statement to execute
     */
    void execute(Statement statement);

    /**
     * Executes a block statement within the given local variable scope. This method is used to
     * execute a series of statements that are grouped together in a block, such as the body of a
     * function or a conditional statement.
     *
     * @param blockStatement the block statement to execute
     * @param localVariableScope the local variable scope to use for the execution
     */
    void execute(BlockStatement blockStatement, VariableScope localVariableScope);

    /**
     * Executes a list of statements within the given local variable scope. This method is used to
     * execute multiple statements in sequence, such as the body of a script or a function.
     *
     * @param statementList the list of statements to execute
     * @param localVariableScope the local variable scope to use for the execution
     */
    void execute(List<Statement> statementList, VariableScope localVariableScope);

    /**
     * Looks up the value of a variable by its name within the context of a specific expression.
     * This method is used to resolve variable references during expression evaluation.
     *
     * @param name the token representing the variable name
     * @param expression the expression context in which the variable is being looked up
     *
     * @return the value of the variable, or null if the variable is not defined
     */
    Object lookUpVariable(Token name, Expression expression);

    /**
     * Resolves the given expression at the specified depth in the variable scope hierarchy. This
     * method is used to associate an expression with its corresponding variable scope, allowing for
     * proper variable symantic analysis during evaluation. As such, this method should only be
     * called during symantic analysis, before any evaluation takes place.
     *
     * @param expression the expression to resolve
     * @param depth the depth in the variable scope hierarchy where the expression is defined
     */
    void resolve(Expression expression, int depth);

    /**
     * Returns the current variable scope being visited by the interpreter. This scope represents
     * the context in which variables are currently being defined and accessed.
     *
     * @return the current variable scope
     */
    VariableScope visitingScope();

    /**
     * Returns the global variable scope, which is the outermost scope in the variable scope
     * hierarchy. The global scope contains variables that are accessible from any other scope
     * within the interpreter.
     *
     * @return the global variable scope
     */
    VariableScope global();

    /**
     * Executes the given runnable within a new variable scope that is a child of the current scope.
     * This method temporarily creates a new scope for the duration of the runnable's execution,
     * allowing for isolated variable definitions and access. After the runnable completes, the
     * interpreter returns to the previous scope.
     *
     * @param runnable the runnable to execute within the new scope
     */
    void withNextScope(Runnable runnable);

    /**
     * Enters the given variable scope, making it the current scope for subsequent variable
     * definitions and access.
     *
     * @param scope the variable scope to enter
     */
    void enterScope(VariableScope scope);

    /**
     * Calculates the distance of the given expression from the current scope. This distance
     * represents the number of scopes between the current scope and the scope in which the
     * expression is defined and is used for variable resolution.
     *
     * <p>The value returned by this method is typically set during the symantic analysis phase and
     * is used to optimize variable lookups during evaluation.
     *
     * @param expression the expression for which to calculate the distance
     *
     * @return the distance of the expression from the current scope
     */
    Integer distance(Expression expression);

    /**
     * Gets the application context associated with this interpreter.
     *
     * @return the application context
     */
    ApplicationContext applicationContext();

    /**
     * Gets the execution options currently configured for this interpreter.
     *
     * @return the execution options
     */
    ExecutionOptions executionOptions();

    /**
     * Sets the execution options for this interpreter.
     *
     * @param options the execution options to set
     *
     * @return the interpreter instance for method chaining
     */
    Interpreter executionOptions(ExecutionOptions options);

    /**
     * Gets the current state of the interpreter. This state includes information about variable
     * scopes, external modules, and other runtime data necessary for interpretation. This state is
     * not thread-safe and should only be accessed by interpreter instances within their own
     * execution context.
     *
     * @return the interpreter state
     */
    InterpreterState state();

    /**
     * Gets the token registry used by this interpreter. Most token information is not required
     * during interpretation, but some features may require access to token metadata, such as
     * virtual functions that need to create new (virtual) tokens during execution.
     *
     * @return the token registry
     */
    TokenRegistry tokenRegistry();

    /**
     * Gets the result collector used by this interpreter to collect results from executed
     * statements and expressions. The result collector allows for capturing and managing the
     * outcomes of interpretation.
     *
     * @return the result collector
     */
    ResultCollector resultCollector();

    /**
     * Interprets a list of statements. This method serves as the entry point for executing a series
     * of statements within the interpreter.
     *
     * @param statements the list of statements to interpret
     *
     * @throws ConcurrentInterpreterExecutionException if the interpreter is already executing
     * statements
     */
    void interpret(List<Statement> statements) throws ConcurrentInterpreterExecutionException;

    /**
     * Restores the interpreter to its initial state. This is to prevent scope pollution, and
     * potential leaking of errors and results. This does not clear the external modules and
     * variables, nor the dynamic imports, as these can be reused safely.
     *
     * <p>This method should be called before starting a new runtime. This should be at least before
     * a
     * potential {@link Resolver} is called, as the resolver will typically modify the
     * {@link #state() interpreter state}.
     */
    void restore();
}
