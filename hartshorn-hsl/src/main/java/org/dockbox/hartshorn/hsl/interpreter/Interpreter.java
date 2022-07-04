/*
 * Copyright 2019-2022 the original author or authors.
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

import org.dockbox.hartshorn.hsl.ast.MoveKeyword;
import org.dockbox.hartshorn.hsl.ast.expression.ArrayGetExpression;
import org.dockbox.hartshorn.hsl.ast.expression.ArraySetExpression;
import org.dockbox.hartshorn.hsl.ast.expression.ArrayVariable;
import org.dockbox.hartshorn.hsl.ast.expression.AssignExpression;
import org.dockbox.hartshorn.hsl.ast.expression.BinaryExpression;
import org.dockbox.hartshorn.hsl.ast.expression.BitwiseExpression;
import org.dockbox.hartshorn.hsl.ast.expression.ElvisExpression;
import org.dockbox.hartshorn.hsl.ast.expression.Expression;
import org.dockbox.hartshorn.hsl.ast.expression.FunctionCallExpression;
import org.dockbox.hartshorn.hsl.ast.expression.GetExpression;
import org.dockbox.hartshorn.hsl.ast.expression.GroupingExpression;
import org.dockbox.hartshorn.hsl.ast.expression.InfixExpression;
import org.dockbox.hartshorn.hsl.ast.expression.LiteralExpression;
import org.dockbox.hartshorn.hsl.ast.expression.LogicalExpression;
import org.dockbox.hartshorn.hsl.ast.expression.PrefixExpression;
import org.dockbox.hartshorn.hsl.ast.expression.SetExpression;
import org.dockbox.hartshorn.hsl.ast.expression.SuperExpression;
import org.dockbox.hartshorn.hsl.ast.expression.TernaryExpression;
import org.dockbox.hartshorn.hsl.ast.expression.ThisExpression;
import org.dockbox.hartshorn.hsl.ast.expression.UnaryExpression;
import org.dockbox.hartshorn.hsl.ast.expression.VariableExpression;
import org.dockbox.hartshorn.hsl.ast.statement.BlockStatement;
import org.dockbox.hartshorn.hsl.ast.statement.BreakStatement;
import org.dockbox.hartshorn.hsl.ast.statement.ClassStatement;
import org.dockbox.hartshorn.hsl.ast.statement.ContinueStatement;
import org.dockbox.hartshorn.hsl.ast.statement.DoWhileStatement;
import org.dockbox.hartshorn.hsl.ast.statement.ExpressionStatement;
import org.dockbox.hartshorn.hsl.ast.statement.ExtensionStatement;
import org.dockbox.hartshorn.hsl.ast.statement.FunctionStatement;
import org.dockbox.hartshorn.hsl.ast.statement.IfStatement;
import org.dockbox.hartshorn.hsl.ast.statement.ModuleStatement;
import org.dockbox.hartshorn.hsl.ast.statement.NativeFunctionStatement;
import org.dockbox.hartshorn.hsl.ast.statement.PrintStatement;
import org.dockbox.hartshorn.hsl.ast.statement.RepeatStatement;
import org.dockbox.hartshorn.hsl.ast.statement.ReturnStatement;
import org.dockbox.hartshorn.hsl.ast.statement.Statement;
import org.dockbox.hartshorn.hsl.ast.statement.TestStatement;
import org.dockbox.hartshorn.hsl.ast.statement.VariableStatement;
import org.dockbox.hartshorn.hsl.ast.statement.WhileStatement;
import org.dockbox.hartshorn.hsl.callable.ErrorReporter;
import org.dockbox.hartshorn.hsl.callable.PropertyContainer;
import org.dockbox.hartshorn.hsl.callable.CallableNode;
import org.dockbox.hartshorn.hsl.callable.external.ExternalClass;
import org.dockbox.hartshorn.hsl.callable.external.ExternalInstance;
import org.dockbox.hartshorn.hsl.callable.module.HslLibrary;
import org.dockbox.hartshorn.hsl.callable.module.NativeModule;
import org.dockbox.hartshorn.hsl.callable.virtual.VirtualClass;
import org.dockbox.hartshorn.hsl.callable.virtual.VirtualFunction;
import org.dockbox.hartshorn.hsl.callable.virtual.VirtualInstance;
import org.dockbox.hartshorn.hsl.runtime.Phase;
import org.dockbox.hartshorn.hsl.runtime.Return;
import org.dockbox.hartshorn.hsl.runtime.RuntimeError;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.TokenType;
import org.dockbox.hartshorn.hsl.visitors.ExpressionVisitor;
import org.dockbox.hartshorn.hsl.visitors.StatementVisitor;
import org.dockbox.hartshorn.inject.binding.Bound;
import org.dockbox.hartshorn.util.Result;
import org.slf4j.Logger;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;

/**
 * Standard interpreter for HSL. This interpreter is capable of executing HSL code by visiting the AST
 * step by step. The interpreter is capable of handling all types of statements and expressions. The
 * interpreter is also capable of handling external classes and modules, as well as virtual classes and
 * functions.
 *
 * <p>During the execution of a script, the interpreter will track its global variables in a
 * {@link VariableScope}, and report any errors or results to the configured {@link ErrorReporter} and
 * {@link ResultCollector} respectively.
 *
 * <p><code>print</code> statements are handled by the configured {@link Logger}, and are not persisted
 * in a local state.
 *
 * <p>Any interpreter instance can only be used <b>once</b>, and should be disposed of after use. This
 * is to prevent scope pollution, and potential leaking of errors and results.
 *
 * <p>Interpretation starts with the {@link #interpret(List)} method, which takes a list of statements
 * which have been previously parsed by a {@link org.dockbox.hartshorn.hsl.parser.Parser}, and
 * preferably resolved by a {@link org.dockbox.hartshorn.hsl.semantic.Resolver}.
 *
 * @author Guus Lieben
 * @since 22.4
 */
