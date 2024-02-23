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

package org.dockbox.hartshorn.hsl.semantic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.dockbox.hartshorn.hsl.ScriptEvaluationError;
import org.dockbox.hartshorn.hsl.ast.FlowControlKeyword;
import org.dockbox.hartshorn.hsl.ast.FlowControlKeyword.ScopeType;
import org.dockbox.hartshorn.hsl.ast.NamedNode;
import org.dockbox.hartshorn.hsl.ast.expression.Expression;
import org.dockbox.hartshorn.hsl.ast.statement.ClassStatement;
import org.dockbox.hartshorn.hsl.ast.statement.ConstructorStatement;
import org.dockbox.hartshorn.hsl.ast.statement.FunctionStatement;
import org.dockbox.hartshorn.hsl.ast.statement.ParametricExecutableStatement;
import org.dockbox.hartshorn.hsl.ast.statement.ParametricExecutableStatement.Parameter;
import org.dockbox.hartshorn.hsl.ast.statement.Statement;
import org.dockbox.hartshorn.hsl.ast.statement.TestStatement;
import org.dockbox.hartshorn.hsl.extension.CustomExpression;
import org.dockbox.hartshorn.hsl.extension.CustomStatement;
import org.dockbox.hartshorn.hsl.interpreter.Interpreter;
import org.dockbox.hartshorn.hsl.objects.Finalizable;
import org.dockbox.hartshorn.hsl.runtime.Phase;
import org.dockbox.hartshorn.hsl.token.Token;

/**
 * Standard resolver to perform semantic analysis and type checking before a collection of statements
 * is interpreted. This allows illegal references to identifiers to be found and reported early on,
 * preventing potential runtime errors.
 *
 * <p>This resolver delegates most of its work to a {@link ResolverVisitor}, which is a visitor
 * that resolves the appropriate nodes in the AST. The resolver itself is responsible for managing
 * the tracking of resolved items.
 *
 * @since 0.4.12
 *
 * @see ResolverVisitor
 *
 * @author Guus Lieben
 */
public class Resolver {

    /**
     * The type of the function that is currently being resolved. This is used to determine
     * whether certain operations are allowed or not.
     *
     * @since 0.4.12
     *
     * @author Guus Lieben
     */
    public enum FunctionType {
        /**
         * No function type, indicating that we are resolving outside the scope of
         * any function.
         */
        NONE,
        /**
         * An inline function, which is a function that is not attached to a class (method).
         * Usually linked only to {@link FunctionStatement function statements}.
         */
        FUNCTION,
        /**
         * A method, which is a function that is attached to a class. Usually linked to {@link
         * FunctionStatement functions} encountered while visiting a {@link ClassStatement}.
         */
        METHOD,
        /**
         * A constructor, which is a method that is used to create a new instance of a class.
         * Usually linked to {@link ConstructorStatement constructor} encountered while visiting
         * a {@link ClassStatement}.
         */
        INITIALIZER,
        /**
         * A test function, which is a inline function-like assertion. Usually linked to {@link
         * TestStatement test statements}.
         */
        TEST,
    }

    /**
     * The type of the class that is currently being resolved. This is used to determine
     * whether certain operations are allowed or not.
     *
     * @since 0.4.12
     *
     * @author Guus Lieben
     */
    public enum ClassType {
        /**
         * No class type, indicating that we are resolving outside the scope of any class.
         */
        NONE,
        /**
         * A single class, which is a class that does not extend another class.
         */
        CLASS,
        /**
         * A subclass, which is a class that extends another class, indicated by the presence
         * of a {@link ClassStatement#superClass() superclass expression}.
         */
        SUBCLASS,
    }

    private final Interpreter interpreter;
    private final Stack<Map<String, Boolean>> scopes = new Stack<>();
    private final Stack<Map<String, String>> finals = new Stack<>();
    private final ResolverVisitor visitor = new ResolverVisitor(this);

    public Resolver(Interpreter interpreter) {
        this.interpreter = interpreter;
    }

