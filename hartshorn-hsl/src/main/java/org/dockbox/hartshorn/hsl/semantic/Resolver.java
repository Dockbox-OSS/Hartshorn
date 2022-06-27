package org.dockbox.hartshorn.hsl.semantic;

import org.dockbox.hartshorn.hsl.ast.statement.ModuleStatement;
import org.dockbox.hartshorn.hsl.callable.ErrorReporter;
import org.dockbox.hartshorn.hsl.ast.expression.ArrayGetExpression;
import org.dockbox.hartshorn.hsl.ast.expression.ArraySetExpression;
import org.dockbox.hartshorn.hsl.ast.expression.ArrayVariable;
import org.dockbox.hartshorn.hsl.ast.expression.AssignExpression;
import org.dockbox.hartshorn.hsl.ast.expression.BinaryExpression;
import org.dockbox.hartshorn.hsl.ast.expression.BitwiseExpression;
import org.dockbox.hartshorn.hsl.ast.statement.BlockStatement;
import org.dockbox.hartshorn.hsl.ast.statement.BreakStatement;
import org.dockbox.hartshorn.hsl.ast.expression.FunctionCallExpression;
import org.dockbox.hartshorn.hsl.ast.statement.ClassStatement;
import org.dockbox.hartshorn.hsl.ast.statement.ContinueStatement;
import org.dockbox.hartshorn.hsl.ast.statement.DoWhileStatement;
import org.dockbox.hartshorn.hsl.ast.expression.ElvisExpression;
import org.dockbox.hartshorn.hsl.ast.expression.Expression;
import org.dockbox.hartshorn.hsl.ast.statement.ExpressionStatement;
import org.dockbox.hartshorn.hsl.ast.statement.ExtensionStatement;
import org.dockbox.hartshorn.hsl.ast.statement.FunctionStatement;
import org.dockbox.hartshorn.hsl.ast.expression.GetExpression;
import org.dockbox.hartshorn.hsl.ast.expression.GroupingExpression;
import org.dockbox.hartshorn.hsl.ast.statement.IfStatement;
import org.dockbox.hartshorn.hsl.ast.expression.InfixExpression;
import org.dockbox.hartshorn.hsl.ast.expression.LiteralExpression;
import org.dockbox.hartshorn.hsl.ast.expression.LogicalExpression;
import org.dockbox.hartshorn.hsl.ast.MoveKeyword;
import org.dockbox.hartshorn.hsl.ast.statement.NativeFunctionStatement;
import org.dockbox.hartshorn.hsl.ast.expression.PrefixExpression;
import org.dockbox.hartshorn.hsl.ast.statement.PrintStatement;
import org.dockbox.hartshorn.hsl.ast.statement.RepeatStatement;
import org.dockbox.hartshorn.hsl.ast.statement.ReturnStatement;
import org.dockbox.hartshorn.hsl.ast.expression.SetExpression;
import org.dockbox.hartshorn.hsl.ast.statement.Statement;
import org.dockbox.hartshorn.hsl.ast.expression.SuperExpression;
import org.dockbox.hartshorn.hsl.ast.expression.TernaryExpression;
import org.dockbox.hartshorn.hsl.ast.statement.TestStatement;
import org.dockbox.hartshorn.hsl.ast.expression.ThisExpression;
import org.dockbox.hartshorn.hsl.ast.expression.UnaryExpression;
import org.dockbox.hartshorn.hsl.ast.statement.VariableStatement;
import org.dockbox.hartshorn.hsl.ast.expression.VariableExpression;
import org.dockbox.hartshorn.hsl.ast.statement.WhileStatement;
import org.dockbox.hartshorn.hsl.callable.module.NativeModule;
import org.dockbox.hartshorn.hsl.callable.virtual.VirtualFunction;
import org.dockbox.hartshorn.hsl.interpreter.Interpreter;
import org.dockbox.hartshorn.hsl.runtime.Phase;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.TokenType;
import org.dockbox.hartshorn.hsl.visitors.ExpressionVisitor;
import org.dockbox.hartshorn.hsl.visitors.StatementVisitor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * Class to do Semantic Analysis and type checking if HSL converted to static type language
 */
