package org.dockbox.hartshorn.proxy.constraint;

import org.dockbox.hartshorn.util.introspect.view.TypeView;

import java.util.Set;

/**
 * A proxy validator is used to validate that a proxy can be created for a given type. This can use a set of
 * {@link ProxyConstraint constraints} to validate the type.
 */
public interface ProxyValidator {

    /**
     * Adds a constraint to the validator.
     * @param constraint the constraint to add
     */
    void add(final ProxyConstraint constraint);

    /**
     * Returns the constraints that are used to validate the type.
     * @return the constraints that are used to validate the type
     */
    Set<ProxyConstraint> constraints();

    /**
     * Validates the given type against the constraints. If the type is invalid for any reason, one or more
     * {@link ProxyConstraintViolation violations} should be returned. If the type is valid, an empty set
     * should be returned.
     *
     * @param type the type to validate
     * @return one or more violations if the type is invalid, an empty set otherwise
     */
    Set<ProxyConstraintViolation> validate(final TypeView<?> type);
}
