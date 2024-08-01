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

package org.dockbox.hartshorn.util;

/**
 * Represents a simple tristate, which is either {@code true}, {@code false}, or {@code undefined}
 * (indicating the value isn't present, either as a boolean or at all)
 *
 * @since 0.4.1
 *
 * @author Guus Lieben
 */
public enum Tristate {

    /**
     * Represents the boolean value {@code true}
     */
    TRUE(true),
    /**
     * Represents the boolean value {@code false}
     */
    FALSE(false),
    /**
     * Represents the absence of a boolean value
     */
    UNDEFINED(false);

    private final boolean booleanValue;

    Tristate(boolean booleanValue) {
        this.booleanValue = booleanValue;
    }

    /**
     * Returns the boolean value of this tristate. If the tristate is {@link #UNDEFINED}, {@code false} is returned.
     *
     * @return the boolean value of this tristate
     */
    public boolean booleanValue() {
        return this.booleanValue;
    }

    /**
     * Returns the tristate value of the provided boolean value.
     *
     * @param booleanValue the boolean value to convert
     * @return the tristate value of the provided boolean value
     */
    public static Tristate valueOf(boolean booleanValue) {
        return booleanValue ? TRUE : FALSE;
    }
}