public class Interpreter implements ExpressionVisitor<Object>, StatementVisitor<Void> {

    private final Map<String, ExternalInstance> externalVariables = new ConcurrentHashMap<>();
    private final Map<String, ExternalClass<?>> imports = new ConcurrentHashMap<>();
    private final Map<Expression, Integer> locals = new ConcurrentHashMap<>();

    private final Map<String, NativeModule> externalModules;

    private final ErrorReporter errorReporter;
    private final ResultCollector resultCollector;
    private final Logger logger;

    private VariableScope global = new VariableScope();
    private VariableScope visitingScope = this.global;
    private boolean isRunning = false;

    @Bound
    public Interpreter(final ErrorReporter errorReporter, final ResultCollector resultCollector, final Logger logger, final Map<String, NativeModule> externalModules) {
        this.errorReporter = errorReporter;
        this.resultCollector = resultCollector;
        this.logger = logger;
        this.externalModules = new ConcurrentHashMap<>(externalModules);
    }

    /**
     * Restores the interpreter to its initial state. This is to prevent scope pollution, and potential
     * leaking of errors and results. This does not clear the external modules and variables, nor the
     * dynamic imports, as these can be reused safely.
     *
     * <p>This method should be called before starting a new runtime. This should be at least before a
     * potential {@link org.dockbox.hartshorn.hsl.semantic.Resolver} is called, as the resolver will
     * typically modify the {@link #locals local variable} map.
     */
    public void restore() {
        this.global = new VariableScope();
        this.visitingScope = this.global;
        this.locals.clear();
        this.errorReporter.clear();
        this.resultCollector.clear();
    }

    public void externalModule(final String name, final NativeModule module) {
        this.externalModules.put(name, module);
    }

    public void externalModules(final Map<String, NativeModule> externalModules) {
        this.externalModules.putAll(externalModules);
    }

    public Map<String, NativeModule> externalModules() {
        return this.externalModules;
    }

    public Map<String, Object> global() {
        return this.global.values();
    }

