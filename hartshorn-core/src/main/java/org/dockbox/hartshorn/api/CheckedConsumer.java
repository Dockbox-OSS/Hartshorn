package org.dockbox.hartshorn.api;

import org.dockbox.hartshorn.api.exceptions.ApplicationException;

/**
 * Represents an operation that accepts a single input argument and returns no
 * result. Unlike most other functional interfaces, {@code CheckedConsumer} is
 * expected to operate via side-effects.
 *
 * @param <T> the type of the input to the operation
 */
public interface CheckedConsumer<T> {
    void accept(T t) throws ApplicationException;
}
