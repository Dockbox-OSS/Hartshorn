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

import java.lang.invoke.CallSite;
import java.lang.invoke.ConstantCallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

import org.dockbox.hartshorn.hsl.customizer.ScriptContext;

/**
 * Bootstrap methods used by {@code invokedynamic} instructions emitted during HSL bytecode compilation.
 *
 * @author Guus Lieben
 * @since 0.7.0
 */
public final class HslBootstrapMethods {

    private HslBootstrapMethods() {
    }

    @DynamicInvocation(
            sources = {"invokedynamic bootstrapBinaryOp"}
    )
    public static CallSite bootstrapBinaryOp(
            MethodHandles.Lookup lookup,
            String name,
            MethodType type,
            String operator
    ) throws Throwable {
        String methodName = switch (operator) {
            case "+" -> "add";
            case "-" -> "subtract";
            case "*" -> "multiply";
            case "/" -> "divide";
            case "%" -> "modulo";
            case "==" -> "equal";
            case "!=" -> "notEqual";
            case ">" -> "greater";
            case ">=" -> "greaterEqual";
            case "<" -> "less";
            case "<=" -> "lessEqual";
            case "&" -> "bitwiseAnd";
            case "|" -> "bitwiseOr";
            case "^" -> "bitwiseXor";
            case "<<" -> "shiftLeft";
            case ">>" -> "shiftRight";
            case ">>>" -> "logicalShiftRight";
            case "?:" -> "elvis";
            case ".." -> "range";
            default -> throw new IllegalArgumentException("Unknown binary operator: " + operator);
        };
        MethodHandle target = lookup.findStatic(
                HslRuntimeSupport.class,
                methodName,
                MethodType.methodType(
                        methodName.equals("equal") || methodName.equals("notEqual")
                                || methodName.equals("greater") || methodName.equals("greaterEqual")
                                || methodName.equals("less") || methodName.equals("lessEqual")
                                ? boolean.class : Object.class,
                        Object.class, Object.class
                )
        );
        return new ConstantCallSite(target.asType(type));
    }

    @DynamicInvocation(
            sources = {"invokedynamic bootstrapUnaryOp"}
    )
    public static CallSite bootstrapUnaryOp(
            MethodHandles.Lookup lookup,
            String name,
            MethodType type,
            String operator
    ) throws Throwable {
        String methodName = switch (operator) {
            case "-" -> "negate";
            case "!" -> "not";
            case "~" -> "complement";
            default -> throw new IllegalArgumentException("Unknown unary operator: " + operator);
        };
        MethodHandle target = lookup.findStatic(
                HslRuntimeSupport.class,
                methodName,
                MethodType.methodType(Object.class, Object.class)
        );
        return new ConstantCallSite(target.asType(type));
    }

    @DynamicInvocation(
            sources = {"invokedynamic bootstrapGetProperty"}
    )
    public static CallSite bootstrapGetProperty(
            MethodHandles.Lookup lookup,
            String name,
            MethodType type
    ) throws Throwable {
        MethodHandle target = lookup.findStatic(
                HslRuntimeSupport.class,
                "getProperty",
                MethodType.methodType(Object.class, Object.class, String.class, ScriptContext.class)
        );
        return new ConstantCallSite(target.asType(type));
    }

    @DynamicInvocation(
            sources = {"invokedynamic bootstrapSetProperty"}
    )
    public static CallSite bootstrapSetProperty(
            MethodHandles.Lookup lookup,
            String name,
            MethodType type
    ) throws Throwable {
        MethodHandle target = lookup.findStatic(
                HslRuntimeSupport.class,
                "setProperty",
                MethodType.methodType(Object.class, Object.class, String.class, Object.class, ScriptContext.class)
        );
        return new ConstantCallSite(target.asType(type));
    }

    @DynamicInvocation(
            sources = {"invokedynamic bootstrapInvoke"}
    )
    public static CallSite bootstrapInvoke(
            MethodHandles.Lookup lookup,
            String name,
            MethodType type
    ) throws Throwable {
        MethodHandle target = lookup.findStatic(
                HslRuntimeSupport.class,
                "invoke",
                MethodType.methodType(Object.class, Object.class, String.class, Object[].class, ScriptContext.class)
        );
        return new ConstantCallSite(target.asType(type));
    }
}
