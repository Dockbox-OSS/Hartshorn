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

package org.dockbox.hartshorn.hsl.runtime;

/**
 * Represents a specific return value, which immediately exits the current
 * scope, even if there are more statements to evaluate.
 *
 * @since 0.4.12
 *
 * @author Guus Lieben
 */
public class Return extends RuntimeException {

    private final Object value;

    public Return(Object value) {
        super(null, null, false, false);
        this.value = value;
    }

    /**
     * Gets the value of the return statement.
     * @return The value of the statement.
     */
    public Object value() {
        return this.value;
    }
}
