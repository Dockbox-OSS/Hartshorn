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

package org.dockbox.hartshorn.hsl.compiler.runtime;

import org.dockbox.hartshorn.hsl.ScriptEvaluationError;
import org.dockbox.hartshorn.hsl.ast.statement.ClassStatement;
import org.dockbox.hartshorn.hsl.ast.statement.FunctionStatement;
import org.dockbox.hartshorn.hsl.compiler.ASTNodeCompiler;
import org.dockbox.hartshorn.hsl.compiler.BytecodeCompilerVisitor;
import org.dockbox.hartshorn.hsl.compiler.expression.*;
import org.dockbox.hartshorn.hsl.compiler.statement.*;
import org.dockbox.hartshorn.hsl.customizer.ScriptContext;
import org.dockbox.hartshorn.hsl.interpreter.Array;
import org.dockbox.hartshorn.hsl.interpreter.Interpreter;
import org.dockbox.hartshorn.hsl.interpreter.CodeExecutionUtilities;
import org.dockbox.hartshorn.hsl.interpreter.VariableScope;
import org.dockbox.hartshorn.hsl.interpreter.statement.ClassStatementInterpreter;
import org.dockbox.hartshorn.hsl.objects.*;
import org.dockbox.hartshorn.hsl.objects.external.ExternalFunction;
import org.dockbox.hartshorn.hsl.objects.virtual.VirtualFunction;
import org.dockbox.hartshorn.hsl.runtime.DiagnosticMessage;
import org.dockbox.hartshorn.hsl.runtime.Phase;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.hsl.token.type.LiteralTokenType;
import org.dockbox.hartshorn.util.Tuple;

import java.lang.constant.ConstantDescs;
import java.lang.constant.MethodTypeDesc;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Runtime helper and dynamic dispatch utility used by compiled JVM bytecode for HSL scripts.
 *
 * @author Guus Lieben
 * @since 0.7.0
 */
public final class HslRuntimeSupport {

    private static final MethodTypeDesc UNARY_OBJECT_DESC = MethodTypeDesc.of(
            ConstantDescs.CD_Object,
            ConstantDescs.CD_Object
    );
    private static final MethodTypeDesc BINARY_OBJECT_DESC = MethodTypeDesc.of(
            ConstantDescs.CD_Object,
            ConstantDescs.CD_Object,
            ConstantDescs.CD_Object
    );
    private static final MethodTypeDesc UNARY_PREDICATE_DESC = MethodTypeDesc.of(
            ConstantDescs.CD_boolean,
            ConstantDescs.CD_Object
    );
    private static final MethodTypeDesc BINARY_PREDICATE_DESC = MethodTypeDesc.of(
            ConstantDescs.CD_boolean,
            ConstantDescs.CD_Object,
            ConstantDescs.CD_Object
    );

    public static final MethodTypeDesc MTD_Add = BINARY_OBJECT_DESC;
    public static final MethodTypeDesc MTD_Subtract = BINARY_OBJECT_DESC;
    public static final MethodTypeDesc MTD_Multiply = BINARY_OBJECT_DESC;
    public static final MethodTypeDesc MTD_Divide = BINARY_OBJECT_DESC;
    public static final MethodTypeDesc MTD_Modulo = BINARY_OBJECT_DESC;
    public static final MethodTypeDesc MTD_Negate = UNARY_OBJECT_DESC;
    public static final MethodTypeDesc MTD_Not = UNARY_OBJECT_DESC;
    public static final MethodTypeDesc MTD_Complement = UNARY_OBJECT_DESC;

    public static final MethodTypeDesc MTD_IsTruthy = UNARY_PREDICATE_DESC;
    public static final MethodTypeDesc MTD_Equal = BINARY_PREDICATE_DESC;
    public static final MethodTypeDesc MTD_NotEqual = BINARY_PREDICATE_DESC;
    public static final MethodTypeDesc MTD_Greater = BINARY_PREDICATE_DESC;
    public static final MethodTypeDesc MTD_GreaterEqual = BINARY_PREDICATE_DESC;
    public static final MethodTypeDesc MTD_Less = BINARY_PREDICATE_DESC;
    public static final MethodTypeDesc MTD_LessEqual = BINARY_PREDICATE_DESC;

