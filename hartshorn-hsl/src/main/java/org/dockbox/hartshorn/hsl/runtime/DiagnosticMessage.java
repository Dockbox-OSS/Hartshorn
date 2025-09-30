package org.dockbox.hartshorn.hsl.runtime;

import org.dockbox.hartshorn.util.StringUtilities;

public enum DiagnosticMessage {
    // 1xxx - Lexical analysis
    UNEXPECTED_CHAR(1001, Phase.TOKENIZING, "Unexpected character: {0}."),
    UNTERMINATED_STRING(1002, Phase.TOKENIZING, "Unterminated string."),
    UNTERMINATED_CHAR(1003, Phase.TOKENIZING, "Unterminated character."),
    UNEXPECTED_NULL(1004, Phase.TOKENIZING, "Unexpected null character."),
    UNEXPECTED_DANGLING_NUMBER_SEPARATOR(1005, Phase.TOKENIZING, "Unexpected dangling number separator."),
    UNEXPECTED_EOT(1006, Phase.TOKENIZING, "Unexpected end of token. Expected any of: {0}."),
    INVALID_COMMENT_TOKEN_PAIR(1007, Phase.TOKENIZING, "Invalid comment token pair for comment type {0}."),
    UNSUPPORTED_COMMENT_TYPE(1008, Phase.TOKENIZING, "Unsupported comment type: {0}."),
    IDENTIFIER_STARTED_WITH_DIGIT(1009, Phase.TOKENIZING, "Identifiers cannot start with a digit."),

    // 2xxx - Parsing
    UNSUPPORTED_STANDALONE_STATEMENT(2001, Phase.PARSING, "Unsupported standalone statement type: {0}"),
    UNSUPPORTED_BODY_STATEMENT(2002, Phase.PARSING, "Unsupported class body statement type: {0}"),
    UNSUPPORTED_FIELD_MEMBER(2003, Phase.PARSING, "Unsupported field member type: {0}"),
    TOO_MANY_PARAMETERS(2004, Phase.PARSING, "Cannot have more than {0} parameters."),
    TOO_MANY_PARAMETERS_FOR_X(2005, Phase.PARSING, "Cannot have more than {0} parameters for {1} functions"),
    NON_LITERAL_CASE_EXPRESSION(2006, Phase.PARSING, "Case expression must be a literal, but got {0}."),
    DUPLICATE_CASE_EXPRESSION(2007, Phase.PARSING, "Duplicate case expression: {0}."),
    INVALID_ASSIGNMENT_TARGET(2008, Phase.PARSING, "Invalid assignment target: {0}"),
    NO_PARSERS_FOR_X(2009, Phase.PARSING, "No parsers found for {0}."),
    FAILED_TO_PARSE_X_STATEMENT(2010, Phase.PARSING, "Failed to parse {0} statement."),
    EXPECTED_VARIABLE_STATEMENT_FOR_EACH(2011, Phase.PARSING, "Expected variable statement in for-each loop."),
    MULTIPLE_DEFAULT_CASES(2012, Phase.PARSING, "Multiple default cases are not allowed."),
    SWITCH_MUST_HAVE_CASE_OR_DEFAULT(2013, Phase.PARSING, "Switch statement must have at least one case or a default case."),
    UNEXPECTED_TOKEN(2014, Phase.PARSING, "Unexpected token '{0}'"),

