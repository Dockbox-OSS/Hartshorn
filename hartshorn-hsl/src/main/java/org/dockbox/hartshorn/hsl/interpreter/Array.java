/*
 * Copyright 2019-2024 the original author or authors.
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
import org.dockbox.hartshorn.hsl.runtime.ExecutionOptions;
import org.dockbox.hartshorn.hsl.token.Token;

import java.util.Arrays;
import java.util.Iterator;

/**
 * Simple wrapper around an array of objects, without a specific type requirement. This is used
 * by the {@link SimpleVisitorInterpreter} to represent arrays in the HSL language, and access them natively.
 *
 * @since 0.4.12
 *
 * @author Guus Lieben
 */
public class Array implements Iterable<Object>, PropertyContainer {

    private final Object[] values;

    public Array(int length) {
        this.values = new Object[length];
    }

    public Array(Object[] values) {
        this.values = values;
    }

    /**
     * Sets the value at the given index.
     * @param value The value to set.
     * @param index The index to set the value at.
     * @throws IndexOutOfBoundsException If the index is out of bounds.
     */
    public void value(Object value, int index) {
        this.values[index] = value;
    }

    /**
     * Gets the value at the given index.
     * @param index The index to get the value from.
     * @return The value at the given index.
     * @throws IndexOutOfBoundsException If the index is out of bounds.
     */
    public Object value(int index) {
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
        return Arrays.asList(this.values).iterator();
    }

    @Override
    public void set(Token name, Object value, VariableScope fromScope, ExecutionOptions options) {
        throw new UnsupportedOperationException("Cannot set properties on arrays.");
    }

    @Override
    public Object get(Token name, VariableScope fromScope, ExecutionOptions interpreter) {
        if ("length".equals(name.lexeme())) {
            return this.values.length;
        } else {
            throw new UnsupportedOperationException("Cannot get properties on arrays, only 'length'.");
        }
    }
}
