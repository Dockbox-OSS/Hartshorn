package org.dockbox.hartshorn.hsl.interpreter;

import org.dockbox.hartshorn.hsl.ast.ModuleStatement;
import org.dockbox.hartshorn.hsl.callable.ErrorReporter;
import org.dockbox.hartshorn.hsl.ast.Array;
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
import org.dockbox.hartshorn.hsl.callable.HslCallable;
import org.dockbox.hartshorn.hsl.callable.VirtualClass;
import org.dockbox.hartshorn.hsl.callable.VirtualFunction;
import org.dockbox.hartshorn.hsl.callable.VirtualInstance;
import org.dockbox.hartshorn.hsl.callable.HslLibrary;
import org.dockbox.hartshorn.hsl.runtime.Return;
import org.dockbox.hartshorn.hsl.runtime.RuntimeError;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.TokenType;
import org.dockbox.hartshorn.hsl.visitors.ExpressionVisitor;
import org.dockbox.hartshorn.hsl.visitors.StatementVisitor;
import org.dockbox.hartshorn.util.Result;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Interpreter implements
        ExpressionVisitor<Object>,
        StatementVisitor<Void> {

    private final Environment globals = new Environment();
    private final ErrorReporter errorReporter;
    private final Map<String, Class<?>> externalModules;
    private Environment environment = this.globals;
    private final Map<Expression, Integer> locals = new HashMap<>();

    public Interpreter(final ErrorReporter errorReporter, final Map<String, Class<?>> externalModules) {
        this.errorReporter = errorReporter;
        this.externalModules = externalModules;
    }

    public void interpret(final List<Statement> statements) {
        try {
            for (final Statement statement : statements) {
                this.execute(statement);
            }
        }
        catch (final RuntimeError error) {
            this.errorReporter.runtimeError(error);
        }
    }

    @Override
    public Object visit(final BinaryExp expr) {
        final Object left = this.evaluate(expr.getLeftExp());
        final Object right = this.evaluate(expr.getRightExp());
        switch (expr.getOperator().type()) {
            case PLUS -> {
                //Math Plus
                if (left instanceof Double && right instanceof Double) {
                    return (double) left + (double) right;
                }
                //String Addition
                if (left instanceof String || right instanceof String) {
                    return left.toString() + right.toString();
                }
                //Character + Character
                if ((left instanceof Character && right instanceof Character)) {
                    return String.valueOf(left) + right;
                }
                //Character + Double
                if ((left instanceof Character) && (right instanceof Double)) {
                    final int value = (Character) left;
                    return (double) right + value;
                }
                //Double + Character
                if ((left instanceof Double) && (right instanceof Character)) {
                    final int value = (Character) right;
                    return (double) left + value;
                }
                throw new RuntimeError(expr.getOperator(), "UnSupported childes for Operator.\n");
            }
            case MINUS -> {
                this.checkNumberOperands(expr.getOperator(), left, right);
                return (double) left - (double) right;
            }
            case STAR -> {
                if ((left instanceof String || left instanceof Character) && right instanceof Double) {
                    final int times = (int) ((double) right);
                    final int finalLen = left.toString().length() * times;
                    final StringBuilder result = new StringBuilder(finalLen);
                    final String strValue = left.toString();
                    result.append(strValue.repeat(Math.max(0, times)));
                    return result.toString();
                }
                this.checkNumberOperands(expr.getOperator(), left, right);
                return (double) left * (double) right;
            }
            case SLASH -> {
                this.checkNumberOperands(expr.getOperator(), left, right);
                if ((double) right == 0) {
                    throw new RuntimeError(expr.getOperator(), "Can't use slash with zero double.");
                }
                return (double) left / (double) right;
            }
            case GREATER -> {
                this.checkNumberOperands(expr.getOperator(), left, right);
                return Double.parseDouble(left.toString()) > Double.parseDouble(right.toString());
            }
            case GREATER_EQUAL -> {
                this.checkNumberOperands(expr.getOperator(), left, right);
                return Double.parseDouble(left.toString()) >= Double.parseDouble(right.toString());
            }
            case LESS -> {
                this.checkNumberOperands(expr.getOperator(), left, right);
                return Double.parseDouble(left.toString()) < Double.parseDouble(right.toString());
            }
            case LESS_EQUAL -> {
                this.checkNumberOperands(expr.getOperator(), left, right);
                return Double.parseDouble(left.toString()) <= Double.parseDouble(right.toString());
            }
            case BANG_EQUAL -> {
                return !this.isEqual(left, right);
            }
            case EQUAL_EQUAL -> {
                return this.isEqual(left, right);
            }
        }
        return null;
    }

    @Override
    public Object visit(final GroupingExp expr) {
        return this.evaluate(expr.getExpression());
    }

    @Override
    public Object visit(final LiteralExp expr) {
        return expr.getValue();
    }

    @Override
    public Object visit(final AssignExp expr) {
        final Token name = expr.getName();
        final Object value = this.evaluate(expr.getValue());

        final Integer distance = this.locals.get(expr);
        if (distance != null) {
            this.environment.assignAt(distance, name, value);
        }
        else {
            this.globals.assign(name, value);
        }
        return value;
    }

    @Override
    public Object visit(final UnaryExp expr) {
        final Object right = this.evaluate(expr.getRightExp());
        switch (expr.getOperator().type()) {
            case MINUS: {
                this.checkNumberOperand(expr.getOperator(), right);
                return -(double) right;
            }
            case BANG: {
                return !this.isTruthy(right);
            }
            case PLUS_PLUS: {
                if (right instanceof Double) {
                    return (double) right + 1;
                }
                this.checkNumberOperand(expr.getOperator(), right);
            }
            case MINUS_MINUS: {
                if (right instanceof Double) {
                    return (double) right - 1;
                }
                this.checkNumberOperand(expr.getOperator(), right);
            }
        }
        return null;
    }

    @Override
    public Object visit(final LogicalExp expr) {
        final Object left = this.evaluate(expr.getLeftExp());
        switch (expr.getOperator().type()) {
            case AND -> {
                final Object right = this.evaluate(expr.getRightExp());
                if (this.isTruthy(left)) return this.isTruthy(right);
                else return false;
            }
            case OR -> {
                final Object right = this.evaluate(expr.getRightExp());
                if (this.isTruthy(left)) return true;
                else return this.isTruthy(right);
            }
            case XOR -> {
                final Object right = this.evaluate(expr.getRightExp());
                return this.isTruthy(left) ^ this.isTruthy(right);
            }
        }
        return false;
    }

    @Override
    public Object visit(final BitwiseExp expr) {
        final Object left = this.evaluate(expr.getLeftExp());
        final Object right = this.evaluate(expr.getRightExp());

        if (left instanceof Double && right instanceof Double) {
            final int iLeft = (int) (double) left;
            final int iRight = (int) (double) right;

            switch (expr.getOperator().type()) {
                case SHIFT_RIGHT -> {
                    return iLeft >> iRight;
                }
                case SHIFT_LEFT -> {
                    return iLeft << iRight;
                }
                case LOGICAL_SHIFT_RIGHT -> {
                    return iLeft >>> iRight;
                }
            }
        }

        throw new RuntimeError(expr.getOperator(), "Bitwise left and right must be a Numbers");
    }

    @Override
    public Object visit(final ElvisExp expr) {
        final Object condition = this.evaluate(expr.getCondition());
        if (this.isTruthy(condition)) {
            return condition;
        }
        return this.evaluate(expr.getRightExp());
    }

    @Override
    public Object visit(final TernaryExp expr) {
        final Object condition = this.evaluate(expr.getCondition());
        if (this.isTruthy(condition)) {
            return this.evaluate(expr.getFirstExp());
        }
        return this.evaluate(expr.getSecondExp());
    }

    @Override
    public Object visit(final ArraySetExp expr) {
        final Array array = (Array) this.environment.get(expr.getName());
        final Double indexValue = (Double) this.evaluate(expr.getIndex());
        final int index = indexValue.intValue();

        if (index < 0 || array.getLength() < index) {
            throw new ArrayIndexOutOfBoundsException("Size can't be negative or bigger than array size");
        }

        final Object value = this.evaluate(expr.getValue());
        array.setValue(value, index);
        return value;
    }

    @Override
    public Object visit(final ArrayGetExp expr) {
        final Double value = (Double) this.evaluate(expr.getSize());
        final int length = value.intValue();
        if (length < 0) {
            throw new ArrayIndexOutOfBoundsException("Size can't be negative");
        }
        return new Array(length);
    }

    @Override
    public Object visit(final ArrayVariable expr) {
        final Array array = (Array) this.environment.get(expr.getName());
        final Double indexValue = (Double) this.evaluate(expr.getIndex());
        final int index = indexValue.intValue();

        if (index < 0 || array.getLength() < index) {
            throw new ArrayIndexOutOfBoundsException("Size can't be negative or bigger than array size");
        }

        return array.getValue(index);
    }

    @Override
    public Object visit(final PrefixExpression expr) {
        final HslCallable value = (HslCallable) this.environment.get(expr.getPrefixOperatorName());
        final List<Object> args = new ArrayList<>();
        args.add(this.evaluate(expr.getRightExpression()));
        return Result.of(() -> value.call(this, args))
                .rethrowUnchecked()
                .orNull();
    }

    @Override
    public Object visit(final InfixExpression expr) {
        final HslCallable value = (HslCallable) this.environment.get(expr.getInfixOperatorName());
        final List<Object> args = new ArrayList<>();
        args.add(this.evaluate(expr.getLeftExp()));
        args.add(this.evaluate(expr.getRightExp()));
        return Result.of(() -> value.call(this, args))
                .rethrowUnchecked()
                .orNull();
    }

    @Override
    public Object visit(final CallExp expr) {
        final Object callee = this.evaluate(expr.getCallee());

        final List<Object> arguments = new ArrayList<>();
        for (final Expression argument : expr.getArguments()) {
            arguments.add(this.evaluate(argument));
        }

        //Make sure this is callable type
        if (!(callee instanceof final HslCallable function)) {
            throw new RuntimeError(expr.getClosingParenthesis(), "Can only call functions and classes.");
        }

        if (arguments.size() != function.arity()) {
            throw new RuntimeError(expr.getClosingParenthesis(), "Expected " +
                    function.arity() + " arguments but got " +
                    arguments.size() + ".");
        }

        return Result.of(() -> function.call(this, arguments))
                .rethrowUnchecked()
                .orNull();
    }

    @Override
    public Object visit(final GetExp expr) {
        final Object object = this.evaluate(expr.getObject());
        if (object instanceof VirtualInstance) {
            return ((VirtualInstance) object).get(expr.getName());
        }
        throw new RuntimeError(expr.getName(), "Only instances have properties.");
    }

    @Override
    public Object visit(final SetExp expr) {
        final Object object = this.evaluate(expr.getObject());

        if (!(object instanceof VirtualInstance)) {
            throw new RuntimeError(expr.getName(), "Only instances have fields.");
        }

        final Object value = this.evaluate(expr.getValue());
        ((VirtualInstance) object).set(expr.getName(), value);
        return value;
    }

    @Override
    public Object visit(final ThisExp expr) {
        return this.lookUpVariable(expr.getKeyword(), expr);
    }

    @Override
    public Object visit(final Variable expr) {
        return this.lookUpVariable(expr.getName(), expr);
    }

    @Override
    public Object visit(final SuperExp expr) {
        final int distance = this.locals.get(expr);
        final VirtualClass superclass = (VirtualClass) this.environment.getAt(distance, "super");
        final VirtualInstance object = (VirtualInstance) this.environment.getAt(distance - 1, "this");
        final VirtualFunction method = superclass.findMethod(expr.getMethod().lexeme());

        //Can't find this property in super class so throw Runtime Exception
        if (method == null) {
            throw new RuntimeError(expr.getMethod()
                    , "Undefined property '" + expr.getMethod().lexeme() + "'.");
        }

        return method.bind(object);
    }

    @Override
    public Void visit(final ExpressionStatement statement) {
        this.evaluate(statement.getExpression());
        return null;
    }

    @Override
    public Void visit(final PrintStatement statement) {
        final Object value = this.evaluate(statement.getExpression());
        System.out.print(this.stringify(value));
        return null;
    }

    @Override
    public Void visit(final BlockStatement statement) {
        this.execute(statement.getStatementList(), new Environment(this.environment));
        return null;
    }

    @Override
    public Void visit(final IfStatement statement) {
        final Object conditionResult = this.evaluate(statement.getCondition());
        if (this.isTruthy(conditionResult)) {
            this.execute(statement.getThenBranch(), this.environment);
        }
        else if (statement.getElseBranch() != null) {
            this.execute(statement.getElseBranch(), this.environment);
        }
        return null;
    }

    @Override
    public Void visit(final WhileStatement statement) {
        while (this.isTruthy(this.evaluate(statement.getCondition()))) {
            try {
                this.execute(statement.getLoopBody());
            }
            catch (final MoveKeyword moveKeyword) {
                //Break;
                if (moveKeyword.getMoveType() == MoveKeyword.MoveType.BREAK) {
                    break;
                }
            }
        }
        return null;
    }

    @Override
    public Void visit(final DoWhileStatement statement) {
        final Environment whileEnvironment = new Environment(this.environment);
        final Environment previous = this.environment;
        this.environment = whileEnvironment;

        do {
            try {
                this.execute(statement.getLoopBody());
            }
            catch (final MoveKeyword moveKeyword) {
                //Break;
                if (moveKeyword.getMoveType() == MoveKeyword.MoveType.BREAK) {
                    break;
                }
            }
        }
        while (this.isTruthy(this.evaluate(statement.getCondition())));

        this.environment = previous;
        return null;
    }

    @Override
    public Void visit(final RepeatStatement statement) {
        final Environment repeatEnvironment = new Environment(this.environment);
        final Environment previous = this.environment;
        this.environment = repeatEnvironment;

        final Object value = this.evaluate(statement.getValue());

        final boolean isNotNumber = !(value instanceof Number);

        if (isNotNumber) {
            throw new RuntimeException("Repeat Counter must be number");
        }

        final int counter = (int) Double.parseDouble(value.toString());
        for (int i = 0; i < counter; i++) {
            try {
                this.execute(statement.getLoopBody());
            }
            catch (final MoveKeyword moveKeyword) {
                //Break;
                if (moveKeyword.getMoveType() == MoveKeyword.MoveType.BREAK) {
                    break;
                }
            }
        }
        this.environment = previous;
        return null;
    }

    @Override
    public Void visit(final Var statement) {
        Object value = null;
        if (statement.getInitializer() != null) {
            value = this.evaluate(statement.getInitializer());
        }
        this.environment.define(statement.getName().lexeme(), value);
        return null;
    }

    @Override
    public Void visit(final ReturnStatement statement) {
        Object value = null;
        if (statement.getValue() != null) {
            value = this.evaluate(statement.getValue());
        }
        throw new Return(value);
    }

    @Override
    public Void visit(final ClassStatement statement) {
        Object superclass = null;
        //Because superclass is variable expression assert it class not any other knid of expressions
        if (statement.getSuperClass() != null) {
            superclass = this.evaluate(statement.getSuperClass());
            if (!(superclass instanceof VirtualClass)) {
                throw new RuntimeError(statement.getSuperClass().getName(), "Superclass must be a class.");
            }
        }

        this.environment.define(statement.getName().lexeme(), null);

        if (statement.getSuperClass() != null) {
            this.environment = new Environment(this.environment);
            this.environment.define("super", superclass);
        }

        final Map<String, VirtualFunction> methods = new HashMap<>();

        //Bind all method into the class to call them with this leter
        for (final FunctionStatement method : statement.getMethods()) {
            final VirtualFunction function = new VirtualFunction(method, this.environment,
                    "init".equals(method.getName().lexeme()));
            methods.put(method.getName().lexeme(), function);
        }

        final VirtualClass virtualClass = new VirtualClass(statement.getName().lexeme(), (VirtualClass) superclass, this.environment, methods);

        if (superclass != null) {
            this.environment = this.environment.enclosing;
        }

        this.environment.assign(statement.getName(), virtualClass);
        return null;
    }

    @Override
    public Void visit(final NativeFunctionStatement statement) {
        final HslLibrary hslLibrary = new HslLibrary(statement, this.externalModules);
        this.environment.define(statement.getName().lexeme(), hslLibrary);
        return null;
    }

    @Override
    public Void visit(final TestStatement statement) {
        final String name = statement.getName().literal().toString();
        final Environment environment = new Environment(this.globals);
        this.execute(statement.getBody(), environment);
        try {
            this.execute(statement.getReturnValue());
        }
        catch (final Return r) {
            final Object value = r.getValue();
            final boolean val = this.isTruthy(value);
            final String state = (val) ? "Ok" : "Fail";
            System.out.println("\nTest with name (" + name + ") is -> " + state);
        }
        return null;
    }

    @Override
    public Void visit(final ModuleStatement statement) {
        // TODO: Implement me!
        return null;
    }

    @Override
    public Void visit(final BreakStatement statement) {
        throw new MoveKeyword(MoveKeyword.MoveType.BREAK);
    }

    @Override
    public Void visit(final ContinueStatement statement) {
        throw new MoveKeyword(MoveKeyword.MoveType.CONTINUE);
    }

    @Override
    public Void visit(final FunctionStatement statement) {
        final VirtualFunction function = new VirtualFunction(statement, this.environment, false);
        this.environment.define(statement.getName().lexeme(), function);
        return null;
    }

    @Override
    public Void visit(final ExtensionStatement statement) {
        final VirtualClass extensionClass = (VirtualClass) this.environment.get(statement.getClassName());
        if (extensionClass == null) {
            throw new RuntimeException("Can't find this extension class");
        }
        final FunctionStatement functionStatement = statement.getFunctionStatement();
        final VirtualFunction extension = new VirtualFunction(functionStatement, extensionClass.getEnvironment(), false);
        if (extensionClass.findMethod(functionStatement.getName().lexeme()) != null) {
            throw new RuntimeException(extensionClass.getName() + " class already have method with same name = " + functionStatement.getName().lexeme());
        }
        extensionClass.addMethod(functionStatement.getName().lexeme(), extension);
        return null;
    }

    private Object evaluate(final Expression expr) {
        return expr.accept(this);
    }

    private boolean isTruthy(final Object object) {
        if (object == null) return false;
        if (object instanceof Boolean) return (boolean) object;
        return true;
    }

    private boolean isEqual(final Object a, final Object b) {
        // nil is only equal to nil.
        if (a == null && b == null) return true;
        if (a == null) return false;

        return a.equals(b);
    }

    private String stringify(final Object object) {
        if (object == null) return "nil";
        // Hack. Work around Java adding ".0" to integer-valued doubles.
        if (object instanceof Double) {
            String text = object.toString();
            if (text.endsWith(".0")) {
                text = text.substring(0, text.length() - 2);
            }
            return text;
        }

        return object.toString()
                .replaceAll("\\\\n", "\n")
                .replaceAll("\\\\t", "\t");
    }

    private void checkNumberOperand(final Token operator, final Object operand) {
        if (operand instanceof Double) return;
        throw new RuntimeError(operator, "Operand must be a number.");
    }

    private void checkNumberOperands(final Token operator, final Object left, final Object right) {
        if (left instanceof Number && right instanceof Number) return;
        throw new RuntimeError(operator, "Operands must the same type -> number.");
    }

    private void execute(final Statement stmt) {
        stmt.accept(this);
    }

    public void execute(final List<Statement> statementList, final Environment localEnvironment) {
        final Environment previous = this.environment;
        try {
            //Make current environment is block local not global
            this.environment = localEnvironment;
            //Execute every statement in block
            for (final Statement statement : statementList) {
                try {
                    this.execute(statement);
                }
                catch (final MoveKeyword type) {
                    if (type.getMoveType() == MoveKeyword.MoveType.CONTINUE) {
                        break;
                    }
                }
            }
        }
        finally {
            //Same like pop environment from stack
            this.environment = previous;
        }
    }

    private Object lookUpVariable(final Token name, final Expression expr) {
        //TODO : Fix it should return distance 1 without check
        if (name.type() == TokenType.THIS) {
            return this.environment.getAt(1, name.lexeme());
        }

        final Integer distance = this.locals.get(expr);
        if (distance != null) {
            //find variable value in locales score
            return this.environment.getAt(distance, name.lexeme());
        }
        else {
            //If can't find distance in locales so it must be global variable
            return this.globals.get(name);
        }
    }

    public void resolve(final Expression expr, final int depth) {
        this.locals.put(expr, depth);
    }

    public Environment getEnvironment() {
        return this.environment;
    }
}