    public static final MethodTypeDesc MTD_BitwiseAnd = BINARY_OBJECT_DESC;
    public static final MethodTypeDesc MTD_BitwiseOr = BINARY_OBJECT_DESC;
    public static final MethodTypeDesc MTD_BitwiseXor = BINARY_OBJECT_DESC;
    public static final MethodTypeDesc MTD_ShiftLeft = BINARY_OBJECT_DESC;
    public static final MethodTypeDesc MTD_ShiftRight = BINARY_OBJECT_DESC;
    public static final MethodTypeDesc MTD_LogicalShiftRight = BINARY_OBJECT_DESC;
    public static final MethodTypeDesc MTD_Range = BINARY_OBJECT_DESC;

    public static final MethodTypeDesc MTD_CreateRootScope = MethodTypeDesc.of(ASTNodeCompiler.CD_VariableScope, ASTNodeCompiler.CD_ScriptContext);
    public static final MethodTypeDesc MTD_PushScope = MethodTypeDesc.of(ASTNodeCompiler.CD_VariableScope, ASTNodeCompiler.CD_VariableScope);
    public static final MethodTypeDesc MTD_PopScope = MethodTypeDesc.of(ASTNodeCompiler.CD_VariableScope, ASTNodeCompiler.CD_VariableScope);
    public static final MethodTypeDesc MTD_DeclareVariable = MethodTypeDesc.of(ConstantDescs.CD_void, ASTNodeCompiler.CD_VariableScope, ConstantDescs.CD_String, ConstantDescs.CD_Object, ConstantDescs.CD_boolean);
    public static final MethodTypeDesc MTD_LookupVariable = MethodTypeDesc.of(ConstantDescs.CD_Object, ASTNodeCompiler.CD_ScriptContext, ASTNodeCompiler.CD_VariableScope, ConstantDescs.CD_String, ConstantDescs.CD_Integer);
    public static final MethodTypeDesc MTD_AssignVariable = MethodTypeDesc.of(ConstantDescs.CD_void, ASTNodeCompiler.CD_ScriptContext, ASTNodeCompiler.CD_VariableScope, ConstantDescs.CD_String, ConstantDescs.CD_Integer, ConstantDescs.CD_Object);
    public static final MethodTypeDesc MTD_GetProperty = MethodTypeDesc.of(ConstantDescs.CD_Object, ConstantDescs.CD_Object, ConstantDescs.CD_String, ASTNodeCompiler.CD_ScriptContext);
    public static final MethodTypeDesc MTD_SetProperty = MethodTypeDesc.of(ConstantDescs.CD_Object, ConstantDescs.CD_Object, ConstantDescs.CD_String, ConstantDescs.CD_Object, ASTNodeCompiler.CD_ScriptContext);
    public static final MethodTypeDesc MTD_InvokeFunction = MethodTypeDesc.of(ConstantDescs.CD_Object, ConstantDescs.CD_Object, ConstantDescs.CD_Object.arrayType(), ASTNodeCompiler.CD_ScriptContext);
    public static final MethodTypeDesc MTD_CreateArray = MethodTypeDesc.of(ASTNodeCompiler.CD_Array, ConstantDescs.CD_Object.arrayType());
    public static final MethodTypeDesc MTD_ArrayGet = BINARY_OBJECT_DESC;
    public static final MethodTypeDesc MTD_ArraySet = MethodTypeDesc.of(ConstantDescs.CD_Object, ConstantDescs.CD_Object, ConstantDescs.CD_Object, ConstantDescs.CD_Object);
    public static final MethodTypeDesc MTD_DeclareFunctionById = MethodTypeDesc.of(ConstantDescs.CD_void, ASTNodeCompiler.CD_ScriptContext, ASTNodeCompiler.CD_VariableScope, ConstantDescs.CD_int);
    public static final MethodTypeDesc MTD_DeclareClassById = MTD_DeclareFunctionById;
    public static final MethodTypeDesc MTD_AddResult = MethodTypeDesc.of(ConstantDescs.CD_void, ASTNodeCompiler.CD_ScriptContext, ConstantDescs.CD_String, ConstantDescs.CD_Object);
    public static final MethodTypeDesc MTD_AddGlobalResult = MethodTypeDesc.of(ConstantDescs.CD_void, ASTNodeCompiler.CD_ScriptContext, ConstantDescs.CD_Object);

    private HslRuntimeSupport() {
    }

