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

package org.dockbox.hartshorn.inject.scope;

import org.dockbox.hartshorn.reporting.Reportable;
import org.dockbox.hartshorn.util.introspect.ParameterizableType;

/**
 * A key that can be used to identify a {@link Scope}. This contains the required metadata to
 * identify a scope, and can be used to manage scoped components in a {@link ScopeAwareComponentProvider}.
 *
 * <p>Scope keys contain a {@link ParameterizableType} that describes the type of the scope. This type can
 * be parameterized. Therefore, key instances differentiate between e.g. {@code TypeScope<String>} and
 * {@code TypeScope<Integer>}.
 *
 * <p>Keys are always immutable. Specific implementations may choose to provide utility builders or factories,
 * but should never allow the final {@link ScopeKey} to be mutable.
 *
 * @see ScopeAwareComponentProvider
 * @see Scope
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public interface ScopeKey extends Reportable {

    /**
     * Returns the name of the scope type. This is the simple name of the type, without any package
     * information, but including any type parameters.
     *
     * @return the name of the scope type
     */
    String name();

    /**
     * Returns the type of the scope. This contains the full type information, including any type
     * parameters.
     *
     * @return the type of the scope
     */
    ParameterizableType scopeType();
}
