package org.dockbox.hartshorn.proxy.constraint;

import org.dockbox.hartshorn.util.ApplicationException;

import java.util.Set;
import java.util.stream.Collectors;

public class ProxyConstraintViolationException extends ApplicationException {

    public ProxyConstraintViolationException(final String message) {
        super(message);
    }

    public ProxyConstraintViolationException(final Set<ProxyConstraintViolation> violations) {
        this(violations.stream()
                .map(ProxyConstraintViolation::message)
                .collect(Collectors.joining("\n"))
        );
    }
}
