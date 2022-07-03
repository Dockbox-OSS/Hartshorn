package org.dockbox.hartshorn.hsl.interpreter;

import org.dockbox.hartshorn.util.Result;

/**
 * Collector to optionally save return values/results which have been calculated inside
 * an expression or script.
 *
 * @author Guus Lieben
 * @since 22.4
 */
public interface ResultCollector {

    /**
     * Adds a global result to the stack, if a global result already exists it will be
     * overwritten.
     * @param value The result value.
     */
    void addResult(Object value);

    /**
     * Adds a result to the stack under the given ID. If a result with the given ID
     * already exists it will be overwritten.
     * @param id The ID to use when saving the result.
     * @param value The result value.
     */
    void addResult(String id, Object value);

    /**
     * Gets the global result from the stack. If no global result exists {@link Result#empty()}
     * is returned.
     * @return The result value, or {@link Result#empty()}.
     * @param <T> The type of the result.
     */
    <T> Result<T> result();

    /**
     * Gets a result with the given ID. If no result with the given ID exists
     * {@link Result#empty()} is returned.
     * @param id The ID of the result.
     * @return The result value, or {@link Result#empty()}.
     * @param <T> The type of the result.
     */
    <T> Result<T> result(String id);

    /**
     * Removes all stored results from the collector instance. If no results exist
     * nothing happens.
     */
    void clear();
}
