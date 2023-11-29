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
 * A proxy constraint is a validation rule that can be applied to a proxy. This can be used to validate
 * that a proxy can be created for a given type.
 *
 * @since 0.5.0
 * @author Guus Lieben
 */
@FunctionalInterface
public interface ProxyConstraint {

    /**
     * Validates the given type view against the constraint. If the constraint is not met, one or more
     * {@link ProxyConstraintViolation violations} should be returned. If the constraint is met, an
     * empty set should be returned.
     *
     * @param typeView the type view to validate
     * @return an error message if the constraint is not met, {@code null} otherwise
     */
    Set<ProxyConstraintViolation> validate(TypeView<?> typeView);
}