    private ClassType currentClass = ClassType.NONE;
    private FunctionType currentFunction = FunctionType.NONE;
    private FlowControlKeyword.ScopeType currentScopeType = FlowControlKeyword.ScopeType.NONE;

    /**
     * Returns the interpreter that is used by this resolver. The interpreter is used by the
     * resolver to create- and pre-populate runtime scopes with all built-in functions and classes.
     *
     * @return The interpreter that is used by this resolver
     */
    public Interpreter interpreter() {
        return this.interpreter;
    }

    /**
     * Returns whether the resolver has defined any scopes. A scope is defined when a new scope
     * is created using {@link #beginScope()} and removed using {@link #endScope()}.
     *
     * @return {@code true} if the resolver has defined any scopes, {@code false} otherwise
     */
    public boolean hasDefinedScopes() {
        return !this.scopes.isEmpty();
    }

    /**
     * Returns the current scope that is being resolved. The scope is a map of variable
     * lexemes (names) to a boolean that indicates whether the variable has been fully
     * initialized.
     *
     * @return The current scope that is being resolved
     */
    public Map<String, Boolean> peekScope() {
        return this.scopes.peek();
    }

    /**
     * Returns the current final variables that are being resolved. The finals are a map of statement
     * lexemes to a string that indicates what kind of final variable it is.
     *
     * @return The current final variables that are being resolved
     */
    public Map<String, String> peekFinal() {
        return this.finals.peek();
    }

    /**
     * Returns the current class type that is being resolved. The class type is used to determine
     * whether certain operations are allowed or not.
     *
     * @return The current class type that is being resolved
     */
    public ClassType currentClass() {
        return this.currentClass;
    }

    /**
     * Returns the current function type that is being resolved. The function type is used to determine
     * whether certain operations are allowed or not.
     *
     * @return The current function type that is being resolved
     */
    public FunctionType currentFunction() {
        return this.currentFunction;
    }

    /**
     * Returns the current scope type that is being resolved. The scope type is used to determine
     * whether certain operations are allowed or not.
     *
     * @return The current scope type that is being resolved
     */
    public ScopeType currentScopeType() {
        return this.currentScopeType;
    }

    /**
     * Sets the current class type that is being resolved. The class type is used to determine
     * whether certain operations are allowed or not.
     *
     * @param currentClass The current class type that is being resolved
     * @return This resolver
     */
    public Resolver currentClass(ClassType currentClass) {
        this.currentClass = currentClass;
        return this;
    }

    /**
     * Sets the current function type that is being resolved. The function type is used to determine
     * whether certain operations are allowed or not.
     *
     * @param currentFunction The current function type that is being resolved
     * @return This resolver
     */
    public Resolver currentFunction(FunctionType currentFunction) {
        this.currentFunction = currentFunction;
        return this;
    }

    /**
     * Sets the current scope type that is being resolved. The scope type is used to determine
     * whether certain operations are allowed or not.
     *
     * @param currentScopeType The current scope type that is being resolved
     * @return This resolver
     */
    public Resolver currentScopeType(ScopeType currentScopeType) {
        this.currentScopeType = currentScopeType;
        return this;
    }

    /**
     * Begins a new scope. This method will create a new scope map and push it onto the stack of
     * scopes that are being resolved. This also includes the scope of final variables.
     */
    public void beginScope() {
        this.scopes.push(new HashMap<>());
        this.finals.push(new HashMap<>());
    }

    /**
     * Ends the current scope. This method will pop the current scope map from the stack of scopes
     * that are being resolved. This also includes the scope of final variables.
     */
    public void endScope() {
        this.scopes.pop();
        this.finals.pop();
    }

    /**
     * Resolves the given list of statements. This method will resolve each statement in the list
     * by calling {@link #resolve(Statement)}.
     *
     * @param statements The list of statements to resolve
     */
    public void resolve(List<Statement> statements) {
        for (Statement statement : statements) {
            this.resolve(statement);
        }
    }

