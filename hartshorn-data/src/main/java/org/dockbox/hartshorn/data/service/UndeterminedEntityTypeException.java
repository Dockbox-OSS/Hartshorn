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

package org.dockbox.hartshorn.data.service;

import org.dockbox.hartshorn.util.ApplicationRuntimeException;
import org.dockbox.hartshorn.util.reflect.MethodContext;

public class UndeterminedEntityTypeException extends ApplicationRuntimeException {
    public UndeterminedEntityTypeException(final MethodContext<?, ?> method) {
        super("Could not determine entity type of %s. Alternatively, set the entityType in the @Query annotation on this method or change the query to JPQL."
                .formatted(method.qualifiedName()));
    }
}