    @DynamicInvocation(
            callers = {BinaryExpressionCompiler.class, HslBootstrapMethods.class},
            sources = {"invokestatic add", "+"}
    )
    public static Object add(Object left, Object right) {
        left = CodeExecutionUtilities.unwrap(left);
        right = CodeExecutionUtilities.unwrap(right);

        if (left instanceof Number nLeft && right instanceof Number nRight) {
            return nLeft.doubleValue() + nRight.doubleValue();
        } else if (left instanceof String || right instanceof String) {
            return String.valueOf(left) + right;
        } else if (left instanceof Array aLeft) {
            List<Object> values = new ArrayList<>(Arrays.asList(aLeft.values()));
            if (right instanceof Array aRight) {
                values.addAll(Arrays.asList(aRight.values()));
            } else {
                values.add(right);
            }
            return new Array(values.toArray());
        }
        throw ScriptEvaluationError.builder(Phase.EXECUTING)
                .message(DiagnosticMessage.OPERAND_MISMATCH, "number, string or array", left, right)
                .build();
    }

    @DynamicInvocation(
            callers = {BinaryExpressionCompiler.class, HslBootstrapMethods.class},
            sources = {"invokestatic subtract", "-"}
    )
    public static Object subtract(Object left, Object right) {
        Tuple<Number, Number> tuple = checkNumbers(left, right);
        return tuple.left().doubleValue() - tuple.right().doubleValue();
    }

    @DynamicInvocation(
            callers = {BinaryExpressionCompiler.class, HslBootstrapMethods.class},
            sources = {"invokestatic multiply", "*"}
    )
    public static Object multiply(Object left, Object right) {
        Tuple<Number, Number> tuple = checkNumbers(left, right);
        return tuple.left().doubleValue() * tuple.right().doubleValue();
    }

    @DynamicInvocation(
            callers = {BinaryExpressionCompiler.class, HslBootstrapMethods.class},
            sources = {"invokestatic divide", "/"}
    )
    public static Object divide(Object left, Object right) {
        Tuple<Number, Number> tuple = checkNumbers(left, right);
        if (tuple.right().doubleValue() == 0) {
            throw ScriptEvaluationError.builder(Phase.EXECUTING)
                    .message(DiagnosticMessage.ILLEGAL_ZERO_DIVISION)
                    .build();
        }
        return tuple.left().doubleValue() / tuple.right().doubleValue();
    }

    @DynamicInvocation(
            callers = {BinaryExpressionCompiler.class, HslBootstrapMethods.class},
            sources = {"invokestatic modulo", "%"}
    )
    public static Object modulo(Object left, Object right) {
        Tuple<Number, Number> tuple = checkNumbers(left, right);
        if (tuple.right().doubleValue() == 0) {
            throw ScriptEvaluationError.builder(Phase.EXECUTING)
                    .message(DiagnosticMessage.ILLEGAL_ZERO_DIVISION)
                    .build();
        }
        return tuple.left().doubleValue() % tuple.right().doubleValue();
    }

    @DynamicInvocation(
            callers = {UnaryExpressionCompiler.class, HslBootstrapMethods.class},
            sources = {"invokestatic negate", "-"}
    )
    public static Object negate(Object value) {
        value = CodeExecutionUtilities.unwrap(value);
        if (value instanceof Number number) {
            return -number.doubleValue();
        }
        throw ScriptEvaluationError.builder(Phase.EXECUTING)
                .message(DiagnosticMessage.OPERAND_MISMATCH, "number", value, null)
                .build();
    }

    @DynamicInvocation(
            callers = {UnaryExpressionCompiler.class, HslBootstrapMethods.class},
            sources = {"invokestatic not", "!"}
    )
    public static Object not(Object value) {
        return !isTruthy(value);
    }

    @DynamicInvocation(
            callers = {UnaryExpressionCompiler.class, HslBootstrapMethods.class},
            sources = {"invokestatic complement", "~"}
    )
    public static Object complement(Object value) {
        value = CodeExecutionUtilities.unwrap(value);
        if (value instanceof Number number) {
            return (double) ~number.intValue();
        }
        throw ScriptEvaluationError.builder(Phase.EXECUTING)
                .message(DiagnosticMessage.ILLEGAL_BITWISE_OP, value, value != null ? value.getClass().getSimpleName() : null, null, null)
                .build();
    }