    public void interpret(final List<Statement> statements) {
        if (this.isRunning) {
            throw new IllegalStateException("Cannot reuse the same interpreter instance for multiple executions");
        }
        this.isRunning = true;
        try {
            for (final Statement statement : statements) {
                this.execute(statement);
            }
        }
        catch (final RuntimeError error) {
            this.errorReporter.error(Phase.INTERPRETING, error.token(), error.getMessage());
        }
        finally {
            this.isRunning = false;
        }
    }

    @Override
    public Object visit(final BinaryExpression expr) {
        Object left = this.evaluate(expr.leftExpression());
        Object right = this.evaluate(expr.rightExpression());

        left = this.unwrap(left);
        right = this.unwrap(right);

        switch (expr.operator().type()) {
            case PLUS -> {
                // Math plus
                if (left instanceof Double && right instanceof Double) {
                    return (double) left + (double) right;
                }
                // String Addition
                if (left instanceof String || right instanceof String) {
                    return left.toString() + right.toString();
                }

                // Special cases
                if ((left instanceof Character && right instanceof Character)) {
                    return String.valueOf(left) + right;
                }
                if ((left instanceof Character) && (right instanceof Double)) {
                    final int value = (Character) left;
                    return (double) right + value;
                }
                if ((left instanceof Double) && (right instanceof Character)) {
                    final int value = (Character) right;
                    return (double) left + value;
                }
                throw new RuntimeError(expr.operator(), "Unsupported child for PLUS.\n");
            }
            case MINUS -> {
                this.checkNumberOperands(expr.operator(), left, right);
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
                this.checkNumberOperands(expr.operator(), left, right);
                return (double) left * (double) right;
            }
            case SLASH -> {
                this.checkNumberOperands(expr.operator(), left, right);
                if ((double) right == 0) {
                    throw new RuntimeError(expr.operator(), "Can't use slash with zero double.");
                }
                return (double) left / (double) right;
            }
            case GREATER -> {
                this.checkNumberOperands(expr.operator(), left, right);
                return Double.parseDouble(left.toString()) > Double.parseDouble(right.toString());
            }
            case GREATER_EQUAL -> {
                this.checkNumberOperands(expr.operator(), left, right);
                return Double.parseDouble(left.toString()) >= Double.parseDouble(right.toString());
            }
            case LESS -> {
                this.checkNumberOperands(expr.operator(), left, right);
                return Double.parseDouble(left.toString()) < Double.parseDouble(right.toString());
            }
            case LESS_EQUAL -> {
                this.checkNumberOperands(expr.operator(), left, right);
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
    public Object visit(final GroupingExpression expr) {
        return this.evaluate(expr.expression());
    }

    @Override
    public Object visit(final LiteralExpression expr) {
        return expr.value();
    }

    @Override
    public Object visit(final AssignExpression expr) {
        final Token name = expr.name();
        final Object value = this.evaluate(expr.value());

        final Integer distance = this.locals.get(expr);
        if (distance != null) {
            this.variableScope().assignAt(distance, name, value);
        }
        else {
            this.global.assign(name, value);
        }
        return value;
    }

    @Override
    public Object visit(final UnaryExpression expr) {
        final Object right = this.evaluate(expr.rightExpression());
        return switch (expr.operator().type()) {
            case MINUS -> {
                this.checkNumberOperand(expr.operator(), right);
                yield -(double) right;
            }
            case PLUS_PLUS -> {
                this.checkNumberOperand(expr.operator(), right);
                yield (double) right + 1;
            }
            case MINUS_MINUS -> {
                this.checkNumberOperand(expr.operator(), right);
                yield (double) right - 1;
            }
            case BANG -> !this.isTruthy(right);
            case COMPLEMENT -> {
                this.checkNumberOperand(expr.operator(), right);
                yield ~((Double) right).intValue();
            }
            default -> null;
        };
    }

    @Override
    public Object visit(final LogicalExpression expr) {
        final Object left = this.evaluate(expr.leftExpression());
        switch (expr.operator().type()) {
            case AND -> {
                if (!this.isTruthy(left)) {
                    return false;
                }
                // Don't evaluate right if left is not truthy
                final Object right = this.evaluate(expr.rightExpression());
                return this.isTruthy(right);
            }
            case OR -> {
                if (this.isTruthy(left)) {
                    return true;
                }
                // No need to evaluate right if left is already truthy
                final Object right = this.evaluate(expr.rightExpression());
                return this.isTruthy(right);
            }
            case XOR -> {
                final Object right = this.evaluate(expr.rightExpression());
                return this.xor(left, right);
            }
            default -> throw new RuntimeError(expr.operator(), "Unsupported logical operator.");
        }
    }

    @Override
    public Object visit(final BitwiseExpression expr) {
        final Object left = this.evaluate(expr.leftExpression());
        final Object right = this.evaluate(expr.rightExpression());

        if (left instanceof Double && right instanceof Double) {
            final int iLeft = (int) (double) left;
            final int iRight = (int) (double) right;

            return switch (expr.operator().type()) {
                case SHIFT_RIGHT -> iLeft >> iRight;
                case SHIFT_LEFT -> iLeft << iRight;
                case LOGICAL_SHIFT_RIGHT -> iLeft >>> iRight;
                case BITWISE_AND -> iLeft & iRight;
                case BITWISE_OR -> iLeft | iRight;
                case XOR -> this.xor(iLeft, iRight);
                default -> throw new RuntimeError(expr.operator(), "Unsupported bitwise operator.");
            };
        }
        throw new RuntimeError(expr.operator(), "Bitwise left and right must be a Numbers");
    }

    private Object xor(final Object left, final Object right) {
        if (left instanceof Number && right instanceof Number) {
            final int iLeft = (int) (double) left;
            final int iRight = (int) (double) right;
            return iLeft ^ iRight;
        }
        return this.isTruthy(left) ^ this.isTruthy(right);
    }

    @Override
    public Object visit(final ElvisExpression expr) {
        final Object condition = this.evaluate(expr.condition());
        if (this.isTruthy(condition)) {
            return condition;
        }
        return this.evaluate(expr.rightExpression());
    }

    @Override
    public Object visit(final TernaryExpression expr) {
        final Object condition = this.evaluate(expr.condition());
        if (this.isTruthy(condition)) {
            return this.evaluate(expr.firstExpression());
        }
        return this.evaluate(expr.secondExpression());
    }

    @Override
    public Object visit(final ArraySetExpression expr) {
        return this.accessArray(expr.name(), expr.index(), (array, index) -> {
            final Object value = this.evaluate(expr.value());
            array.value(value, index);
            return value;
        });
    }

    @Override
    public Object visit(final ArrayGetExpression expr) {
        final Double value = (Double) this.evaluate(expr.size());
        final int length = value.intValue();
        if (length < 0) {
            throw new ArrayIndexOutOfBoundsException("Size can't be negative");
        }
        return new Array(length);
    }

    @Override
    public Object visit(final ArrayVariable expr) {
        return this.accessArray(expr.name(), expr.index(), Array::value);
    }

    private Object accessArray(final Token name, final Expression indexExp, final BiFunction<Array, Integer, Object> converter) {
        final Array array = (Array) this.variableScope().get(name);
        final Double indexValue = (Double) this.evaluate(indexExp);
        final int index = indexValue.intValue();

        if (index < 0 || array.length() < index) {
            throw new ArrayIndexOutOfBoundsException("Size can't be negative or bigger than array size");
        }

        return converter.apply(array, index);
    }

    @Override
    public Object visit(final PrefixExpression expr) {
        final CallableNode value = (CallableNode) this.variableScope().get(expr.prefixOperatorName());
        final List<Object> args = new ArrayList<>();
        args.add(this.evaluate(expr.rightExpression()));
        return Result.of(() -> value.call(expr.prefixOperatorName(), this, args))
                .caught(e -> this.errorReporter.error(Phase.INTERPRETING, expr.prefixOperatorName().line(), e.getMessage()))
                .orNull();
    }

    @Override
    public Object visit(final InfixExpression expr) {
        final CallableNode value = (CallableNode) this.variableScope().get(expr.infixOperatorName());
        final List<Object> args = new ArrayList<>();
        args.add(this.evaluate(expr.leftExpression()));
        args.add(this.evaluate(expr.rightExpression()));

        return Result.of(() -> value.call(expr.infixOperatorName(), this, args))
                .caught(e -> this.errorReporter.error(Phase.INTERPRETING, expr.infixOperatorName().line(), e.getMessage()))
                .orNull();
    }

    @Override
    public Object visit(final FunctionCallExpression expr) {
        final Object callee = this.evaluate(expr.callee());

        final List<Object> arguments = new ArrayList<>();
        for (final Expression argument : expr.arguments()) {
            Object evaluated = this.evaluate(argument);
            if (evaluated instanceof ExternalInstance external) evaluated = external.instance();
            arguments.add(evaluated);
        }

        // Can't call non-callable nodes..
        if (!(callee instanceof final CallableNode function)) {
            throw new RuntimeError(expr.closingParenthesis(), "Can only call functions and classes, but received " + callee + ".");
        }

        return Result.of(() -> function.call(expr.closingParenthesis(), this, arguments))
                .caught(e -> this.errorReporter.error(Phase.INTERPRETING, expr.closingParenthesis().line(), e.getMessage()))
                .orNull();
    }

    @Override
    public Object visit(final GetExpression expr) {
        final Object object = this.evaluate(expr.object());
        if (object instanceof PropertyContainer instance) {
            return instance.get(expr.name());
        }
        throw new RuntimeError(expr.name(), "Only instances have properties.");
    }

    @Override
    public Object visit(final SetExpression expr) {
        final Object object = this.evaluate(expr.object());

        if (object instanceof PropertyContainer instance) {
            final Object value = this.evaluate(expr.value());
            instance.set(expr.name(), value);
            return value;
        }
        throw new RuntimeError(expr.name(), "Only instances have properties.");
    }

    @Override
    public Object visit(final ThisExpression expr) {
        return this.lookUpVariable(expr.keyword(), expr);
    }

    @Override
    public Object visit(final VariableExpression expr) {
        return this.lookUpVariable(expr.name(), expr);
    }

    @Override
    public Object visit(final SuperExpression expr) {
        final int distance = this.locals.get(expr);
        final VirtualClass superclass = (VirtualClass) this.variableScope().getAt(expr.method(), distance, TokenType.SUPER.representation());
        final VirtualInstance object = (VirtualInstance) this.variableScope().getAt(expr.method(), distance - 1, TokenType.THIS.representation());
        final VirtualFunction method = superclass.findMethod(expr.method().lexeme());

        if (method == null) {
            throw new RuntimeError(expr.method(), "Undefined property '" + expr.method().lexeme() + "'.");
        }
        return method.bind(object);
    }

    @Override
    public Void visit(final ExpressionStatement statement) {
        this.evaluate(statement.expression());
        return null;
    }

    @Override
    public Void visit(final PrintStatement statement) {
        final Object value = this.evaluate(statement.expression());
        this.logger.info(this.stringify(value));
        return null;
    }

    @Override
    public Void visit(final BlockStatement statement) {
        this.execute(statement.statementList(), new VariableScope(this.variableScope()));
        return null;
    }

    @Override
    public Void visit(final IfStatement statement) {
        final Object conditionResult = this.evaluate(statement.condition());
        if (this.isTruthy(conditionResult)) {
            this.execute(statement.thenBranch(), this.variableScope());
        }
        else if (statement.elseBranch() != null) {
            this.execute(statement.elseBranch(), this.variableScope());
        }
        return null;
    }

    @Override
    public Void visit(final WhileStatement statement) {
        while (this.isTruthy(this.evaluate(statement.condition()))) {
            try {
                this.execute(statement.loopBody());
            }
            catch (final MoveKeyword moveKeyword) {
                if (moveKeyword.moveType() == MoveKeyword.MoveType.BREAK) {
                    break;
                }
            }
        }
        return null;
    }

    @Override
    public Void visit(final DoWhileStatement statement) {
        final VariableScope whileVariableScope = new VariableScope(this.variableScope());
        final VariableScope previous = this.variableScope();
        this.visitingScope = whileVariableScope;

        do {
            try {
                this.execute(statement.loopBody());
            }
            catch (final MoveKeyword moveKeyword) {
                if (moveKeyword.moveType() == MoveKeyword.MoveType.BREAK) break;
            }
        }
        while (this.isTruthy(this.evaluate(statement.condition())));

        this.visitingScope = previous;
        return null;
    }

    @Override
    public Void visit(final RepeatStatement statement) {
        final VariableScope repeatVariableScope = new VariableScope(this.variableScope());
        final VariableScope previous = this.variableScope();
        this.visitingScope = repeatVariableScope;

        final Object value = this.evaluate(statement.value());

        final boolean isNotNumber = !(value instanceof Number);

        if (isNotNumber) {
            throw new RuntimeException("Repeat Counter must be number");
        }

        final int counter = (int) Double.parseDouble(value.toString());
        for (int i = 0; i < counter; i++) {
            try {
                this.execute(statement.loopBody());
            }
            catch (final MoveKeyword moveKeyword) {
                if (moveKeyword.moveType() == MoveKeyword.MoveType.BREAK) break;
            }
        }
        this.visitingScope = previous;
        return null;
    }

    @Override
    public Void visit(final VariableStatement statement) {
        Object value = null;
        if (statement.initializer() != null) {
            value = this.evaluate(statement.initializer());
        }
        this.variableScope().define(statement.name().lexeme(), value);
        return null;
    }

    @Override
    public Void visit(final ReturnStatement statement) {
        Object value = null;
        if (statement.value() != null) {
            value = this.evaluate(statement.value());
        }
        throw new Return(value);
    }

    @Override
    public Void visit(final ClassStatement statement) {
        Object superclass = null;
        // Because superclass is a variable expression assert it's a class
        if (statement.superClass() != null) {
            superclass = this.evaluate(statement.superClass());
            if (!(superclass instanceof VirtualClass)) {
                throw new RuntimeError(statement.superClass().name(), "Superclass must be a class.");
            }
        }

        this.variableScope().define(statement.name().lexeme(), null);

        if (statement.superClass() != null) {
            this.visitingScope = new VariableScope(this.variableScope());
            this.variableScope().define(TokenType.SUPER.representation(), superclass);
        }

        final Map<String, VirtualFunction> methods = new HashMap<>();

        // Bind all method into the class
        for (final FunctionStatement method : statement.methods()) {
            final VirtualFunction function = new VirtualFunction(method, this.variableScope(),
                    VirtualFunction.CLASS_INIT.equals(method.name().lexeme()));
            methods.put(method.name().lexeme(), function);
        }

        final VirtualClass virtualClass = new VirtualClass(statement.name().lexeme(), (VirtualClass) superclass, this.variableScope(), methods);

        if (superclass != null) {
            this.visitingScope = this.variableScope().enclosing();
        }

        this.variableScope().assign(statement.name(), virtualClass);
        return null;
    }

    @Override
    public Void visit(final NativeFunctionStatement statement) {
        final HslLibrary hslLibrary = new HslLibrary(statement, this.externalModules);
        this.variableScope().define(statement.name().lexeme(), hslLibrary);
        return null;
    }

    @Override
    public Void visit(final TestStatement statement) {
        final String name = statement.name().literal().toString();
        final VariableScope variableScope = new VariableScope(this.global);
        this.execute(statement.body(), variableScope);
        try {
            this.execute(statement.returnValue());
        }
        catch (final Return r) {
            final Object value = r.value();
            final boolean val = this.isTruthy(value);
            this.resultCollector.addResult(name, val);
        }
        return null;
    }

    @Override
    public Void visit(final ModuleStatement statement) {
        final String moduleName = statement.name().lexeme();
        final NativeModule module = this.externalModules().get(moduleName);
        for (final NativeFunctionStatement supportedFunction : module.supportedFunctions(statement.name())) {
            final HslLibrary library = new HslLibrary(supportedFunction, this.externalModules);
            this.global.define(supportedFunction.name().lexeme(), library);
        }
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
        final VirtualFunction function = new VirtualFunction(statement, this.variableScope(), false);
        this.variableScope().define(statement.name().lexeme(), function);
        return null;
    }

    @Override
    public Void visit(final ExtensionStatement statement) {
        final VirtualClass extensionClass = (VirtualClass) this.variableScope().get(statement.className());
        if (extensionClass == null) {
            throw new RuntimeException("Can't find extension class " + statement.className());
        }
        final FunctionStatement functionStatement = statement.functionStatement();
        final VirtualFunction extension = new VirtualFunction(functionStatement, extensionClass.variableScope(), false);
        if (extensionClass.findMethod(functionStatement.name().lexeme()) != null) {
            throw new RuntimeException(extensionClass.name() + " class already have method with same name = " + functionStatement.name().lexeme());
        }
        extensionClass.addMethod(functionStatement.name().lexeme(), extension);
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
        if (a == null && b == null) return true;
        if (a == null) return false;
        if (a instanceof Number na && b instanceof Number nb) {
            final BigDecimal ba = BigDecimal.valueOf(na.doubleValue());
            final BigDecimal bb = BigDecimal.valueOf(nb.doubleValue());
            return ba.compareTo(bb) == 0;
        }
        return a.equals(b);
    }

    private Object unwrap(final Object object) {
        if (object instanceof ExternalInstance external) return external.instance();
        return object;
    }

    private String stringify(final Object object) {
        if (object == null) return TokenType.NULL.representation();
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

    public void execute(final List<Statement> statementList, final VariableScope localVariableScope) {
        final VariableScope previous = this.variableScope();
        try {
            // Make current scope block local and not global
            this.visitingScope = localVariableScope;

            for (final Statement statement : statementList) {
                try {
                    this.execute(statement);
                }
                catch (final MoveKeyword type) {
                    if (type.moveType() == MoveKeyword.MoveType.CONTINUE) {
                        break;
                    }
                }
            }
        }
        finally {
            // 'Pop' scope from stack
            this.visitingScope = previous;
        }
    }

    private Object lookUpVariable(final Token name, final Expression expr) {
        if (name.type() == TokenType.THIS) {
            return this.visitingScope.getAt(name, 1);
        }

        final Integer distance = this.locals.get(expr);
        if (distance != null) {
            // Find variable value in locales score
            return this.visitingScope.getAt(name, distance);
        }
        else if (this.global.contains(name.lexeme())) {
            // Can't find distance in locales, so it must be global variable
            return this.global.get(name);
        }
        else if (this.externalVariables.containsKey(name.lexeme())) {
            return this.externalVariables.get(name.lexeme());
        }
        else if (this.imports.containsKey(name.lexeme())) {
            return this.imports.get(name.lexeme());
        }

        this.errorReporter.error(Phase.INTERPRETING, name, "Could not resolve variable " + name.lexeme() + ".");
        return null;
    }

    public void resolve(final Expression expr, final int depth) {
        this.locals.put(expr, depth);
    }

    public VariableScope variableScope() {
        return this.visitingScope;
    }

    public void global(final Map<String, Object> globalVariables) {
        globalVariables.forEach((name, instance) -> this.externalVariables.put(name, new ExternalInstance(instance)));
    }

    public void imports(final Map<String, Class<?>> imports) {
        imports.forEach((name, type) -> this.imports.put(name, new ExternalClass<>(type)));
    }
}
