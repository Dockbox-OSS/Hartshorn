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

package org.dockbox.hartshorn.hsl.runtime;

import org.dockbox.hartshorn.util.StringUtilities;

/**
 * An enumeration of all diagnostic messages used in HSL, each with a unique code and a message template.
 *
 * <p>The codes are structured as follows:
 * <ul>
 *     <li>1xxx - Lexical analysis (tokenizing)</li>
 *     <li>2xxx - Parsing</li>
 *     <li>3xxx - Resolution</li>
 *     <li>4xxx - Interpretation</li>
 *     <li>5xxx - Common validation</li>
 * </ul>
 *
 * <p>Each message can be formatted with arguments using the {@link #format(Object...)} method.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public enum DiagnosticMessage {
    // 1xxx - Lexical analysis
    UNEXPECTED_CHAR(Phase.TOKENIZING, "Unexpected character: {0}."),
    UNTERMINATED_STRING(Phase.TOKENIZING, "Unterminated string."),
    UNTERMINATED_CHAR(Phase.TOKENIZING, "Unterminated character."),
    UNEXPECTED_NULL(Phase.TOKENIZING, "Unexpected null character."),
    UNEXPECTED_DANGLING_NUMBER_SEPARATOR(Phase.TOKENIZING, "Unexpected dangling number separator."),
    UNEXPECTED_EOT(Phase.TOKENIZING, "Unexpected end of token. Expected any of: {0}."),
    INVALID_COMMENT_TOKEN_PAIR(Phase.TOKENIZING, "Invalid comment token pair for comment type {0}."),
    UNSUPPORTED_COMMENT_TYPE(Phase.TOKENIZING, "Unsupported comment type: {0}."),
    IDENTIFIER_STARTED_WITH_DIGIT(Phase.TOKENIZING, "Identifiers cannot start with a digit."),

    // 2xxx - Parsing
    UNSUPPORTED_STANDALONE_STATEMENT(Phase.PARSING, "Unsupported standalone statement type: {0}."),
    UNSUPPORTED_BODY_STATEMENT(Phase.PARSING, "Unsupported class body statement type: {0}."),
    UNSUPPORTED_FIELD_MEMBER(Phase.PARSING, "Unsupported field member type: {0}."),
    TOO_MANY_PARAMETERS(Phase.PARSING, "Cannot have more than {0} parameters."),
    TOO_MANY_PARAMETERS_FOR_X(Phase.PARSING, "Cannot have more than {0} parameters for {1} functions."),
    NON_LITERAL_CASE_EXPRESSION(Phase.PARSING, "Case expression must be a literal, but got {0}."),
    DUPLICATE_CASE_EXPRESSION(Phase.PARSING, "Duplicate case expression: {0}."),
    INVALID_ASSIGNMENT_TARGET(Phase.PARSING, "Invalid assignment target: {0}."),
    NO_PARSERS_FOR_X(Phase.PARSING, "No parsers found for {0}."),
    FAILED_TO_PARSE_X_STATEMENT(Phase.PARSING, "Failed to parse {0} statement."),
    EXPECTED_VARIABLE_STATEMENT_FOR_EACH(Phase.PARSING, "Expected variable statement in for-each loop."),
    MULTIPLE_DEFAULT_CASES(Phase.PARSING, "Multiple default cases are not allowed."),
    SWITCH_MUST_HAVE_CASE_OR_DEFAULT(Phase.PARSING, "Switch statement must have at least one case or a default case."),
    UNEXPECTED_TOKEN(Phase.PARSING, "Unexpected token '{0}'."),
    NOT_ENOUGH_PARAMETERS_FOR_X(Phase.PARSING, "Expected at least {0} parameters for {1} functions."),
    EMPTY_TEST_BODY(Phase.PARSING, "Test body cannot be empty."),
    TEST_BODY_MUST_END_WITH_YIELD(Phase.PARSING, "Test body must end with a yield return statement."),

    // 3xxx - Semantic analysis
    CANNOT_USE_X_OUTSIDE_CLASS(Phase.SEMANTIC_ANALYSIS, "Cannot use '{0}' outside of a class."),
    LOCAL_VAR_IN_INITIALIZER(Phase.SEMANTIC_ANALYSIS, "Cannot read local variable '{0}' in its own initializer."),
    TOP_LEVEL_RETURN(Phase.SEMANTIC_ANALYSIS, "Cannot return from top-level code."),
    TEST_BLOCK_RETURN(Phase.SEMANTIC_ANALYSIS, "Cannot return from a test statement."),
    INITIALIZER_RETURN(Phase.SEMANTIC_ANALYSIS, "Cannot return a value from an initializer."),
    CLASS_CANNOT_EXTEND_SELF(Phase.SEMANTIC_ANALYSIS, "Class {0} cannot extend itself."),
    CANNOT_USE_X_WITHOUT_SUPER_CLASS(Phase.SEMANTIC_ANALYSIS, "Cannot use '{0}' without a super class."),
    VARIABLE_ALREADY_DECLARED(Phase.SEMANTIC_ANALYSIS, "Variable with name '{0}' already declared in this scope."),
    INVALID_EXPRESSION(Phase.SEMANTIC_ANALYSIS, "Expected last statement to be a valid expression or return statement, but found {0}."),
    MISSING_MODULE(Phase.SEMANTIC_ANALYSIS, "Cannot find module named '{0}'."),
    X_CAN_ONLY_BE_USED_IN_LOOPS_AND_SWITCHES(Phase.SEMANTIC_ANALYSIS, "{0} can only be used in loops and switch case statements."),
    FIELD_MEMBER_RETURN(Phase.SEMANTIC_ANALYSIS, "Cannot return from a field member statement."),
    FUNCTION_CANNOT_YIELD(Phase.SEMANTIC_ANALYSIS, "Cannot yield from a function."),
    CONSTRUCTOR_OUTSIDE_CLASS(Phase.SEMANTIC_ANALYSIS, "Constructor cannot be declared outside of a class."),

    // 4xxx - Interpretation
    ILLEGAL_ZERO_DIVISION(Phase.INTERPRETING, "Division by zero: can't divide value with zero."),
    ILLEGAL_POSTFIX(Phase.INTERPRETING, "Illegal/invalid postfix operator: {0}."),
    ILLEGAL_BITWISE_OP(Phase.INTERPRETING, "Bitwise left and right must be a numbers, but got {0} ({1}) and {2} ({3})."),
    ILLEGAL_METHOD_BINDING_CALL(Phase.INTERPRETING, "Function reference was bound to {0}, but was invoked with a different object {1}."),
    ILLEGAL_EXTERNAL_FUNCTION_BINDING(Phase.INTERPRETING, "Cannot bind external function to virtual instance of type {0}."),
    ILLEGAL_GETTER_WITH_PARAMETERS(Phase.INTERPRETING, "Getter '{0}' has parameters, but can only be called without parameters."),
    ILLEGAL_SETTER_PARAMETER_MISMATCH(Phase.INTERPRETING, "Setter '{0}' has {1} parameters, but can only be called with one."),
    ILLEGAL_FINAL_SUPER_TYPE(Phase.INTERPRETING, "Cannot extend final class '{0}'."),
    ILLEGAL_NON_CLASS_SUPER(Phase.INTERPRETING, "Cannot extend non-class type '{0}'."),
    UNSUPPORTED_CHILD(Phase.INTERPRETING, "Unsupported child for {0}: {1} (left), {2} (right)."),
    UNSUPPORTED_LOGICAL(Phase.INTERPRETING, "Unsupported logical operator: {0}."),
    UNSUPPORTED_BITWISE(Phase.INTERPRETING, "Unsupported bitwise operator: {0}."),
    UNSUPPORTED_RETURN_TYPE(Phase.INTERPRETING, "Unsupported return type: {0}."),
    UNSUPPORTED_UNARY(Phase.INTERPRETING, "Unsupported unary operator: {0}."),
    CONSTRUCTOR_CALL_ON_INSTANCE(Phase.INTERPRETING, "Cannot call constructor on instance."),
    MISSING_CONSTRUCTOR_WITH_PARAMETERS(Phase.INTERPRETING, "No constructor found for class {0} with arguments {1}."),
    NON_ITERABLE_COLLECTION(Phase.INTERPRETING, "Collection must be iterable, but got {0}."),
    NON_PROPERTY_CONTAINER(Phase.INTERPRETING, "Can only access properties of property containers, but received {0}."),
    NON_NUMBER_OPERAND(Phase.INTERPRETING, "Operand must be a number, but got '{0}'."),
    NON_EXTERNAL_OBJECT_CALL(Phase.INTERPRETING, "Cannot call method '{0}' on non-external instance."),
    OPERAND_MISMATCH(Phase.INTERPRETING, "Operand mismatch, expected operands to be the same type ({0}), but got {1} and {2}."),
    DUPLICATE_X_DEFINITION(Phase.INTERPRETING, "Duplicate {0} definition: {1}.{2}."),
    MISSING_METHOD_WITH_PARAMETERS(Phase.INTERPRETING, "Method '{0}' with parameters accepting {1} does not exist on external instance of type {2}."),
    MISSING_ENCLOSING_SCOPE_AT_DIST(Phase.INTERPRETING, "No enclosing scope at distance {0} for active scope."),
    UNDEFINED_PROPERTY_ACCESSOR(Phase.INTERPRETING, "Could not register {0} for unknown property '{1}'."),
    INVALID_PROPERTY_ACCESS(Phase.INTERPRETING, "Cannot {0} property '{1}' of {2} because it is not accessible from the current scope. The property is declared {3} and has {4}."),
    AMBIGUOUS_FUNCTION_IN_MODULE(Phase.INTERPRETING, "Module '{0}' contains ambiguous function '{1}' which is already defined in the global scope."),
    UNEXPECTED_DEFAULT_CASE(Phase.INTERPRETING, "Unexpected default case in non-switch statement."),
    TEST_CONDITION_FAILED(Phase.INTERPRETING, "Test condition '{0}' failed with result: {1}."),
    NO_COMPATIBLE_LIBRARY_FUNCTION(Phase.INTERPRETING, "No compatible library function found for {0} arguments."),
    MULTIPLE_COMPATIBLE_LIBRARY_FUNCTIONS(Phase.INTERPRETING, "Multiple compatible library functions found for {0} arguments."),
    ILLEGAL_YIELD_IN_NON_GENERATOR(Phase.INTERPRETING, "Cannot yield from non-generator function."),
    ILLEGAL_RETURN_IN_GENERATOR(Phase.INTERPRETING, "Cannot return a value from generator function."),
    INCORRECT_INSTANCE_TYPE_FOR_FUNCTION(Phase.INTERPRETING, "Function '{0}' expected instance of type {1}, but got {2}."),
    DUPLICATE_EXTERNAL_CLASS_NAME(Phase.INTERPRETING, "An external class with name '{0}' is already registered."),
    ILLEGAL_NEGATIVE_NUMBER(Phase.INTERPRETING, "Expected a positive number (>=0), but got {0}."),
    NON_CALLABLE_CALLEE(Phase.INTERPRETING, "Callee is not callable, got {0}."),
    ERROR_WHILE_EVALUATING_X_EXPRESSION_WITH_OPERATOR(Phase.INTERPRETING, "Error while evaluating {0} expression with operator {1}: {2}"),
    UNSUPPORTED_MODULE_FUNCTION(Phase.INTERPRETING, "Function '{0}' is not supported by module '{1}'."),
    AMBIGUOUS_FUNCTION_CALL(Phase.INTERPRETING, "Ambiguous function call to '{0}'."),
    DEFERRED_INSTANCE_EAGER_ACCESS(Phase.INTERPRETING, "Cannot access deferred instance of type {0} before it has been initialized."),
    PROPERTY_ACCESS_FAILURE(Phase.INTERPRETING, "Failed to {0} property '{1}' on instance of type {2}: {3}"),
    ERROR_WHILE_INVOKING_NATIVE_METHOD(Phase.INTERPRETING, "Error while invoking native method '{0}': {1}"),

    // 5xxx - Common validation
    EXPECTED_EXPRESSION_AFTER_X("Expected expression after {0}."),
    EXPECTED_BLOCK_AFTER_X("Expected block after {0}."),
    EXPECTED_EXPRESSION("Expected expression, but found {0}."),
    EXPECTED_X_OR_Y("Expected {0} or {1}, but got {2}."),
    EXPECTED_X_OF_Y_AT_Z("Expected {0} {1}, but got {2}."),
    ILLEGAL_FINAL_X_REASSIGNMENT("Cannot reassign {0} '{1}' because it is final."),
    ILLEGAL_FINAL_X_OF_Y_REASSIGNMENT("Cannot reassign {0} '{1}' of {2} because it is final."),
    ILLEGAL_USE_OF_X("Illegal use of {0}. Expected valid keyword to follow, but got {1}."),
    UNDEFINED_PROPERTY("Property '{0}' is not defined on {1}."),
    UNDEFINED_VARIABLE("Variable '{0}' is not defined."),
    ;

    static {
        for (final DiagnosticMessage index : DiagnosticMessage.values()) {
            final String name = index.name();

            if (!index.message.endsWith(".")) {
                throw new IllegalStateException(name + ": Message does not end with a period.");
            }
        }
    }

    private final int id;
    private final int group;
    private final int member;
    private final String message;
    private final Phase phase;

    DiagnosticMessage(String message) {
        this.group = Phase.values().length + 1;
        this.member = MemberSequenceGenerator.nextMember(null);

        this.id = (this.group * 1000) + this.member;
        this.message = message;
        this.phase = null;
    }

    DiagnosticMessage(Phase phase, String message) {
        this.group = phase.ordinal() + 1;
        this.member = MemberSequenceGenerator.nextMember(phase);

        this.id = (this.group * 1000) + this.member;
        this.message = message;
        this.phase = phase;
    }

    /**
     * Gets the unique identifier for this diagnostic message.
     * @return the unique identifier
     */
    public int id() {
        return this.id;
    }

    /**
     * Gets the group of this diagnostic message. The group represents the category of the message (e.g., tokenizing, parsing,
     * etc.). The group is determined by the thousands place of the message ID. For example, {@link #UNEXPECTED_TOKEN} has ID 2014,
     * which means it belongs to group 2 (parsing).
     *
     * <p>The following groups are defined:
     * <ul>
     *     <li>1 - Lexical analysis (tokenizing)</li>
     *     <li>2 - Parsing</li>
     *     <li>3 - Resolution</li>
     *     <li>4 - Interpretation</li>
     *     <li>5 - Common validation</li>
     * </ul>
     *
     * @return the group
     */
    public int group() {
        return this.group;
    }

    /**
     * Gets the member number within its group for this diagnostic message. For example, {@link #UNEXPECTED_TOKEN} has
     * ID 2014, which means it belongs to group 2 (parsing) and is member 14 within that group.
     *
     * @return the member number
     */
    public int member() {
        return this.member;
    }

    /**
     * Gets the phase associated with this diagnostic message. This indicates the stage of processing (e.g.,
     * tokenizing, parsing, etc.) where the message is relevant. If no specific phase is associated, this method
     * returns {@code null}.
     *
     * @return the associated phase, or {@code null} if none is associated
     */
    public Phase phase() {
        return this.phase;
    }

    /**
     * Formats the diagnostic message with the given arguments. The message template may contain positional
     * placeholders (e.g., {0}, {1}, etc.) that will be replaced by the corresponding arguments.
     *
     * @param args the arguments to format the message with
     * @return the formatted message
     */
    public String format(Object... args) {
        return "HSL" + this.id + ": " + StringUtilities.format(this.message, args);
    }
}
