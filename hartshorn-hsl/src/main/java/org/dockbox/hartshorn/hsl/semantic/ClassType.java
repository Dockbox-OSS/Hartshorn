package org.dockbox.hartshorn.hsl.semantic;

import org.dockbox.hartshorn.hsl.ast.statement.ClassStatement;

/**
 * The type of the class that is currently being resolved. This is used to determine
 * whether certain operations are allowed or not.
 *
 * @author Guus Lieben
 * @since 0.4.12
 */
public enum ClassType {
    /**
     * No class type, indicating that we are resolving outside the scope of any class.
     */
    NONE,
    /**
     * A single class, which is a class that does not extend another class.
     */
    CLASS,
    /**
     * A subclass, which is a class that extends another class, indicated by the presence
     * of a {@link ClassStatement#superClass() superclass expression}.
     */
    SUBCLASS,
}