    @DynamicInvocation(
            callers = {
                    IfStatementCompiler.class,
                    WhileStatementCompiler.class,
                    DoWhileStatementCompiler.class,
                    ForStatementCompiler.class,
                    LogicalExpressionCompiler.class,
                    ElvisExpressionCompiler.class,
                    TernaryExpressionCompiler.class,
                    TestStatementCompiler.class,
            },
            sources = {"invokestatic isTruthy"}
    )
    public static boolean isTruthy(Object value) {
        return CodeExecutionUtilities.isTruthy(value);
    }

    @DynamicInvocation(
            callers = {BinaryExpressionCompiler.class, SwitchStatementCompiler.class, HslBootstrapMethods.class},
            sources = {"invokestatic equal", "=="}
    )
    public static boolean equal(Object left, Object right) {
        return CodeExecutionUtilities.isEqual(left, right);
    }

    @DynamicInvocation(
            callers = {BinaryExpressionCompiler.class, HslBootstrapMethods.class},
            sources = {"invokestatic notEqual", "!="}
    )
    public static boolean notEqual(Object left, Object right) {
        return !CodeExecutionUtilities.isEqual(left, right);
    }

    @DynamicInvocation(
            callers = {BinaryExpressionCompiler.class, HslBootstrapMethods.class},
            sources = {"invokestatic greater", ">"}
    )
    public static boolean greater(Object left, Object right) {
        Tuple<Number, Number> tuple = checkNumbers(left, right);
        return tuple.left().doubleValue() > tuple.right().doubleValue();
    }

    @DynamicInvocation(
            callers = {BinaryExpressionCompiler.class, HslBootstrapMethods.class},
            sources = {"invokestatic greaterEqual", ">="}
    )
    public static boolean greaterEqual(Object left, Object right) {
        Tuple<Number, Number> tuple = checkNumbers(left, right);
        return tuple.left().doubleValue() >= tuple.right().doubleValue();
    }

    @DynamicInvocation(
            callers = {BinaryExpressionCompiler.class, HslBootstrapMethods.class},
            sources = {"invokestatic less", "<"}
    )
    public static boolean less(Object left, Object right) {
        Tuple<Number, Number> tuple = checkNumbers(left, right);
        return tuple.left().doubleValue() < tuple.right().doubleValue();
    }

    @DynamicInvocation(
            callers = {BinaryExpressionCompiler.class, HslBootstrapMethods.class},
            sources = {"invokestatic lessEqual", "<="}
    )
    public static boolean lessEqual(Object left, Object right) {
        Tuple<Number, Number> tuple = checkNumbers(left, right);
        return tuple.left().doubleValue() <= tuple.right().doubleValue();
    }

    @DynamicInvocation(
            callers = {BitwiseExpressionCompiler.class, HslBootstrapMethods.class},
            sources = {"invokestatic bitwiseAnd", "&"}
    )
    public static Object bitwiseAnd(Object left, Object right) {
        Tuple<Integer, Integer> tuple = checkInts(left, right);
        return tuple.left() & tuple.right();
    }

    @DynamicInvocation(
            callers = {BitwiseExpressionCompiler.class, HslBootstrapMethods.class},
            sources = {"invokestatic bitwiseOr", "|"}
    )
    public static Object bitwiseOr(Object left, Object right) {
        Tuple<Integer, Integer> tuple = checkInts(left, right);
        return tuple.left() | tuple.right();
    }

    @DynamicInvocation(
            callers = {BitwiseExpressionCompiler.class, HslBootstrapMethods.class},
            sources = {"invokestatic bitwiseXor", "^"}
    )
    public static Object bitwiseXor(Object left, Object right) {
        left = CodeExecutionUtilities.unwrap(left);
        right = CodeExecutionUtilities.unwrap(right);
        if (left instanceof Number nLeft && right instanceof Number nRight) {
            return nLeft.intValue() ^ nRight.intValue();
        }
        return isTruthy(left) ^ isTruthy(right);
    }

    @DynamicInvocation(
            callers = {BitwiseExpressionCompiler.class, HslBootstrapMethods.class},
            sources = {"invokestatic shiftLeft", "<<"}
    )
    public static Object shiftLeft(Object left, Object right) {
        Tuple<Integer, Integer> tuple = checkInts(left, right);
        return tuple.left() << tuple.right();
    }

