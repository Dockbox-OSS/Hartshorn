package org.dockbox.hartshorn.proxy.constraint;

import org.dockbox.hartshorn.util.introspect.view.TypeView;

import java.util.Set;

/**
 * A proxy constraint is a validation rule that can be applied to a proxy. This can be used to validate
 * that a proxy can be created for a given type.
 */
public interface ProxyConstraint {

    /**
     * Validates the given type view against the constraint. If the constraint is not met, one or more
     * {@link ProxyConstraintViolation violations} should be returned. If the constraint is met, an
     * empty set should be returned.
     *
     * @param typeView the type view to validate
     * @return an error message if the constraint is not met, {@code null} otherwise
     */
    Set<ProxyConstraintViolation> validate(final TypeView<?> typeView);
}
