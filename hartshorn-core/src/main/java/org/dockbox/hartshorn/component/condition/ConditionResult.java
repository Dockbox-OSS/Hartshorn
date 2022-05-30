package org.dockbox.hartshorn.component.condition;

public final class ConditionResult {

    private final boolean matches;
    private final String message;

    private ConditionResult(final boolean matches, final String message) {
        this.matches = matches;
        this.message = message;
    }

    public boolean matches() {
        return this.matches;
    }

    public String message() {
        return this.message;
    }

    public static ConditionResult matched() {
        return new ConditionResult(true, null);
    }

    public static ConditionResult notMatched(final String message) {
        return new ConditionResult(false, message);
    }

    public static ConditionResult notFound(final String what, final String name) {
        return new ConditionResult(false, "Could not find " + what + ": " + name);
    }

    public static ConditionResult found(final String what, final String name, final String value) {
        return new ConditionResult(false, "Found " + what + " with value " + name);
    }

    public static ConditionResult found(final String what, final String name) {
        return new ConditionResult(false, "Found " + what);
    }

    public static ConditionResult notEqual(final String what, final String expected, final String actual) {
        return new ConditionResult(false, "Expected " + what + " to be " + expected + " but was " + actual);
    }

    public static ConditionResult invalidCondition(final String conditionType) {
        return new ConditionResult(false, "Invalid " + conditionType + " condition");
    }
}
