package org.dockbox.hartshorn.hsl.runtime;

import org.dockbox.hartshorn.hsl.token.TokenType;
import org.dockbox.hartshorn.util.StringUtilities;

public enum DiagnosticMessage {

    // SR-10XX: Lexer ungraceful exit
    UNEXPECTED_CHAR(1001, "Unexpected character: {0}."),
    UNTERMINATED_STRING(1002, "Unterminated string."),
    UNTERMINATED_CHAR(1003, "Unterminated character."),

    // SR-20XX: Parser token mismatch
    EXPECTED_N_X_BUT_GOT_Y(2001, "Expected{0} {1}, but got {2}."),
    EXPECTED_EXPRESSION_AFTER_X(2002, "Expected expression after {0}."),
    EXPECTED_EXPRESSION(2003, "Expected expression, but found {0}."),
    EXPECTED_X_OR_Y(2004, "Expected {0} or {1}, but got {2}."),
    EXPECTED_X(2005, "Expected {0}, but got {1}."),
    EXPECTED_X_AT_Z(2006, "Expected {0} {1} {2}, but got {3}."),

    // SR-30XX: Illegal operations
    ILLEGAL_ZERO_DIVISION(3001, "Division by zero: can't divide value with zero."),
    ILLEGAL_POSTFIX(3002, "Illegal/invalid postfix operator: {0}."),
    ILLEGAL_BITWISE_OP(3003, "Bitwise left and right must be a numbers, but got {0} ({1}) and {2} ({3})."),
    ILLEGAL_RETURN(3004, "Illegal return statement: {0}."),
    ILLEGAL_FIELD_DEFINITION(3005, "Illegal field definition: {0} in non-class."),
    ILLEGAL_METHOD_BINDING_CALL(3006, "Function reference was bound to {0}, but was invoked with a different object {1}"),
    ILLEGAL_EXTERNAL_FUNCTION_BINDING(3007, "Cannot bind external function to virtual instance of type {0}"),
    ILLEGAL_USE_OF_X(3008, "Illegal use of {0}. Expected valid keyword to follow, but got {1}."),
    ILLEGAL_GETTER_WITH_PARAMETERS(3009, "Getter {0} has parameters, but can only be called without parameters."),
    ILLEGAL_SETTER_PARAMETER_MISMATCH(3010, "Setter {0} has {1} parameters, but can only be called with one."),
    ILLEGAL_SETTER_RETURN(3011, "Setter for property {0} returned a value. Did you mean to use " + TokenType.YIELD.representation() + "?"),
    ILLEGAL_SETTER_NO_YIELD(3012, "Setter for property {0} did not yield a value to set."),
    ILLEGAL_FINAL_SUPER_TYPE(3013, "Cannot extend final class '{0}'."),
    ILLEGAL_FINAL_PROPERTY_REASSIGNMENT(3014, "Cannot reassign property {0} of {1} because it is final."),
    ILLEGAL_FINAL_X_REASSIGNMENT(3015, "Cannot reassign {0} {1} because it is final."),

    // SR-40XX: Unsupported operations
    UNSUPPORTED_CHILD(4001, "Unsupported child for {0}: {1}."),
    UNSUPPORTED_LOGICAL(4002, "Unsupported logical operator: {0}."),
    UNSUPPORTED_BITWISE(4003, "Unsupported bitwise operator: {0}."),
    UNSUPPORTED_NATIVE_FUNCTION(4004, "Native function {0} is not supported by module {1}."),
    UNSUPPORTED_STANDALONE_STATEMENT(4005, "Unsupported standalone statement type: {0}"),
    UNSUPPORTED_BODY_STATEMENT(4006, "Unsupported class body statement type: {0}"),
    UNSUPPORTED_FIELD_MEMBER(4007, "Unsupported field member type: {0}"),
    UNSUPPORTED_FIELD_MEMBER_BODY(4008, "Unsupported field member body type: {0}"),
    UNSUPPORTED_RETURN_TYPE(4009, "Unsupported return type: {0}"),

