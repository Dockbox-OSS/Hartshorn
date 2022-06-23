package org.dockbox.hartshorn.hsl.interpreter;

import java.util.Arrays;

public class Array {

    private final Object[] values;
    private final int length;

    public Array(final int length) {
        this.length = length;
        this.values = new Object[length];
    }

    public Array(final Object[] values) {
        this.values = values;
        this.length = values.length;
    }

    public void value(final Object value, final int index) {
        this.values[index] = value;
    }

    public Object value(final int index) {
        return this.values[index];
    }

    public Object[] values() {
        return this.values;
    }

    public int length() {
        return this.length;
    }

    @Override
    public String toString() {
        return Arrays.toString(this.values);
    }
}
