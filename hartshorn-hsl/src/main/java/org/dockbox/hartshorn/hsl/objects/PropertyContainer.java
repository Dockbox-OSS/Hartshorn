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

package org.dockbox.hartshorn.hsl.objects;

import org.dockbox.hartshorn.hsl.ScriptEvaluationError;
import org.dockbox.hartshorn.hsl.interpreter.VariableScope;
import org.dockbox.hartshorn.hsl.runtime.ExecutionOptions;
import org.dockbox.hartshorn.hsl.token.Token;

/**
 * Represents a class instance which is capable of carrying properties. This instance can
 * be virtual or native, depending on its implementation.
 *
 * @see org.dockbox.hartshorn.hsl.objects.virtual.VirtualInstance
 * @see org.dockbox.hartshorn.hsl.objects.external.ExternalInstance
 *
 * @since 0.4.12
 *
 * @author Guus Lieben
 */
public interface PropertyContainer {

    /**
     * Sets a property on the instance. If the property is not supported or accessible,
     * an {@link ScriptEvaluationError} is thrown.
     *
     * @param name The name of the property.
     * @param value The value of the property.
     * @param fromScope The scope from which the property is set.
     * @param options The execution options.
     *
     * @throws ScriptEvaluationError If the property is not supported or not accessible.
     */
    void set(Token name, Object value, VariableScope fromScope, ExecutionOptions options);

    /**
     * Gets a property from the instance. This may either return a {@link CallableNode}
     * if the property is a function, or any other {@link Object} if the property is
     * a field. If the property is not supported or accessible, a {@link ScriptEvaluationError}
     * is thrown.
     *
     * @param name The name of the property.
     * @param fromScope The scope from which the property is retrieved.
     * @param options The execution options.
     *
     * @return The value of the property.
     * @throws ScriptEvaluationError If the property is not supported or accessible.
     */
    Object get(Token name, VariableScope fromScope, ExecutionOptions options);
}
