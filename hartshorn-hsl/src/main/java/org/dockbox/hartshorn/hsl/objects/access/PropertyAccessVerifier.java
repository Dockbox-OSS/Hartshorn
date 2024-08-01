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

package org.dockbox.hartshorn.hsl.objects.access;

import org.dockbox.hartshorn.hsl.ast.statement.FieldStatement;
import org.dockbox.hartshorn.hsl.interpreter.VariableScope;
import org.dockbox.hartshorn.hsl.objects.InstanceReference;
import org.dockbox.hartshorn.hsl.token.Token;

/**
 * A verifier that checks if a property access is allowed. This is used to check if a property
 * can be accessed from a given scope. For example, a private property can only be accessed
 * from within the class that defines it.
 *
 * @since 0.4.12
 *
 * @author Guus Lieben
 */
@FunctionalInterface
public interface PropertyAccessVerifier {

    /**
     * Verifies if the given property can be accessed from the given scope. If the property
     * is not accessible, {@code false} is returned. If the property is accessible, {@code true}
     * is returned.
     *
     * @param at The name of the property.
     * @param field The field statement that defines the property.
     * @param instance The instance that the property is accessed on.
     * @param fromScope The scope from which the property is accessed.
     * @return {@code true} if the property can be accessed, {@code false} otherwise.
     */
    boolean verify(Token at, FieldStatement field, InstanceReference instance, VariableScope fromScope);
}
