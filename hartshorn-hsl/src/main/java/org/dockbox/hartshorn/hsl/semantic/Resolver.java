package org.dockbox.hartshorn.hsl.semantic;

import org.dockbox.hartshorn.hsl.ast.ModuleStatement;
import org.dockbox.hartshorn.hsl.callable.ErrorReporter;
import org.dockbox.hartshorn.hsl.ast.ArrayGetExp;
import org.dockbox.hartshorn.hsl.ast.ArraySetExp;
import org.dockbox.hartshorn.hsl.ast.ArrayVariable;
import org.dockbox.hartshorn.hsl.ast.AssignExp;
import org.dockbox.hartshorn.hsl.ast.BinaryExp;
import org.dockbox.hartshorn.hsl.ast.BitwiseExp;
import org.dockbox.hartshorn.hsl.ast.BlockStatement;
import org.dockbox.hartshorn.hsl.ast.BreakStatement;
import org.dockbox.hartshorn.hsl.ast.CallExp;
import org.dockbox.hartshorn.hsl.ast.ClassStatement;
import org.dockbox.hartshorn.hsl.ast.ContinueStatement;
import org.dockbox.hartshorn.hsl.ast.DoWhileStatement;
import org.dockbox.hartshorn.hsl.ast.ElvisExp;
import org.dockbox.hartshorn.hsl.ast.Expression;
import org.dockbox.hartshorn.hsl.ast.ExpressionStatement;
import org.dockbox.hartshorn.hsl.ast.ExtensionStatement;
import org.dockbox.hartshorn.hsl.ast.FunctionStatement;
import org.dockbox.hartshorn.hsl.ast.GetExp;
import org.dockbox.hartshorn.hsl.ast.GroupingExp;
import org.dockbox.hartshorn.hsl.ast.IfStatement;
import org.dockbox.hartshorn.hsl.ast.InfixExpression;
import org.dockbox.hartshorn.hsl.ast.LiteralExp;
import org.dockbox.hartshorn.hsl.ast.LogicalExp;
import org.dockbox.hartshorn.hsl.ast.MoveKeyword;
import org.dockbox.hartshorn.hsl.ast.NativeFunctionStatement;
import org.dockbox.hartshorn.hsl.ast.PrefixExpression;
import org.dockbox.hartshorn.hsl.ast.PrintStatement;
import org.dockbox.hartshorn.hsl.ast.RepeatStatement;
import org.dockbox.hartshorn.hsl.ast.ReturnStatement;
import org.dockbox.hartshorn.hsl.ast.SetExp;
import org.dockbox.hartshorn.hsl.ast.Statement;
import org.dockbox.hartshorn.hsl.ast.SuperExp;
import org.dockbox.hartshorn.hsl.ast.TernaryExp;
import org.dockbox.hartshorn.hsl.ast.TestStatement;
import org.dockbox.hartshorn.hsl.ast.ThisExp;
import org.dockbox.hartshorn.hsl.ast.UnaryExp;
import org.dockbox.hartshorn.hsl.ast.Var;
import org.dockbox.hartshorn.hsl.ast.Variable;
import org.dockbox.hartshorn.hsl.ast.WhileStatement;
import org.dockbox.hartshorn.hsl.interpreter.Interpreter;
import org.dockbox.hartshorn.hsl.token.Token;
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
    public Void visit(final BinaryExp expr) {
        this.resolve(expr.getLeftExp());
        this.resolve(expr.getRightExp());
        return null;
    }

    @Override
    public Void visit(final GroupingExp expr) {
        this.resolve(expr.getExpression());
        return null;
    }

    @Override
    public Void visit(final LiteralExp expr) {
        return null;
    }

    @Override
    public Void visit(final AssignExp expr) {
        this.resolve(expr.getValue());
        this.resolveLocal(expr, expr.getName());
        return null;
    }

    @Override
    public Void visit(final UnaryExp expr) {
        this.resolve(expr.getRightExp());
        return null;
    }

    @Override
    public Void visit(final LogicalExp expr) {
        this.resolve(expr.getLeftExp());
        this.resolve(expr.getRightExp());
        return null;
    }

    @Override
    public Void visit(final BitwiseExp expr) {
        this.resolve(expr.getLeftExp());
        this.resolve(expr.getRightExp());
        return null;
    }

    @Override
    public Void visit(final CallExp expr) {
        this.resolve(expr.getCallee());

        for (final Expression argument : expr.getArguments()) {
            this.resolve(argument);
        }

        return null;
    }

    @Override
    public Void visit(final GetExp expr) {
        this.resolve(expr.getObject());
        return null;
    }

    @Override
    public Void visit(final SetExp expr) {
        this.resolve(expr.getValue());
        this.resolve(expr.getObject());
        return null;
    }

    @Override
    public Void visit(final ThisExp expr) {
        if (this.currentClass == ClassType.NONE) {
            this.errorReporter.error(expr.getKeyword(), "Cannot use 'this' outside of a class.");
            return null;
        }
        this.resolveLocal(expr, expr.getKeyword());
        return null;
    }

    @Override
    public Void visit(final Variable expr) {
        if (!this.scopes.isEmpty() &&
                this.scopes.peek().get(expr.getName().lexeme()) == Boolean.FALSE) {
            this.errorReporter.error(expr.getName(), "Cannot read local variable in its own initializer.");
        }
        this.resolveLocal(expr, expr.getName());
        return null;
    }

    @Override
    public Void visit(final ExpressionStatement statement) {
        this.resolve(statement.getExpression());
        return null;
    }

    @Override
    public Void visit(final PrintStatement statement) {
        this.resolve(statement.getExpression());
        return null;
    }

    @Override
    public Void visit(final BlockStatement statement) {
        this.beginScope();
        this.resolve(statement.getStatementList());
        this.endScope();
        return null;
    }

    @Override
    public Void visit(final IfStatement statement) {
        this.resolve(statement.getCondition());
        this.resolve(statement.getThenBranch());
        //TODO : in future i will add resolve for every if else statement
        if (statement.getElseBranch() != null) this.resolve(statement.getElseBranch());
        return null;
    }

    @Override
    public Void visit(final WhileStatement statement) {
        final MoveKeyword.ScopeType enclosingType = this.currentScopeType;
        this.currentScopeType = MoveKeyword.ScopeType.LOOP;
        this.resolve(statement.getCondition());
        this.resolve(statement.getLoopBody());
        this.currentScopeType = enclosingType;
        return null;
    }

    @Override
    public Void visit(final DoWhileStatement statement) {
        final MoveKeyword.ScopeType enclosingType = this.currentScopeType;
        this.currentScopeType = MoveKeyword.ScopeType.LOOP;
        this.resolve(statement.getCondition());
        this.resolve(statement.getLoopBody());
        this.currentScopeType = enclosingType;
        return null;
    }

    @Override
    public Void visit(final RepeatStatement statement) {
        final MoveKeyword.ScopeType enclosingType = this.currentScopeType;
        this.currentScopeType = MoveKeyword.ScopeType.LOOP;
        this.resolve(statement.getValue());
        this.resolve(statement.getLoopBody());
        this.currentScopeType = enclosingType;
        return null;
    }

    @Override
    public Void visit(final BreakStatement statement) {
        // add this case inside semantic to make sure it inside loop
        if (this.currentScopeType == MoveKeyword.ScopeType.NONE) {
            this.errorReporter.error(statement.getKeyword(), "Continue can only used be inside loops.");
        }
        return null;
    }

    @Override
    public Void visit(final ContinueStatement statement) {
        // add this case inside semantic to make sure it inside loop
        if (this.currentScopeType == MoveKeyword.ScopeType.NONE) {
            this.errorReporter.error(statement.getKeyword(), "Break can only used be inside loops.");
        }
        return null;
    }

    @Override
    public Void visit(final FunctionStatement statement) {
        this.declare(statement.getName());
        this.define(statement.getName());

        this.resolveFunction(statement, FunctionType.FUNCTION);
        return null;
    }

    @Override
    public Void visit(final ExtensionStatement statement) {
        final ClassType enclosingClass = this.currentClass;
        this.currentClass = ClassType.CLASS;
        this.declare(statement.getClassName());
        this.resolveFunction(statement.getFunctionStatement(), FunctionType.FUNCTION);
        this.currentClass = enclosingClass;
        return null;
    }

    @Override
    public Void visit(final Var statement) {
        //Resolving a variable declaration adds a new entry to the current innermost scope’s map
        this.declare(statement.getName());
        if (statement.getInitializer() != null) {
            this.resolve(statement.getInitializer());
        }
        this.define(statement.getName());
        return null;
    }

    @Override
    public Void visit(final ReturnStatement statement) {
        //Make sure return is inside function
        if (this.currentFunction == FunctionType.NONE) {
            this.errorReporter.error(statement.getKeyword(), "Cannot return from top-level code.");
        }
        if (statement.getValue() != null) {
            if (this.currentFunction == FunctionType.INITIALIZER) {
                this.errorReporter.error(statement.getKeyword(), "Cannot return a value from an initializer.");
            }
            this.resolve(statement.getValue());
        }
        return null;
    }

    @Override
    public Void visit(final ClassStatement statement) {
        final ClassType enclosingClass = this.currentClass;
        this.currentClass = ClassType.CLASS;

        this.declare(statement.getName());

        //Class must not extends same class
        if (statement.getSuperClass() != null &&
                statement.getName().lexeme().equals(statement.getSuperClass().getName().lexeme())) {
            this.errorReporter.error(statement.getSuperClass().getName(), "A class cannot inherit from itself.");
        }

        //for Inheritance
        if (statement.getSuperClass() != null) {
            this.currentClass = ClassType.SUBCLASS;
            this.resolve(statement.getSuperClass());
        }

        //Support super keyword
        if (statement.getSuperClass() != null) {
            this.beginScope();
            this.scopes.peek().put("super", true);
        }

        this.beginScope();
        this.scopes.peek().put("this", true);
        for (final FunctionStatement method : statement.getMethods()) {
            FunctionType declaration = FunctionType.METHOD;
            if ("init".equals(method.getName().lexeme())) {
                declaration = FunctionType.INITIALIZER;
            }
            this.resolveFunction(method, declaration);
        }
        this.define(statement.getName());
        this.endScope();
        if (statement.getSuperClass() != null) this.endScope();
        this.currentClass = enclosingClass;
        return null;
    }

    @Override
    public Void visit(final NativeFunctionStatement statement) {
        this.declare(statement.getName());
        this.define(statement.getName());

        return null;
    }

    @Override
    public Void visit(final TestStatement statement) {
        final FunctionType enclosingClass = this.currentFunction;
        this.currentFunction = FunctionType.TEST;
        this.declare(statement.getName());
        this.resolve(statement.getBody());
        this.resolve(statement.getReturnValue());
        this.currentFunction = enclosingClass;
        return null;
    }

    @Override
    public Void visit(final ModuleStatement statement) {
        // TODO: Implement me!
        return null;
    }

    @Override
    public Void visit(final ElvisExp statement) {
        this.resolve(statement.getCondition());
        this.resolve(statement.getRightExp());
        return null;
    }

    @Override
    public Void visit(final TernaryExp statement) {
        this.resolve(statement.getCondition());
        this.resolve(statement.getFirstExp());
        this.resolve(statement.getSecondExp());
        return null;
    }

    @Override
    public Void visit(final ArraySetExp expr) {
        this.declare(expr.getName());
        this.resolve(expr.getIndex());
        this.resolve(expr.getValue());
        return null;
    }

    @Override
    public Void visit(final ArrayGetExp expr) {
        this.resolve(expr.getSize());
        return null;
    }

    @Override
    public Void visit(final ArrayVariable expr) {
        this.declare(expr.getName());
        this.resolve(expr.getIndex());
        return null;
    }

    @Override
    public Void visit(final PrefixExpression expr) {
        this.resolve(expr.getRightExpression());
        return null;
    }

    @Override
    public Void visit(final InfixExpression expr) {
        this.resolve(expr.getLeftExp());
        this.resolve(expr.getRightExp());
        return null;
    }

    @Override
    public Void visit(final SuperExp expr) {
        if (this.currentClass == ClassType.NONE) {
            this.errorReporter.error(expr.getKeyword(), "Cannot use 'super' outside of a class.");
        }
        else if (this.currentClass != ClassType.SUBCLASS) {
            this.errorReporter.error(expr.getKeyword(), "Cannot use 'super' in a class with no superclass.");
        }
        this.resolveLocal(expr, expr.getKeyword());
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
        for (final Token param : func.getParams()) {
            this.declare(param);
            this.define(param);
        }
        this.resolve(func.getFunctionBody());
        this.endScope();

        this.currentFunction = enclosingFunction;
    }

    private void declare(final Token name) {
        if (this.scopes.isEmpty()) return;

        final Map<String, Boolean> scope = this.scopes.peek();

        //Never declare variable twice in same scope
        if (scope.containsKey(name.lexeme())) {
            this.errorReporter.error(name, "Variable with this name already declared in this scope.");
        }
        scope.put(name.lexeme(), false);
    }

    private void define(final Token name) {
        //set the variable’s value in the scope map to true to mark it as fully initialized and available for use
        if (this.scopes.isEmpty()) return;
        this.scopes.peek().put(name.lexeme(), true);
    }
}
