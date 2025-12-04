package org.dockbox.hartshorn.hsl.semantic;

import org.dockbox.hartshorn.hsl.ast.statement.ClassStatement;
import org.dockbox.hartshorn.hsl.ast.statement.ConstructorStatement;
import org.dockbox.hartshorn.hsl.ast.statement.FunctionStatement;
import org.dockbox.hartshorn.hsl.ast.statement.TestStatement;

/**
 * The type of the function that is currently being resolved. This is used to determine
 * whether certain operations are allowed or not.
 *
 * @author Guus Lieben
 * @since 0.4.12
 */
public enum FunctionType {
    /**
     * No function type, indicating that we are resolving outside the scope of any function.
     */
    NONE,
    /**
     * An inline function, which is a function that is not attached to a class (method). Usually
     * linked only to {@link FunctionStatement function statements}.
     */
    INLINE_FUNCTION,
    /**
     * A function that is attached to a class. Usually linked to {@link FunctionStatement functions}
     * encountered while visiting a {@link ClassStatement}.
     */
    CLASS_FUNCTION,
    /**
     * A constructor, which is a method that is used to create a new instance of a class. Usually
     * linked to {@link ConstructorStatement constructor} encountered while visiting a
     * {@link ClassStatement}.
     */
    INITIALIZER,
    /**
     * A test function, which is an inline function-like assertion. Usually linked to
     * {@link TestStatement test statements}.
     */
    TEST,
    /**
     * A field member, which is a function-like expression used to assign get/set statements to
     * field in a class. Usually linked to {@link ClassStatement class statements} that have field
     * members.
     */
    FIELD_MEMBER,
}