    @DynamicInvocation(
            callers = {BitwiseExpressionCompiler.class, HslBootstrapMethods.class},
            sources = {"invokestatic shiftRight", ">>"}
    )
    public static Object shiftRight(Object left, Object right) {
        Tuple<Integer, Integer> tuple = checkInts(left, right);
        return tuple.left() >> tuple.right();
    }

    @DynamicInvocation(
            callers = {BitwiseExpressionCompiler.class, HslBootstrapMethods.class},
            sources = {"invokestatic logicalShiftRight", ">>>"}
    )
    public static Object logicalShiftRight(Object left, Object right) {
        Tuple<Integer, Integer> tuple = checkInts(left, right);
        return tuple.left() >>> tuple.right();
    }

    @DynamicInvocation(
            callers = {ElvisExpressionCompiler.class, HslBootstrapMethods.class},
            sources = {"invokestatic elvis", "?:"}
    )
    public static Object elvis(Object condition, Object right) {
        return isTruthy(condition) ? condition : right;
    }

    @DynamicInvocation(
            callers = {RangeExpressionCompiler.class, HslBootstrapMethods.class},
            sources = {"invokestatic range", ".."}
    )
    public static Object range(Object start, Object end) {
        start = CodeExecutionUtilities.unwrap(start);
        end = CodeExecutionUtilities.unwrap(end);
        Tuple<Number, Number> tuple = checkNumbers(start, end);
        int min = tuple.left().intValue();
        int max = tuple.right().intValue();
        int length = max - min + 1;
        Object[] result = new Object[length];
        for (int i = 0; i < length; i++) {
            result[i] = (double) min + i;
        }
        return new Array(result);
    }

    private static Tuple<Number, Number> checkNumbers(Object left, Object right) {
        left = CodeExecutionUtilities.unwrap(left);
        right = CodeExecutionUtilities.unwrap(right);
        if (left instanceof Number nLeft && right instanceof Number nRight) {
            return new Tuple<>(nLeft, nRight);
        }
        throw ScriptEvaluationError.builder(Phase.EXECUTING)
                .message(DiagnosticMessage.OPERAND_MISMATCH, "number", left, right)
                .build();
    }

    private static Tuple<Integer, Integer> checkInts(Object left, Object right) {
        Tuple<Number, Number> numbers = checkNumbers(left, right);
        return new Tuple<>(numbers.left().intValue(), numbers.right().intValue());
    }

    @DynamicInvocation(
            callers = {BytecodeCompilerVisitor.class},
            sources = {"invokestatic createRootScope"}
    )
    public static VariableScope createRootScope(ScriptContext context) {
        if (context.interpreter() != null) {
            return context.interpreter().visitingScope();
        }
        return new VariableScope();
    }

    @DynamicInvocation(
            callers = {BlockStatementCompiler.class, ForStatementCompiler.class},
            sources = {"invokestatic pushScope"}
    )
    public static VariableScope pushScope(VariableScope parent) {
        return new VariableScope(parent);
    }

    @DynamicInvocation(
            callers = {BlockStatementCompiler.class, ForStatementCompiler.class},
            sources = {"invokestatic popScope"}
    )
    public static VariableScope popScope(VariableScope current) {
        return current.enclosing() != null ? current.enclosing() : current;
    }

    @DynamicInvocation(
            callers = {VariableStatementCompiler.class},
            sources = {"invokestatic declareVariable"}
    )
    public static void declareVariable(VariableScope scope, String name, Object value, boolean isFinal) {
        scope.define(name, value);
    }

    private static Token identifierToken(String name) {
        return new Token(LiteralTokenType.IDENTIFIER, name, 0, 0);
    }

    @DynamicInvocation(
            callers = {
                    VariableExpressionCompiler.class,
                    ArrayGetExpressionCompiler.class,
                    ArraySetExpressionCompiler.class,
                    ThisExpressionCompiler.class,
                    SuperExpressionCompiler.class
            },
            sources = {"invokestatic lookupVariable"}
    )
    public static Object lookupVariable(ScriptContext context, VariableScope scope, String name, Integer distance) {
        Token token = identifierToken(name);
        if (scope != null) {
            try {
                return scope.get(token);
            } catch (Exception ignored) {
            }
        }
        if (context.interpreter() != null) {
            try {
                return context.interpreter().lookUpVariable(token, null);
            } catch (Exception ignored) {
            }
        }
        if (context.result(name).present()) {
            return context.result(name).get();
        }
        throw ScriptEvaluationError.builder(Phase.EXECUTING)
                .message(DiagnosticMessage.UNDEFINED_VARIABLE, name)
                .build();
    }