    // 3xxx - Resolution
    CANNOT_USE_X_OUTSIDE_CLASS(3001, Phase.RESOLVING, "Cannot use '{0}' outside of a class."),
    LOCAL_VAR_IN_INITIALIZER(3002, Phase.RESOLVING, "Cannot read local variable '{0}' in its own initializer."),
    TOP_LEVEL_RETURN(3003, Phase.RESOLVING, "Cannot return from top-level code."),
    TEST_BLOCK_RETURN(3004, Phase.RESOLVING, "Cannot return from a test statement."),
    INITIALIZER_RETURN(3005, Phase.RESOLVING, "Cannot return a value from an initializer."),
    CLASS_CANNOT_EXTEND_SELF(3006, Phase.RESOLVING, "Class {0} cannot extend itself."),
    CANNOT_USE_X_WITHOUT_SUPER_CLASS(3007, Phase.RESOLVING, "Cannot use '{0}' without a super class."),
    VARIABLE_ALREADY_DECLARED(3008, Phase.RESOLVING, "Variable with name '{0}' already declared in this scope."),
    INVALID_EXPRESSION(3009, Phase.RESOLVING, "Expected last statement to be a valid expression or return statement, but found {0}"),
    MISSING_MODULE(3010, Phase.RESOLVING, "Cannot find module named '{0}'."),
    X_CAN_ONLY_BE_USED_IN_LOOPS_AND_SWITCHES(3011, Phase.RESOLVING, "{0} can only be used in loops and switch case statements."),

    // 4xxx - Interpretation
    ILLEGAL_ZERO_DIVISION(4001, Phase.INTERPRETING, "Division by zero: can't divide value with zero."),
    ILLEGAL_POSTFIX(4002, Phase.INTERPRETING, "Illegal/invalid postfix operator: {0}."),
    ILLEGAL_BITWISE_OP(4003, Phase.INTERPRETING, "Bitwise left and right must be a numbers, but got {0} ({1}) and {2} ({3})."),
    ILLEGAL_METHOD_BINDING_CALL(4004, Phase.INTERPRETING, "Function reference was bound to {0}, but was invoked with a different object {1}"),
    ILLEGAL_EXTERNAL_FUNCTION_BINDING(4005, Phase.INTERPRETING, "Cannot bind external function to virtual instance of type {0}"),
    ILLEGAL_GETTER_WITH_PARAMETERS(4006, Phase.INTERPRETING, "Getter '{0}' has parameters, but can only be called without parameters."),
    ILLEGAL_SETTER_PARAMETER_MISMATCH(4007, Phase.INTERPRETING, "Setter '{0}' has {1} parameters, but can only be called with one."),
    ILLEGAL_FINAL_SUPER_TYPE(4008, Phase.INTERPRETING, "Cannot extend final class '{0}'."),
    ILLEGAL_NON_CLASS_SUPER(4009, Phase.INTERPRETING, "Cannot extend non-class type '{0}'."),
    UNSUPPORTED_CHILD(4010, Phase.INTERPRETING, "Unsupported child for {0}: {1} (left), {2} (right)."),
    UNSUPPORTED_LOGICAL(4011, Phase.INTERPRETING, "Unsupported logical operator: {0}."),
    UNSUPPORTED_BITWISE(4012, Phase.INTERPRETING, "Unsupported bitwise operator: {0}."),
    UNSUPPORTED_RETURN_TYPE(4013, Phase.INTERPRETING, "Unsupported return type: {0}"),
    UNSUPPORTED_UNARY(4014, Phase.INTERPRETING, "Unsupported unary operator: {0}"),
    CONSTRUCTOR_CALL_ON_INSTANCE(4015, Phase.INTERPRETING, "Cannot call constructor on instance."),
    MISSING_CONSTRUCTOR_WITH_PARAMETERS(4016, Phase.INTERPRETING, "No constructor found for class {0} with arguments {1}."),
    NON_ITERABLE_COLLECTION(4017, Phase.INTERPRETING, "Collection must be iterable, but got {0}."),
    NON_PROPERTY_CONTAINER(4018, Phase.INTERPRETING, "Can only access properties of property containers, but received {0}."),
    NON_NUMBER_OPERAND(4019, Phase.INTERPRETING, "Operand must be a number, but got {0}."),
    NON_EXTERNAL_OBJECT_CALL(4020, Phase.INTERPRETING, "Cannot call method '{0}' on non-external instance."),
    OPERAND_MISMATCH(4021, Phase.INTERPRETING, "Operand mismatch, expected operands to be the same type ({0}), but got {1} and {2}."),
    DUPLICATE_X_DEFINITION(4022, Phase.INTERPRETING, "Duplicate {0} definition: {1}.{2}."),
    MISSING_METHOD_WITH_PARAMETERS(4023, Phase.INTERPRETING, "Method '{0}' with parameters accepting {1} does not exist on external instance of type {2}."),
    MISSING_ENCLOSING_SCOPE_AT_DIST(4024, Phase.INTERPRETING, "No enclosing scope at distance {0} for active scope."),
    UNDEFINED_PROPERTY_ACCESSOR(4025, Phase.INTERPRETING, "Could not register {0} for unknown property '{1}'."),
    INVALID_PROPERTY_ACCESS(4026, Phase.INTERPRETING, "Cannot {0} property '{1}' of {2} because it is not accessible from the current scope. The property is declared {3} and has {4}"),
    AMBIGUOUS_FUNCTION_IN_MODULE(4027, Phase.INTERPRETING, "Module '{0}' contains ambiguous function '{1}' which is already defined in the global scope."),
    UNEXPECTED_DEFAULT_CASE(4028, Phase.INTERPRETING, "Unexpected default case in non-switch statement."),
    TEST_CONDITION_FAILED(4029, Phase.INTERPRETING, "Test condition '{0}' failed with result: {1}"),
    NO_COMPATIBLE_LIBRARY_FUNCTION(4030, Phase.INTERPRETING, "No compatible library function found for {0} arguments."),
    MULTIPLE_COMPATIBLE_LIBRARY_FUNCTIONS(4031, Phase.INTERPRETING, "Multiple compatible library functions found for {0} arguments."),
    ILLEGAL_YIELD_IN_NON_GENERATOR(4032, Phase.INTERPRETING, "Cannot yield from non-generator function."),
    ILLEGAL_RETURN_IN_GENERATOR(4033, Phase.INTERPRETING, "Cannot return a value from generator function."),
    INCORRECT_INSTANCE_TYPE_FOR_FUNCTION(4034, Phase.INTERPRETING, "Function '{0}' expected instance of type {1}, but got {2}."),