    // SR-50XX: Wrong token usage
    CONSTRUCTOR_CALL_ON_INSTANCE(5001, "Cannot call constructor on instance."),
    TOO_MANY_PARAMETERS(5002, "Cannot have more than {0} parameters."),
    TOO_MANY_PARAMETERS_FOR_X(5003, "Cannot have more than {0} parameters for {1} functions"),
    CANNOT_USE_X_OUTSIDE_CLASS(5004, "Cannot use '{0}' outside of a class."),
    LOCAL_VAR_IN_INITIALIZER(5005, "Cannot read local variable {0} in its own initializer."),
    X_ONLY_IN_LOOP_SWITCH(5006, "Cannot use {0} outside of a loop or switch."),
    X_ONLY_IN_LOOP(5007, "Cannot use {0} outside of a loop."),
    TOP_LEVEL_RETURN(5008, "Cannot return from top-level code."),
    INITIALIZER_RETURN(5009, "Cannot return a value from an initializer."),
    CLASS_CANNOT_EXTEND_SELF(5010, "Class {0} cannot extend itself."),
    CANNOT_USE_SUPER_WITHOUT_SUPER_CLASS(5011, "Cannot use '" + TokenType.SUPER.representation() + "' without a super class."),
    RETURN_ON_YIELD(5012, "Expected last statement to be yield a value, but got explicit return call."),

    // SR-60XX: Wrong value type
    NON_ITERABLE_COLLECTION(6001, "Collection must be iterable, but got {0}."),
    NON_CALLABLE_FUNCTION(6002, "Can only call callable nodes (functions, classes), but received {0}."),
    NON_PROPERTY_CONTAINER(6003, "Can only access properties of property containers, but received {0}."),
    NON_NUMBER_OPERAND(6004, "Operand must be a number, but got {0}."),
    NON_EXTERNAL_OBJECT_CALL(6005, "Cannot call method '{0}' on non-external instance."),
    NON_LITERAL_CASE_EXPRESSION(6006, "Case expression must be a literal, but got {0}."),
    OPERAND_MISMATCH(6007, "Operand mismatch, expected operands to be the same type ({0}), but got {1} and {2}."),

    // SR-70XX: Duplication errors
    DUPLICATE_METHOD(7001, "Duplicate method definition {0}.{1}."),
    DUPLICATE_CASE_EXPRESSION(7002, "Duplicate case expression: {0}."),
    VARIABLE_ALREADY_DECLARED(7003, "Variable with name '{0}' already declared in this scope."),
    DUPLICATE_FIELD(7004, "Duplicate field definition: {0}.{1}."),

    // SR-80XX: Invalid assignments
    INVALID_SUPER_TYPE(8001, "Super type must be a class, but got {0}."),
    INVALID_EXPRESSION(8002, "Expected last statement to be a valid expression or return statement, but found {0}"),
    INVALID_ASSIGNMENT_TARGET(8003, "Invalid assignment target: {0}"),

    // SR-90XX: Runtime failure
    MISSING_METHOD_WITH_COUNT(9001, "Method '{0}' with {1} parameters does not exist on external instance of type {2}."),
    MISSING_METHOD_WITH_PARAMETERS(9002, "Method '{0}' with parameters accepting {1} does not exist on external instance of type {2}."),
    MISSING_EXTENSION_CLASS(9003, "Can't find extension class {0}."),
    MISSING_MODULE(9004, "Cannot find module named '{0}'."),
    MISSING_ENCLOSING_SCOPE_AT_DIST(9005, "No enclosing scope at distance {0} for active scope."),
    UNDEFINED_PROPERTY(9006, "Property {0} is not defined on {1}."),
    UNDEFINED_VARIABLE(9007, "Variable {0} is not defined."),
    UNDEFINED_EXTERNAL_PROPERTY(9008, "Property {0} does not exist on external instance of type {1}"),
    UNDEFINED_PROPERTY_ACCESSOR(9009, "Could not register {0} for unknown property {1}."),
    INVALID_PROPERTY_ACCESS(9010, "Cannot {0} property {1} of {2} because it is not accessible from the current scope. The property is declared {3} and has {4}"),
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
        this.id = id;
        this.message = message;
        this.group = id / 1000;
        this.member = id % 1000;
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

    public static DiagnosticMessage fromId(final int id) {
        for (final DiagnosticMessage message : DiagnosticMessage.values()) {
            if (message.id == id) {
                return message;
            }
        }
        return null;
    }
}
