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

import org.dockbox.hartshorn.hsl.ScriptEvaluationError;
import org.dockbox.hartshorn.hsl.ast.ASTNode;
import org.dockbox.hartshorn.hsl.objects.external.ExternalInstance;
import org.dockbox.hartshorn.hsl.runtime.DiagnosticMessage;
import org.dockbox.hartshorn.hsl.runtime.Phase;
import org.dockbox.hartshorn.hsl.token.Token;
import org.dockbox.hartshorn.util.Tuple;

import java.math.BigDecimal;
import java.util.Arrays;

/**
 * Utilities for interpreters, providing common functionality that should remain consistent across
 * different implementations of the interpreter.
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public final class InterpreterUtilities {

    /**
     * Determines the "truthiness" of an object according to HSL's rules. If a value is null or false,
     * it is considered "falsy". All other values are considered "truthy".
     *
     * @param object the object to evaluate
     * @return true if the object is "truthy", false if it is "falsy"
     */
    public static boolean isTruthy(Object object) {
        object = InterpreterUtilities.unwrap(object);
        if (object == null) {
            return false;
        }
        if (object instanceof Boolean bool) {
            return bool;
        }
        return true;
    }

    /**
     * Compares two objects for equality according to HSL's rules. If both objects are null, or {@link
     * Object#equals(Object)} returns true, they are considered equal. If both objects are instances of
     * {@link Number}, they are compared numerically using {@link BigDecimal}.
     *
     * @param a the first object
     * @param b the second object
     * @return true if the objects are considered equal, false otherwise
     */
    public static boolean isEqual(Object a, Object b) {
        if (a == null && b == null) {
            return true;
        }
        if (a == null) {
            return false;
        }
        if (a instanceof Number na && b instanceof Number nb) {
            BigDecimal ba = BigDecimal.valueOf(na.doubleValue());
            BigDecimal bb = BigDecimal.valueOf(nb.doubleValue());
            return ba.compareTo(bb) == 0;
        }
        return a.equals(b);
    }

    /**
     * Unwraps an object if it is an {@link ExternalInstance}, returning the underlying instance.
     * If the object is not an {@link ExternalInstance}, it is returned as-is.
     *
     * @param object the object to unwrap
     * @return the unwrapped object
     */
    public static Object unwrap(Object object) {
        if (object instanceof ExternalInstance external) {
            return external.instance();
        }
        return object;
    }

    /**
     * Checks if the given operand is a number. If it is not, a {@link ScriptEvaluationError} is thrown
     * with a message indicating that a non-number operand was provided.
     *
     * @param operator the operator token
     * @param operand the operand to check
     *
     * @return the operand cast to a {@link Number} if it is valid
     *
     * @see #checkNumberOperands(Token, Object, Object) for binary operand checking
     */
    public static Number checkNumberOperand(Token operator, Object operand) {
        if (operand instanceof Number number) {
            return number;
        }
        throw ScriptEvaluationError.builder(Phase.INTERPRETING)
                .message(DiagnosticMessage.NON_NUMBER_OPERAND, operand)
                .at(operator)
                .build();
    }

    /**
     * Checks if both given operands are numbers. If either is not, a {@link ScriptEvaluationError} is
     * thrown with a message indicating that non-number operands were provided.
     *
     * @param operator the operator token
     * @param left the left operand
     * @param right the right operand
     *
     * @return a {@link Tuple} containing both operands cast to {@link Number} if they are valid
     *
     * @see #checkNumberOperand(Token, Object) for unary operand checking
     */
    public static Tuple<Number, Number> checkNumberOperands(Token operator, Object left, Object right) {
        if (left instanceof Number leftNumber && right instanceof Number rightNumber) {
            return new Tuple<>(leftNumber, rightNumber);
        }
        throw ScriptEvaluationError.builder(Phase.INTERPRETING)
                .message(DiagnosticMessage.OPERAND_MISMATCH, "number", left, right)
                .at(operator)
                .build();
    }

    public static Iterable<?> checkIterable(ASTNode at, Object collection) {
        collection = InterpreterUtilities.unwrap(collection);

        if (collection instanceof Iterable<?> collectionIterable) {
            return collectionIterable;
        }
        else if (collection != null && collection.getClass().isArray()) {
            return Arrays.asList((Object[]) collection);
        }
        else {
            throw ScriptEvaluationError.builder(Phase.INTERPRETING)
                    .message(DiagnosticMessage.NON_ITERABLE_COLLECTION, collection)
                    .at(at)
                    .build();
        }
    }
}
