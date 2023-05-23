/*
 * Copyright 2019-2023 the original author or authors.
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

package org.dockbox.hartshorn.proxy.constraint;

import org.dockbox.hartshorn.util.introspect.view.TypeView;

import java.util.Set;

/**
 * A proxy validator is used to validate that a proxy can be created for a given type. This can use a set of
 * {@link ProxyConstraint constraints} to validate the type.
 *
 * @since 23.1
 * @author Guus Lieben
 */
public interface ProxyValidator {

    /**
     * Adds a constraint to the validator.
     * @param constraint the constraint to add
     */
    void add(final ProxyConstraint constraint);

    /**
     * Returns the constraints that are used to validate the type.
     * @return the constraints that are used to validate the type
     */
    Set<ProxyConstraint> constraints();

    /**
     * Validates the given type against the constraints. If the type is invalid for any reason, one or more
     * {@link ProxyConstraintViolation violations} should be returned. If the type is valid, an empty set
     * should be returned.
     *
     * @param type the type to validate
     * @return one or more violations if the type is invalid, an empty set otherwise
     */
    Set<ProxyConstraintViolation> validate(final TypeView<?> type);
}
