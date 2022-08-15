/*
 * Copyright 2019-2022 the original author or authors.
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

package org.dockbox.hartshorn.hsl.interpreter;

import org.dockbox.hartshorn.hsl.objects.PropertyContainer;
import org.dockbox.hartshorn.hsl.token.Token;

import java.util.Arrays;
import java.util.Iterator;

/**
 * Simple wrapper around an array of objects, without a specific type requirement. This is used
 * by the {@link Interpreter} to represent arrays in the HSL language, and access them natively.
 *
 * @author Guus Lieben
 * @since 22.4
 */
public class Array implements Iterable<Object>, PropertyContainer {

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

    @Override
    public Iterator<Object> iterator() {
        return Arrays.asList(values).iterator();
    }

    @Override
    public void set(final Token name, final Object value, VariableScope fromScope) {
        throw new UnsupportedOperationException("Cannot set properties on arrays.");
    }

    @Override
    public Object get(final Token name, VariableScope fromScope) {
        if (name.lexeme().equals("length")) {
            return this.values.length;
        } else {
            throw new UnsupportedOperationException("Cannot get properties on arrays, only 'length'.");
        }
    }
}
