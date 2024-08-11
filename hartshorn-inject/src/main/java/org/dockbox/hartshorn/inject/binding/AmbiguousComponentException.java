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

package org.dockbox.hartshorn.inject.binding;

import java.util.Set;
import java.util.stream.Collectors;
import org.dockbox.hartshorn.inject.ComponentKey;
import org.dockbox.hartshorn.util.ApplicationRuntimeException;

/**
 * Thrown when two or more non-strict components match a lookup key. This indicates that two equally
 * qualified components are compatible with the lookup key, and that there is no strict binding for
 * the lookup key.
 *
 * @see org.dockbox.hartshorn.inject.InjectorConfiguration#isStrictMode()
 * @see ComponentKey
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public class AmbiguousComponentException extends ApplicationRuntimeException {
    public AmbiguousComponentException(ComponentKey<?> lookupKey, Set<ComponentKey<?>> foundKeys) {
        super(
            "Ambiguous component lookup for key " + lookupKey
                + ". Found " + foundKeys.size() + " components: " + foundKeys.stream()
                .map(key -> key.qualifiedName(true))
                .collect(Collectors.joining(", ")));
    }
}
