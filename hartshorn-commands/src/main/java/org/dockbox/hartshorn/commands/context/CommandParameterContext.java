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

package org.dockbox.hartshorn.commands.context;

import org.dockbox.hartshorn.core.context.element.ParameterContext;

/**
 * Type used to store a {@link java.lang.reflect.Method}'s parameter and the index of said
 * parameter.
 */
public class CommandParameterContext {

    private final ParameterContext<?> parameter;
    private final int index;

    public CommandParameterContext(final ParameterContext<?> parameter, final int index) {
        this.parameter = parameter;
        this.index = index;
    }

    public ParameterContext<?> parameter() {
        return this.parameter;
    }

    public int index() {
        return this.index;
    }

    /**
     * Checks if the provided type is equal to, or a supertype of, the stored parameter's type.
     *
     * @param type The type to compare against
     *
     * @return <code>true</code> if the provided type is equal or a supertype, else <code>false</code>
     */
    public boolean is(final Class<?> type) {
        return this.parameter().type().childOf(type);
    }

}