    /**
     * Resolves the given statement. This method will resolve the statement by passing it to the
     * active {@link ResolverVisitor} if it is compatible, or by directly passing the current
     * resolver to the statement if it is a {@link CustomStatement}.
     *
     * @param statement The statement to resolve
     */
    public void resolve(Statement statement) {
        if (statement instanceof CustomStatement<?> customStatement) {
            customStatement.resolve(this);
        }
        else {
            statement.accept(this.visitor);
        }
    }

    /**
     * Resolves the given expression. This method will resolve the expression by passing it to the
     * active {@link ResolverVisitor} if it is compatible, or by directly passing the current
     * resolver to the expression if it is a {@link CustomExpression}.
     *
     * @param expression The expression to resolve
     */
    public void resolve(Expression expression) {
        if (expression instanceof CustomExpression<?> customExpression) {
            customExpression.resolve(this);
        }
        else {
            expression.accept(this.visitor);
        }
    }

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
    public void resolveLocal(Expression expression, Token name) {
        for (int i = this.scopes.size() - 1; i >= 0; i--) {
            if (this.scopes.get(i).containsKey(name.lexeme())) {
                this.interpreter.resolve(expression, this.scopes.size() - 1 - i);
                return;
            }
        }
        // Not found. Assume it is global.
    }

    /**
     * Resolves the given function. This method will open a scope for the function, which has declarations
     * and definitions present for its parameters. All statements within the function body will be
     * resolved in this scope. After the function body has been resolved, the scope is closed.
     *
     * @param executable The function to resolve
     * @param type The type of the function
     */
    public void resolveFunction(ParametricExecutableStatement executable, FunctionType type) {
        FunctionType enclosingFunction = this.currentFunction;
        this.currentFunction = type;

        this.beginScope();
        for (Parameter param : executable.parameters()) {
            this.declare(param.name());
            this.define(param.name());
        }
        this.resolve(executable.statements());
        this.endScope();

        this.currentFunction = enclosingFunction;
    }

    /**
     * Declares a new variable in the current scope. This method will declare the variable by adding
     * it to the current scope map. If the variable is already declared in the current scope, an error
     * is thrown.
     *
     * @param name The name token of the variable to declare
     *
     * @throws ScriptEvaluationError If the variable is already declared in the current scope
     */
    public void declare(Token name) {
        if (this.scopes.isEmpty()) {
            return;
        }

        Map<String, Boolean> scope = this.scopes.peek();

        // Never declare variable twice in same scope
        if (scope.containsKey(name.lexeme())) {
            throw new ScriptEvaluationError("Variable with name '%s' already declared in this scope.".formatted(name.lexeme()), Phase.RESOLVING, name);
        }
        scope.put(name.lexeme(), false);
    }

    /**
     * Defines a variable, meaning that it has been assigned a value and is available for use. This
     * method does not handle the value itself, as that is done by the interpreter. If the variable
     * is final, an error is thrown if it is redefined.
     *
     * @param name The name token of the variable to define
     *
     * @throws ScriptEvaluationError If the variable is final and has already been defined
     */
    public void define(Token name) {
        if (this.scopes.isEmpty()) {
            return;
        }
        this.checkFinal(name);
        this.scopes.peek().put(name.lexeme(), true);
    }

    /**
     * Checks whether the given name token is a final variable. If it is, and it has already been
     * defined, an error is thrown.
     *
     * @param name The name token of the variable to check
     *
     * @throws ScriptEvaluationError If the variable is final and has already been defined
     */
    public void checkFinal(Token name) {
        if (this.finals.peek().containsKey(name.lexeme())) {
            String existingWhat = this.finals.peek().get(name.lexeme());
            throw new ScriptEvaluationError("Cannot reassign final %s '%s'.".formatted(existingWhat, name.lexeme()), Phase.RESOLVING, name);
        }
    }

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
    public <R extends Finalizable & NamedNode> void makeFinal(R node, String what) {
        // Unlike scopes, finals need to be tracked even in the global scope.
        if (this.finals.isEmpty()) {
            this.finals.push(new HashMap<>());
        }
        this.checkFinal(node.name());
        if (node.isFinal()) {
            this.finals.peek().put(node.name().lexeme(), what);
        }
    }
}
