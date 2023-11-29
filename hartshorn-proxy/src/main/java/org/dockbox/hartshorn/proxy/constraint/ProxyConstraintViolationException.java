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

import org.dockbox.hartshorn.util.ApplicationException;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * An exception that is thrown when a proxy constraint is violated. This exception contains a message that describes
 * why the constraint is violated.
 *
 * @since 0.5.0
 * @author Guus Lieben
 */
public class ProxyConstraintViolationException extends ApplicationException {

    public ProxyConstraintViolationException(String message) {
        super(message);
    }

    public ProxyConstraintViolationException(Set<ProxyConstraintViolation> violations) {
        this(violations.stream()
                .map(ProxyConstraintViolation::message)
                .collect(Collectors.joining("\n"))
        );
    }
}