public class Resolver implements ExpressionVisitor<Void>, StatementVisitor<Void> {

    private final ErrorReporter errorReporter;
    private final Interpreter interpreter;
    private final Stack<Map<String, Boolean>> scopes = new Stack<>();

    public Resolver(final ErrorReporter errorReporter, final Interpreter interpreter) {
        this.errorReporter = errorReporter;
        this.interpreter = interpreter;
    }

    private enum FunctionType {
        NONE,
        FUNCTION,
        METHOD,
        INITIALIZER,
        TEST
    }

    private enum ClassType {
        NONE,
        CLASS,
        SUBCLASS,
    }

    private ClassType currentClass = ClassType.NONE;
    private FunctionType currentFunction = FunctionType.NONE;
    private MoveKeyword.ScopeType currentScopeType = MoveKeyword.ScopeType.NONE;

    @Override
    public Void visit(final BinaryExpression expr) {
        this.resolve(expr.leftExpression());
        this.resolve(expr.rightExpression());
        return null;
    }

    @Override
    public Void visit(final GroupingExpression expr) {
        this.resolve(expr.expression());
        return null;
    }

    @Override
    public Void visit(final LiteralExpression expr) {
        return null;
    }

    @Override
    public Void visit(final AssignExpression expr) {
        this.resolve(expr.value());
        this.resolveLocal(expr, expr.name());
        return null;
    }

    @Override
    public Void visit(final UnaryExpression expr) {
        this.resolve(expr.rightExpression());
        return null;
    }

    @Override
    public Void visit(final LogicalExpression expr) {
        this.resolve(expr.leftExpression());
        this.resolve(expr.rightExpression());
        return null;
    }

    @Override
    public Void visit(final BitwiseExpression expr) {
        this.resolve(expr.leftExpression());
        this.resolve(expr.rightExpression());
        return null;
    }

    @Override
    public Void visit(final FunctionCallExpression expr) {
        this.resolve(expr.callee());

        for (final Expression argument : expr.arguments()) {
            this.resolve(argument);
        }

        return null;
    }

    @Override
    public Void visit(final GetExpression expr) {
        this.resolve(expr.object());
        return null;
    }

    @Override
    public Void visit(final SetExpression expr) {
        this.resolve(expr.value());
        this.resolve(expr.object());
        return null;
    }

    @Override
    public Void visit(final ThisExpression expr) {
        if (this.currentClass == ClassType.NONE) {
            this.errorReporter.error(Phase.RESOLVING, expr.keyword(), "Cannot use 'this' outside of a class.");
            return null;
        }
        this.resolveLocal(expr, expr.keyword());
        return null;
    }

    @Override
    public Void visit(final VariableExpression expr) {
        if (!this.scopes.isEmpty() &&
                this.scopes.peek().get(expr.name().lexeme()) == Boolean.FALSE) {
            this.errorReporter.error(Phase.RESOLVING, expr.name(), "Cannot read local variable in its own initializer.");
        }
        this.resolveLocal(expr, expr.name());
        return null;
    }

    @Override
    public Void visit(final ExpressionStatement statement) {
        this.resolve(statement.expression());
        return null;
    }

    @Override
    public Void visit(final PrintStatement statement) {
        this.resolve(statement.expression());
        return null;
    }

    @Override
    public Void visit(final BlockStatement statement) {
        this.beginScope();
        this.resolve(statement.statementList());
        this.endScope();
        return null;
    }

    @Override
    public Void visit(final IfStatement statement) {
        this.resolve(statement.condition());
        this.resolve(statement.thenBranch());
        if (statement.elseBranch() != null) this.resolve(statement.elseBranch());
        return null;
    }

    @Override
    public Void visit(final WhileStatement statement) {
        final MoveKeyword.ScopeType enclosingType = this.currentScopeType;
        this.currentScopeType = MoveKeyword.ScopeType.LOOP;
        this.resolve(statement.condition());
        this.resolve(statement.loopBody());
        this.currentScopeType = enclosingType;
        return null;
    }