    // 5xxx - Common validation
    EXPECTED_EXPRESSION_AFTER_X(5001, "Expected expression after {0}."),
    EXPECTED_BLOCK_AFTER_X(5002, "Expected block after {0}."),
    EXPECTED_EXPRESSION(5003, "Expected expression, but found {0}."),
    EXPECTED_X_OR_Y(5004, "Expected {0} or {1}, but got {2}."),
    EXPECTED_X_OF_Y_AT_Z(5005, "Expected {0} {1}, but got {2}."),
    ILLEGAL_FINAL_X_REASSIGNMENT(5006, "Cannot reassign {0} '{1}' because it is final."),
    ILLEGAL_USE_OF_X(5007, "Illegal use of {0}. Expected valid keyword to follow, but got {1}."),
    UNDEFINED_PROPERTY(5008, "Property '{0}' is not defined on {1}."),
    UNDEFINED_VARIABLE(5009, "Variable '{0}' is not defined."),
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
                throw new IllegalStateException(name + ": Member " + member + " (" + index.id() + ") is not consecutive with previous member " + previousMember + " (" + (index.id() - (member - previousMember)) + ")");
            }
            previousMember = member;
        }
    }

    private final int id;
    private final int group;
    private final int member;
    private final String message;
    private final Phase phase;

    DiagnosticMessage(int id, String message) {
        this(id, null, message);
    }

    DiagnosticMessage(int id, Phase phase, String message) {
        this.id = id;
        this.message = message;
        this.group = this.id / 1000;
        this.member = this.id % 1000;
        this.phase = phase;
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

    public Phase phase() {
        return phase;
    }

    public String format(Object... args) {
        return "HSL" + this.id + ": " + StringUtilities.format(message, args);
    }
}
