package org.dockbox.hartshorn.hsl.interpreter;

import java.util.Arrays;

/**
 * Simple wrapper around an array of objects, without a specific type requirement. This is used
 * by the {@link Interpreter} to represent arrays in the HSL language, and access them natively.
 *
 * @author Guus Lieben
 * @since 22.4
 */
public class Array {

    private final Object[] values;

    public Array(final int length) {
        this.values = new Object[length];
    }

    public Array(final Object[] values) {
        this.values = values;
    }

    /**
     * Sets the value at the given index.
     * @param value The value to set.
     * @param index The index to set the value at.
     * @throws IndexOutOfBoundsException If the index is out of bounds.
     */
    public void value(final Object value, final int index) {
        this.values[index] = value;
    }

    /**
     * Gets the value at the given index.
     * @param index The index to get the value from.
     * @return The value at the given index.
     * @throws IndexOutOfBoundsException If the index is out of bounds.
     */
    public Object value(final int index) {
        return this.values[index];
    }

    /**
     * Gets the array represented by this instance.
     * @return The array represented by this instance.
     */
    public Object[] values() {
        return this.values;
    }

    /**
     * Gets the length of the array.
     * @return The length of the array.
     */
    public int length() {
        return this.values.length;
    }

    @Override
    public String toString() {
        return Arrays.toString(this.values);
    }
}