    @Override
    public Void visit(final DoWhileStatement statement) {
        final MoveKeyword.ScopeType enclosingType = this.currentScopeType;
        this.currentScopeType = MoveKeyword.ScopeType.LOOP;
        this.resolve(statement.condition());
        this.resolve(statement.loopBody());
        this.currentScopeType = enclosingType;
        return null;
    }

    @Override
    public Void visit(final RepeatStatement statement) {
        final MoveKeyword.ScopeType enclosingType = this.currentScopeType;
        this.currentScopeType = MoveKeyword.ScopeType.LOOP;
        this.resolve(statement.value());
        this.resolve(statement.loopBody());
        this.currentScopeType = enclosingType;
        return null;
    }

    @Override
    public Void visit(final BreakStatement statement) {
        // add this case inside semantic to make sure it inside loop
        if (this.currentScopeType == MoveKeyword.ScopeType.NONE) {
            this.errorReporter.error(Phase.RESOLVING, statement.keyword(), "Continue can only used be inside loops.");
        }
        return null;
    }

    @Override
    public Void visit(final ContinueStatement statement) {
        // add this case inside semantic to make sure it inside loop
        if (this.currentScopeType == MoveKeyword.ScopeType.NONE) {
            this.errorReporter.error(Phase.RESOLVING, statement.keyword(), "Break can only used be inside loops.");
        }
        return null;
    }

    @Override
    public Void visit(final FunctionStatement statement) {
        this.define(statement.name());

        this.resolveFunction(statement, FunctionType.FUNCTION);
        return null;
    }

    @Override
    public Void visit(final ExtensionStatement statement) {
        final ClassType enclosingClass = this.currentClass;
        this.currentClass = ClassType.CLASS;
        this.declare(statement.className());
        this.resolveFunction(statement.functionStatement(), FunctionType.FUNCTION);
        this.currentClass = enclosingClass;
        return null;
    }

    @Override
    public Void visit(final VariableStatement statement) {
        // Resolving a variable declaration adds a new entry to the current innermost scope’s map
        this.declare(statement.name());
        if (statement.initializer() != null) {
            this.resolve(statement.initializer());
        }
        this.define(statement.name());
        return null;
    }

    @Override
    public Void visit(final ReturnStatement statement) {
        // Make sure return is inside function
        if (this.currentFunction == FunctionType.NONE) {
            this.errorReporter.error(Phase.RESOLVING, statement.keyword(), "Cannot return from top-level code.");
        }
        if (statement.value() != null) {
            if (this.currentFunction == FunctionType.INITIALIZER) {
                this.errorReporter.error(Phase.RESOLVING, statement.keyword(), "Cannot return a value from an initializer.");
            }
            this.resolve(statement.value());
        }
        return null;
    }

    @Override
    public Void visit(final ClassStatement statement) {
        final ClassType enclosingClass = this.currentClass;
        this.currentClass = ClassType.CLASS;

        this.declare(statement.name());

        // Class must not extend itself
        if (statement.superClass() != null &&
                statement.name().lexeme().equals(statement.superClass().name().lexeme())) {
            this.errorReporter.error(Phase.RESOLVING, statement.superClass().name(), "A class cannot inherit from itself.");
        }

        // For inheritance
        if (statement.superClass() != null) {
            this.currentClass = ClassType.SUBCLASS;
            this.resolve(statement.superClass());
        }

        // Support super keyword
        if (statement.superClass() != null) {
            this.beginScope();
            this.scopes.peek().put(TokenType.SUPER.representation(), true);
        }

        this.beginScope();
        this.scopes.peek().put(TokenType.THIS.representation(), true);
        for (final FunctionStatement method : statement.methods()) {
            FunctionType declaration = FunctionType.METHOD;
            if (VirtualFunction.CLASS_INIT.equals(method.name().lexeme())) {
                declaration = FunctionType.INITIALIZER;
            }
            this.resolveFunction(method, declaration);
        }
        this.define(statement.name());
        this.endScope();
        if (statement.superClass() != null) this.endScope();
        this.currentClass = enclosingClass;
        return null;
    }

    @Override
    public Void visit(final NativeFunctionStatement statement) {
        this.declare(statement.name());
        this.define(statement.name());

        return null;
    }

