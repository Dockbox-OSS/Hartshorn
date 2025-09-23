package org.dockbox.hartshorn.hsl.runtime;

import org.dockbox.hartshorn.hsl.token.TokenType;
import org.dockbox.hartshorn.util.StringUtilities;

public enum DiagnosticMessage {
    UNEXPECTED_CHAR(-1, "Unexpected character: {0}."),
    UNTERMINATED_STRING(-1, "Unterminated string."),
    UNTERMINATED_CHAR(-1, "Unterminated character."),
    UNEXPECTED_NULL(-1, "Unexpected null character."),
    UNEXPECTED_DANGLING_NUMBER_SEPARATOR(-1, "Unexpected dangling number separator."),
    UNEXPECTED_EOT(-1, "Unexpected end of token. Expected any of: {0}."),
    INVALID_COMMENT_TOKEN_PAIR(-1, "Invalid comment token pair for comment type {0}."),
    UNSUPPORTED_COMMENT_TYPE(-1, "Unsupported comment type: {0}."),
    IDENTIFIER_STARTED_WITH_DIGIT(-1, "Identifiers cannot start with a digit."),
    EXPECTED_N_X_BUT_GOT_Y(-1, "Expected{0} {1}, but got {2}."),
    EXPECTED_EXPRESSION_AFTER_X(-1, "Expected expression after {0}."),
    EXPECTED_EXPRESSION(-1, "Expected expression, but found {0}."),
    EXPECTED_X_OR_Y(-1, "Expected {0} or {1}, but got {2}."),
    EXPECTED_X(-1, "Expected {0}, but got {1}."),
    EXPECTED_X_AT_Z(-1, "Expected {0} {1} {2}, but got {3}."),
    ILLEGAL_ZERO_DIVISION(-1, "Division by zero: can't divide value with zero."),
    ILLEGAL_POSTFIX(-1, "Illegal/invalid postfix operator: {0}."),
    ILLEGAL_BITWISE_OP(-1, "Bitwise left and right must be a numbers, but got {0} ({1}) and {2} ({3})."),
    ILLEGAL_RETURN(-1, "Illegal return statement: {0}."),
    ILLEGAL_FIELD_DEFINITION(-1, "Illegal field definition: {0} in non-class."),
    ILLEGAL_METHOD_BINDING_CALL(-1, "Function reference was bound to {0}, but was invoked with a different object {1}"),
    ILLEGAL_EXTERNAL_FUNCTION_BINDING(-1, "Cannot bind external function to virtual instance of type {0}"),
    ILLEGAL_USE_OF_X(-1, "Illegal use of {0}. Expected valid keyword to follow, but got {1}."),
    ILLEGAL_GETTER_WITH_PARAMETERS(-1, "Getter {0} has parameters, but can only be called without parameters."),
    ILLEGAL_SETTER_PARAMETER_MISMATCH(-1, "Setter {0} has {1} parameters, but can only be called with one."),
    ILLEGAL_SETTER_RETURN(-1, "Setter for property {0} returned a value. Did you mean to use " + TokenType.YIELD.representation() + "?"),
    ILLEGAL_SETTER_NO_YIELD(-1, "Setter for property {0} did not yield a value to set."),
    ILLEGAL_FINAL_SUPER_TYPE(-1, "Cannot extend final class '{0}'."),
    ILLEGAL_FINAL_PROPERTY_REASSIGNMENT(-1, "Cannot reassign property {0} of {1} because it is final."),
    ILLEGAL_FINAL_X_REASSIGNMENT(-1, "Cannot reassign {0} {1} because it is final."),
    ILLEGAL_NON_CLASS_SUPER(-1, "Cannot extend non-class type '{0}'."),
    UNSUPPORTED_CHILD(-1, "Unsupported child for {0}: {1}."),
    UNSUPPORTED_LOGICAL(-1, "Unsupported logical operator: {0}."),
    UNSUPPORTED_BITWISE(-1, "Unsupported bitwise operator: {0}."),
    UNSUPPORTED_NATIVE_FUNCTION(-1, "Native function {0} is not supported by module {1}."),
    UNSUPPORTED_STANDALONE_STATEMENT(-1, "Unsupported standalone statement type: {0}"),
    UNSUPPORTED_BODY_STATEMENT(-1, "Unsupported class body statement type: {0}"),
    UNSUPPORTED_FIELD_MEMBER(-1, "Unsupported field member type: {0}"),
    UNSUPPORTED_FIELD_MEMBER_BODY(-1, "Unsupported field member body type: {0}"),
    UNSUPPORTED_RETURN_TYPE(-1, "Unsupported return type: {0}"),
    UNSUPPORTED_UNARY(-1, "Unsupported unary operator: {0}"),
    CONSTRUCTOR_CALL_ON_INSTANCE(-1, "Cannot call constructor on instance."),
    MISSING_CONSTRUCTOR_WITH_PARAMETERS(-1, "No constructor found for class {0} with arguments {1}."),
    TOO_MANY_PARAMETERS(-1, "Cannot have more than {0} parameters."),
    TOO_MANY_PARAMETERS_FOR_X(-1, "Cannot have more than {0} parameters for {1} functions"),
    CANNOT_USE_X_OUTSIDE_CLASS(-1, "Cannot use '{0}' outside of a class."),
    LOCAL_VAR_IN_INITIALIZER(-1, "Cannot read local variable {0} in its own initializer."),
    X_ONLY_IN_LOOP_SWITCH(-1, "Cannot use {0} outside of a loop or switch."),
    X_ONLY_IN_LOOP(-1, "Cannot use {0} outside of a loop."),
    TOP_LEVEL_RETURN(-1, "Cannot return from top-level code."),
    INITIALIZER_RETURN(-1, "Cannot return a value from an initializer."),
    CLASS_CANNOT_EXTEND_SELF(-1, "Class {0} cannot extend itself."),
    CANNOT_USE_SUPER_WITHOUT_SUPER_CLASS(-1, "Cannot use '" + TokenType.SUPER.representation() + "' without a super class."),
    RETURN_ON_YIELD(-1, "Expected last statement to be yield a value, but got explicit return call."),
    NON_ITERABLE_COLLECTION(-1, "Collection must be iterable, but got {0}."),
    NON_CALLABLE_FUNCTION(-1, "Can only call callable nodes (functions, classes), but received {0}."),
    NON_PROPERTY_CONTAINER(-1, "Can only access properties of property containers, but received {0}."),
    NON_NUMBER_OPERAND(-1, "Operand must be a number, but got {0}."),
    NON_EXTERNAL_OBJECT_CALL(-1, "Cannot call method '{0}' on non-external instance."),
    NON_LITERAL_CASE_EXPRESSION(-1, "Case expression must be a literal, but got {0}."),
    OPERAND_MISMATCH(-1, "Operand mismatch, expected operands to be the same type ({0}), but got {1} and {2}."),
    DUPLICATE_METHOD(-1, "Duplicate method definition {0}.{1}."),
    DUPLICATE_CASE_EXPRESSION(-1, "Duplicate case expression: {0}."),
    VARIABLE_ALREADY_DECLARED(-1, "Variable with name '{0}' already declared in this scope."),
    DUPLICATE_FIELD(-1, "Duplicate field definition: {0}.{1}."),
    INVALID_SUPER_TYPE(-1, "Super type must be a class, but got {0}."),
    INVALID_EXPRESSION(-1, "Expected last statement to be a valid expression or return statement, but found {0}"),
    INVALID_ASSIGNMENT_TARGET(-1, "Invalid assignment target: {0}"),
    MISSING_METHOD_WITH_PARAMETERS(-1, "Method '{0}' with parameters accepting {1} does not exist on external instance of type {2}."),
    MISSING_EXTENSION_CLASS(-1, "Can't find extension class {0}."),
    MISSING_MODULE(-1, "Cannot find module named '{0}'."),
    MISSING_ENCLOSING_SCOPE_AT_DIST(-1, "No enclosing scope at distance {0} for active scope."),
    UNDEFINED_PROPERTY(-1, "Property {0} is not defined on {1}."),
    UNDEFINED_VARIABLE(-1, "Variable {0} is not defined."),
    UNDEFINED_EXTERNAL_PROPERTY(-1, "Property {0} does not exist on external instance of type {1}"),
    UNDEFINED_PROPERTY_ACCESSOR(-1, "Could not register {0} for unknown property {1}."),
    INVALID_PROPERTY_ACCESS(-1, "Cannot {0} property {1} of {2} because it is not accessible from the current scope. The property is declared {3} and has {4}"),
    AMBIGUOUS_FUNCTION_IN_MODULE(-1, "Module '{0}' contains ambiguous function '{1}' which is already defined in the global scope."),
    UNEXPECTED_DEFAULT_CASE(-1, "Unexpected default case in non-switch statement."),
    TEST_CONDITION_FAILED(-1, "Test condition '{0}' failed with result: {1}"),
    NO_COMPATIBLE_LIBRARY_FUNCTION(-1, "No compatible library function found for {0} arguments."),
    MULTIPLE_COMPATIBLE_LIBRARY_FUNCTIONS(-1, "Multiple compatible library functions found for {0} arguments."),
    ;