    @DynamicInvocation(
            callers = {AssignExpressionCompiler.class},
            sources = {"invokestatic assignVariable"}
    )
    public static void assignVariable(ScriptContext context, VariableScope scope, String name, Integer distance, Object value) {
        Token token = identifierToken(name);
        if (scope != null) {
            try {
                scope.assign(token, value);
            } catch (Exception ignored) {
            }
        }
        if (context.interpreter() != null && context.interpreter().visitingScope() != null) {
            try {
                context.interpreter().visitingScope().assign(token, value);
            } catch (Exception ignored) {
            }
        }
        context.addResult(name, value);
    }

    @DynamicInvocation(
            callers = {GetExpressionCompiler.class, HslBootstrapMethods.class},
            sources = {"invokestatic getProperty", "bootstrapGetProperty"}
    )
    public static Object getProperty(Object target, String name, ScriptContext context) {
        target = CodeExecutionUtilities.unwrap(target);
        if (target instanceof PropertyContainer container) {
            Interpreter interpreter = context.interpreter();
            VariableScope scope = interpreter != null ? interpreter.visitingScope() : new VariableScope();
            Object result = container.get(interpreter, identifierToken(name), scope);
            if (result instanceof ExternalObjectReference objectReference) {
                result = objectReference.externalObject();
            }
            if (result instanceof ExternalFunction bindableNode && target instanceof InstanceReference instance) {
                return bindableNode.bind(instance);
            }
            return result;
        }
        throw ScriptEvaluationError.builder(Phase.EXECUTING)
                .message(DiagnosticMessage.NON_PROPERTY_CONTAINER, target)
                .build();
    }

    @DynamicInvocation(
            callers = {SetExpressionCompiler.class, HslBootstrapMethods.class},
            sources = {"invokestatic setProperty", "bootstrapSetProperty"}
    )
    public static Object setProperty(Object target, String name, Object value, ScriptContext context) {
        target = CodeExecutionUtilities.unwrap(target);
        if (target instanceof PropertyContainer container) {
            Interpreter interpreter = context.interpreter();
            VariableScope scope = interpreter != null ? interpreter.visitingScope() : new VariableScope();
            container.set(interpreter, identifierToken(name), value, scope);
            return value;
        }
        throw ScriptEvaluationError.builder(Phase.EXECUTING)
                .message(DiagnosticMessage.NON_PROPERTY_CONTAINER, target)
                .build();
    }

    @DynamicInvocation(
            callers = {HslBootstrapMethods.class},
            sources = {"invokestatic invoke", "bootstrapInvoke"}
    )
    public static Object invoke(Object target, String methodName, Object[] arguments, ScriptContext context) {
        Object property = getProperty(target, methodName, context);
        return invokeFunction(property, arguments, context);
    }

    @DynamicInvocation(
            callers = {HslBootstrapMethods.class, FunctionCallExpressionCompiler.class},
            sources = {"dynamic callee invocation"}
    )
    public static Object invokeFunction(Object callee, Object[] arguments, ScriptContext context) {
        callee = CodeExecutionUtilities.unwrap(callee);
        List<Object> args = arguments != null ? Arrays.asList(arguments) : List.of();
        Token token = identifierToken("call");

        if (!(callee instanceof CallableNode function)) {
            throw ScriptEvaluationError.builder(Phase.EXECUTING)
                    .message(DiagnosticMessage.NON_CALLABLE_CALLEE, callee)
                    .build();
        }

        try {
            Interpreter interpreter = context.interpreter();
            if (callee instanceof InstanceReference instance) {
                return function.call(token, interpreter, instance, args);
            } else if (callee instanceof BindableNode<?> bindable) {
                return function.call(token, interpreter, bindable.bound(), args);
            } else {
                return function.call(token, interpreter, null, args);
            }
        } catch (Exception e) {
            throw ScriptEvaluationError.builder(Phase.EXECUTING)
                    .cause(e)
                    .build();
        }
    }

    @DynamicInvocation(
            callers = {ArrayLiteralExpressionCompiler.class},
            sources = {"invokestatic createArray"}
    )
    public static Array createArray(Object[] elements) {
        return new Array(elements != null ? elements : new Object[0]);
    }