    @Override
    public Void visit(final TestStatement statement) {
        final FunctionType enclosingClass = this.currentFunction;
        this.currentFunction = FunctionType.TEST;
        this.declare(statement.name());
        this.resolve(statement.body());
        this.resolve(statement.returnValue());
        this.currentFunction = enclosingClass;
        return null;
    }

    @Override
    public Void visit(final ModuleStatement statement) {
        final Map<String, NativeModule> modules = this.interpreter.externalModules();
        final String module = statement.name().lexeme();
        if (!modules.containsKey(module)) {
            this.errorReporter.error(Phase.RESOLVING, statement.name(), "Cannot find module named '" + module + "'");
        }
        return null;
    }

    @Override
    public Void visit(final ElvisExpression statement) {
        this.resolve(statement.condition());
        this.resolve(statement.rightExpression());
        return null;
    }

    @Override
    public Void visit(final TernaryExpression statement) {
        this.resolve(statement.condition());
        this.resolve(statement.firstExpression());
        this.resolve(statement.secondExpression());
        return null;
    }

    @Override
    public Void visit(final ArraySetExpression expr) {
        this.define(expr.name());
        this.resolve(expr.index());
        this.resolve(expr.value());
        return null;
    }

    @Override
    public Void visit(final ArrayGetExpression expr) {
        this.resolve(expr.size());
        return null;
    }

    @Override
    public Void visit(final ArrayVariable expr) {
        this.define(expr.name());
        this.resolve(expr.index());
        return null;
    }

    @Override
    public Void visit(final PrefixExpression expr) {
        this.resolve(expr.rightExpression());
        return null;
    }

    @Override
    public Void visit(final InfixExpression expr) {
        this.resolve(expr.leftExpression());
        this.resolve(expr.rightExpression());
        return null;
    }

    @Override
    public Void visit(final SuperExpression expr) {
        if (this.currentClass == ClassType.NONE) {
            this.errorReporter.error(Phase.RESOLVING, expr.keyword(), "Cannot use 'super' outside of a class.");
        }
        else if (this.currentClass != ClassType.SUBCLASS) {
            this.errorReporter.error(Phase.RESOLVING, expr.keyword(), "Cannot use 'super' in a class with no superclass.");
        }
        this.resolveLocal(expr, expr.keyword());
        return null;
    }

    private void beginScope() {
        this.scopes.push(new HashMap<>());
    }

    private void endScope() {
        this.scopes.pop();
    }

    public void resolve(final List<Statement> stmtList) {
        for (final Statement statement : stmtList) {
            this.resolve(statement);
        }
    }

    private void resolve(final Statement stmt) {
        stmt.accept(this);
    }

    private void resolve(final Expression expr) {
        expr.accept(this);
    }

    private void resolveLocal(final Expression expr, final Token name) {
        for (int i = this.scopes.size() - 1; i >= 0; i--) {
            if (this.scopes.get(i).containsKey(name.lexeme())) {
                this.interpreter.resolve(expr, this.scopes.size() - 1 - i);
                return;
            }
        }
        // Not found. Assume it is global.
    }

    private void resolveFunction(final FunctionStatement func, final FunctionType type) {
        final FunctionType enclosingFunction = this.currentFunction;
        this.currentFunction = type;

        this.beginScope();
        for (final Token param : func.parameters()) {
            this.declare(param);
            this.define(param);
        }
        this.resolve(func.functionBody());
        this.endScope();

        this.currentFunction = enclosingFunction;
    }

    private void declare(final Token name) {
        if (this.scopes.isEmpty()) return;

        final Map<String, Boolean> scope = this.scopes.peek();

        // Never declare variable twice in same scope
        if (scope.containsKey(name.lexeme())) {
            this.errorReporter.error(Phase.RESOLVING, name, "Variable with name '%s' already declared in this scope.".formatted(name.lexeme()));
        }
        scope.put(name.lexeme(), false);
    }

    private void define(final Token name) {
        // set the variable’s value in the scope map to true to mark it as fully initialized and available for use
        if (this.scopes.isEmpty()) return;
        this.scopes.peek().put(name.lexeme(), true);
    }
}