    /*
     * Verify that all diagnostic messages have unique codes, and that the codes are in the correct order.
     */
    static {
        int previousGroup = 0;
        int previousMember = 0;

        for (final DiagnosticMessage index : DiagnosticMessage.values()) {
            final String name = index.name();

            // Verify that the groups are sequential
            final int group = index.group();
            if (group < previousGroup) {
                throw new IllegalStateException(name + ": Group " + group + " is less than previous group " + previousGroup);
            }
            if (group > previousGroup) {
                if (group != previousGroup + 1) {
                    throw new IllegalStateException(name + ": Group " + group + " is not consecutive with previous group " + previousGroup);
                }
                previousMember = 0;
            }
            previousGroup = group;

            // Verify that the member is consecutive with the previous member.
            final int member = index.member();
            if (member < previousMember) {
                throw new IllegalStateException(name + ": Member " + member + " is less than previous member " + previousMember);
            }
            if (member != previousMember + 1) {
                throw new IllegalStateException(name + ": Member " + member + " is not consecutive with previous member " + previousMember);
            }
            previousMember = member;
        }
    }

    private final int id;
    private final int group;
    private final int member;
    private final String message;

    DiagnosticMessage(final int id, final String message) {
        // TODO: Provide proper IDs, grouping relevant messages together
        this.id = ordinal() * 1000;
        this.message = message;
        this.group = this.id / 1000;
        this.member = this.id % 1000;
    }

    public int id() {
        return id;
    }

    public int group() {
        return group;
    }

    public int member() {
        return member;
    }

    public String format(Object... args) {
        return "HSL" + this.id + ": " + StringUtilities.format(message, args);
    }
}