    @DynamicInvocation(
            callers = {ArrayGetExpressionCompiler.class},
            sources = {"invokestatic arrayGet"}
    )
    public static Object arrayGet(Object target, Object indexValue) {
        target = CodeExecutionUtilities.unwrap(target);
        indexValue = CodeExecutionUtilities.unwrap(indexValue);
        if (target instanceof Array array && indexValue instanceof Number num) {
            int index = num.intValue();
            if (index < 0 || index >= array.length()) {
                throw ScriptEvaluationError.builder(Phase.EXECUTING)
                        .message(DiagnosticMessage.ARRAY_INDEX_OUT_OF_BOUNDS, index, array.length())
                        .build();
            }
            return array.value(index);
        }
        throw ScriptEvaluationError.builder(Phase.EXECUTING)
                .message(DiagnosticMessage.NON_PROPERTY_CONTAINER, target)
                .build();
    }

    @DynamicInvocation(
            callers = {ArraySetExpressionCompiler.class},
            sources = {"invokestatic arraySet"}
    )
    public static Object arraySet(Object target, Object indexValue, Object value) {
        target = CodeExecutionUtilities.unwrap(target);
        indexValue = CodeExecutionUtilities.unwrap(indexValue);
        if (target instanceof Array array && indexValue instanceof Number num) {
            int index = num.intValue();
            if (index < 0 || index >= array.length()) {
                throw ScriptEvaluationError.builder(Phase.EXECUTING)
                        .message(DiagnosticMessage.ARRAY_INDEX_OUT_OF_BOUNDS, index, array.length())
                        .build();
            }
            array.value(value, index);
            return value;
        }
        throw ScriptEvaluationError.builder(Phase.EXECUTING)
                .message(DiagnosticMessage.NON_PROPERTY_CONTAINER, target)
                .build();
    }

    private static final Map<Integer, FunctionStatement> FUNCTION_REGISTRY = new ConcurrentHashMap<>();
    private static final Map<Integer, ClassStatement> CLASS_REGISTRY = new ConcurrentHashMap<>();
    private static final AtomicInteger REGISTRY_COUNTER = new AtomicInteger();

    public static int registerFunction(FunctionStatement statement) {
        int id = REGISTRY_COUNTER.incrementAndGet();
        FUNCTION_REGISTRY.put(id, statement);
        return id;
    }

    public static int registerClass(ClassStatement statement) {
        int id = REGISTRY_COUNTER.incrementAndGet();
        CLASS_REGISTRY.put(id, statement);
        return id;
    }

    @DynamicInvocation(
            callers = {FunctionStatementCompiler.class},
            sources = {"invokestatic declareFunctionById"}
    )
    public static void declareFunctionById(ScriptContext context, VariableScope scope, int id) {
        FunctionStatement statement = FUNCTION_REGISTRY.get(id);
        if (statement != null) {
            declareFunction(context, scope, statement);
        }
    }

    @DynamicInvocation(
            callers = {ClassStatementCompiler.class},
            sources = {"invokestatic declareClassById"}
    )
    public static void declareClassById(ScriptContext context, VariableScope scope, int id) {
        ClassStatement statement = CLASS_REGISTRY.get(id);
        if (statement != null) {
            declareClass(context, scope, statement);
        }
    }

    public static void declareFunction(ScriptContext context, VariableScope scope, FunctionStatement statement) {
        VariableScope parentScope = scope != null ? scope : (context.interpreter() != null ? context.interpreter().visitingScope() : new VariableScope());
        VirtualFunction function = new VirtualFunction(statement, parentScope, false);
        parentScope.define(statement.name().lexeme(), function);
    }

    public static void declareClass(ScriptContext context, VariableScope scope, ClassStatement statement) {
        if (context.interpreter() != null) {
            new ClassStatementInterpreter().interpret(statement, context.interpreter());
        }
    }

    @DynamicInvocation(
            callers = {VariableStatementCompiler.class, TestStatementCompiler.class},
            sources = {"invokestatic addResult"}
    )
    public static void addResult(ScriptContext context, String name, Object value) {
        context.addResult(name, value);
    }

    @DynamicInvocation(
            callers = {ExpressionStatementCompiler.class},
            sources = {"invokestatic addGlobalResult"}
    )
    public static void addGlobalResult(ScriptContext context, Object value) {
        context.addResult(value);
    }
}
