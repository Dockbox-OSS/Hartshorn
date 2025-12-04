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

package org.dockbox.hartshorn.hsl.semantic;

import org.dockbox.hartshorn.hsl.ScriptEvaluationError;
import org.dockbox.hartshorn.hsl.ast.FlowControlKeyword;
import org.dockbox.hartshorn.hsl.ast.NamedNode;
import org.dockbox.hartshorn.hsl.ast.expression.Expression;
import org.dockbox.hartshorn.hsl.ast.statement.ParametricExecutableStatement;
import org.dockbox.hartshorn.hsl.ast.statement.Statement;
import org.dockbox.hartshorn.hsl.extension.CustomExpression;
import org.dockbox.hartshorn.hsl.extension.CustomStatement;
import org.dockbox.hartshorn.hsl.interpreter.Interpreter;
import org.dockbox.hartshorn.hsl.objects.Finalizable;
import org.dockbox.hartshorn.hsl.token.Token;

import java.util.List;
import java.util.Map;

/**
 * Resolver is responsible for performing semantic analysis and type checking before a collection of
 * statements is interpreted. This allows illegal references to identifiers to be found and reported
 * early on, preventing potential runtime errors.
 *
 * <p>Additionally, resolvers manage variable scopes, ensuring that variables are declared and
 * defined correctly according to their scope. As a result, variable lookups can be accurately and
 * efficiently performed during interpretation.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public interface Resolver {

    /**
     * Resolves the given expression. This method will resolve the expression by passing it to the
     * active {@link ResolverVisitor} if it is compatible, or by directly passing the current
     * resolver to the expression if it is a {@link CustomExpression}.
     *
     * @param expression The expression to resolve
     */
    void resolve(Expression expression);

    /**
     * Resolves the given statement. This method will resolve the statement by passing it to the
     * active {@link ResolverVisitor} if it is compatible, or by directly passing the current
     * resolver to the statement if it is a {@link CustomStatement}.
     *
     * @param statement The statement to resolve
     */
    void resolve(Statement statement);

    /**
     * Resolves the given list of statements. This method will resolve each statement in the list
     * by calling {@link #resolve(Statement)}.
     *
     * @param statements The list of statements to resolve
     */
    void resolve(List<Statement> statements);

    /**
     * Checks whether the given name token is a final variable. If it is, and it has already been
     * defined, an error is thrown.
     *
     * @param name The name token of the variable to check
     *
     * @throws ScriptEvaluationError If the variable is final and has already been defined
     */
    void checkFinal(Token name);

    /**
     * Resolves the given name token in the active scopes. This method will resolve the name token
     * by passing it to the active {@link Interpreter} to resolve the expression in the given scope.
     *
     * <p>If the name is not known (declared) in any currently known scope, it is assumed to be a
     * global variable. This is not an error, as this is a valid use-case for scripting through
     * managed runtimes.
     *
     * @param expression The expression that contains the name token
     * @param name The name token to resolve
     */
    void resolveLocal(Expression expression, Token name);

    /**
     * Returns the current class type that is being resolved. The class type is used to determine
     * whether certain operations are allowed or not.
     *
     * @return The current class type that is being resolved
     */
    ClassType currentClassType();

    /**
     * Sets the current class type that is being resolved. The class type is used to determine
     * whether certain operations are allowed or not.
     *
     * @param type The current class type that is being resolved
     * @return This resolver
     */
    Resolver currentClassType(ClassType type);

    /**
     * Returns whether the resolver has defined any scopes. A scope is defined when a new scope
     * is created using {@link #beginScope()} and removed using {@link #endScope()}.
     *
     * @return {@code true} if the resolver has defined any scopes, {@code false} otherwise
     */
    boolean hasDefinedScopes();

    /**
     * Returns the current scope that is being resolved. The scope is a map of variable
     * lexemes (names) to a boolean that indicates whether the variable has been fully
     * initialized.
     *
     * @return The current scope that is being resolved
     */
    Map<String, Boolean> peekScope();

    /**
     * Begins a new scope. This method will create a new scope map and push it onto the stack of
     * scopes that are being resolved. This also includes the scope of final variables.
     */
    void beginScope();

    /**
     * Ends the current scope. This method will pop the current scope map from the stack of scopes
     * that are being resolved. This also includes the scope of final variables.
     */
    void endScope();

    /**
     * Returns the current scope type that is being resolved. The scope type is used to determine
     * whether certain operations are allowed or not.
     *
     * @return The current scope type that is being resolved
     */
    FlowControlKeyword.ScopeType currentScopeType();

    /**
     * Returns the current function type that is being resolved. The function type is used to determine
     * whether certain operations are allowed or not.
     *
     * @return The current function type that is being resolved
     */
    FunctionType currentFunction();

    /**
     * Sets the current function type that is being resolved. The function type is used to determine
     * whether certain operations are allowed or not.
     *
     * @param type The current function type that is being resolved
     * @return This resolver
     */
    Resolver currentFunction(FunctionType type);

    /**
     * Sets the current scope type that is being resolved. The scope type is used to determine
     * whether certain operations are allowed or not.
     *
     * @param type The current scope type that is being resolved
     * @return This resolver
     */
    Resolver currentScopeType(FlowControlKeyword.ScopeType type);

    /**
     * Declares a new variable in the current scope. This method will declare the variable by adding
     * it to the current scope map. If the variable is already declared in the current scope, an error
     * is thrown.
     *
     * @param name The name token of the variable to declare
     *
     * @throws ScriptEvaluationError If the variable is already declared in the current scope
     */
    void declare(Token name);

    /**
     * Defines a variable, meaning that it has been assigned a value and is available for use. This
     * method does not handle the value itself, as that is done by the interpreter. If the variable
     * is final, an error is thrown if it is redefined.
     *
     * @param name The name token of the variable to define
     *
     * @throws ScriptEvaluationError If the variable is final and has already been defined
     */
    void define(Token name);

    /**
     * Makes the given node final. This method will add the node to the current finals map, indicating
     * that it is a final variable. If the node is already final, an error as it is considered a
     * re-definition of the final variable.
     *
     * @param <R> The type of the node
     *
     * @param node The node to make final
     * @param what The type of the node
     *
     * @throws ScriptEvaluationError If the node is final and has already been defined
     */
    <R extends Finalizable & NamedNode> void makeFinal(R node, String what);

    /**
     * Resolves the given function. This method will open a scope for the function, which has declarations
     * and definitions present for its parameters. All statements within the function body will be
     * resolved in this scope. After the function body has been resolved, the scope is closed.
     *
     * @param executable The function to resolve
     * @param type The type of the function
     */
    void resolveFunction(ParametricExecutableStatement executable, FunctionType type);

    /**
     * Returns the current final variables that are being resolved. The finals are a map of statement
     * lexemes to a string that indicates what kind of final variable it is.
     *
     * @return The current final variables that are being resolved
     */
    Map<String, String> peekFinal();

    /**
     * Returns the interpreter that is used by this resolver. The interpreter is used by the
     * resolver to create- and pre-populate runtime scopes with all built-in functions and classes.
     *
     * @return The interpreter that is used by this resolver
     */
    Interpreter interpreter();
}
