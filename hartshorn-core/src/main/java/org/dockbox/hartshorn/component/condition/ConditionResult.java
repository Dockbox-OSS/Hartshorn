/*
 * Copyright 2019-2023 the original author or authors.
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

package org.dockbox.hartshorn.component.condition;

/**
 * Represents the result of a {@link Condition} check. Contains a boolean value indicating whether the condition matched
 * or not, and a message describing the result.
 *
 * @see Condition
 *
 * @since 0.4.12
 *
 * @author Guus Lieben
 */
public final class ConditionResult {

    private final boolean matches;
    private final String message;

    private ConditionResult(boolean matches, String message) {
        this.matches = matches;
        this.message = message;
    }

    /**
     * Returns {@code true} if the condition matched, {@code false} otherwise.
     *
     * @return {@code true} if the condition matched, {@code false} otherwise
     */
    public boolean matches() {
        return this.matches;
    }

    /**
     * Returns the message describing the result of the condition check. May be {@code null}.
     *
     * @return the message describing the result of the condition check
     */
    public String message() {
        return this.message;
    }

    /**
     * Creates a new {@link ConditionResult} with the given matched value. The message will be {@code null}.
     *
     * @param matched the matched value
     *
     * @return the new {@link ConditionResult}
     */
    public static ConditionResult of(boolean matched) {
        return new ConditionResult(matched, null);
    }

    /**
     * Creates a new matched {@link ConditionResult}. The message will be {@code null}.
     *
     * @return the new {@link ConditionResult}
     */
    public static ConditionResult matched() {
        return new ConditionResult(true, null);
    }

    /**
     * Creates a new unmatched {@link ConditionResult} with the given message.
     *
     * @param message the message
     * @return the new {@link ConditionResult}
     */
    public static ConditionResult notMatched(String message) {
        return new ConditionResult(false, message);
    }

    /**
     * Creates a new unmatched {@link ConditionResult} with a default message indicating that the given what and name
     * could not be found.
     *
     * @param what the what, e.g. "class"
     * @param name the name, e.g. "org.dockbox.hartshorn.component.condition.Condition"
     *
     * @return the new {@link ConditionResult}
     */
    public static ConditionResult notFound(String what, String name) {
        return new ConditionResult(false, "Could not find " + what + ": " + name);
    }

    /**
     * Creates a new matched {@link ConditionResult} with a default message indicating that the given what and name
     * were found with the given value.
     *
     * @param what the what, e.g. "property"
     * @param name the name, e.g. "user.name"
     * @param value the value, e.g. "John Doe"
     *
     * @return the new {@link ConditionResult}
     */
    public static ConditionResult found(String what, String name, String value) {
        return new ConditionResult(false, "Found " + what + " " + name + " with value " + value);
    }

    /**
     * Creates a new matched {@link ConditionResult} with a default message indicating that the given what and name
     * were found.
     *
     * @param what the what, e.g. "property"
     * @param name the name, e.g. "user.name"
     * @return the new {@link ConditionResult}
     */
    public static ConditionResult found(String what, String name) {
        return new ConditionResult(false, "Found " + what + " " + name);
    }

    /**
     * Creates a new unmatched {@link ConditionResult} with a default message indicating that the actual value of the
     * given what was not equal to the expected value.
     *
     * @param what the what, e.g. "property"
     * @param expected the expected value
     * @param actual the actual value
     * @return the new {@link ConditionResult}
     */
    public static ConditionResult notEqual(String what, String expected, String actual) {
        return new ConditionResult(false, "Expected " + what + " to be " + expected + " but was " + actual);
    }

    /**
     * Creates a new unmatched {@link ConditionResult} with a default message indicating that the condition declaration
     * was invalid.
     *
     * @param conditionType the condition type, e.g. "property"
     * @return the new {@link ConditionResult}
     */
    public static ConditionResult invalidCondition(String conditionType) {
        return new ConditionResult(false, "Invalid " + conditionType + " condition");
    }
}
