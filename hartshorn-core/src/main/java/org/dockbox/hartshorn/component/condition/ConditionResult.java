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

public final class ConditionResult {

    private final boolean matches;
    private final String message;

    private ConditionResult(boolean matches, String message) {
        this.matches = matches;
        this.message = message;
    }

    public boolean matches() {
        return this.matches;
    }

    public String message() {
        return this.message;
    }

    public static ConditionResult of(boolean matched) {
        return new ConditionResult(matched, null);
    }

    public static ConditionResult matched() {
        return new ConditionResult(true, null);
    }

    public static ConditionResult notMatched(String message) {
        return new ConditionResult(false, message);
    }

    public static ConditionResult notFound(String what, String name) {
        return new ConditionResult(false, "Could not find " + what + ": " + name);
    }

    public static ConditionResult found(String what, String name, String value) {
        return new ConditionResult(false, "Found " + what + " with value " + name);
    }

    public static ConditionResult found(String what, String name) {
        return new ConditionResult(false, "Found " + what);
    }

    public static ConditionResult notEqual(String what, String expected, String actual) {
        return new ConditionResult(false, "Expected " + what + " to be " + expected + " but was " + actual);
    }

    public static ConditionResult invalidCondition(String conditionType) {
        return new ConditionResult(false, "Invalid " + conditionType + " condition");
    }
}
