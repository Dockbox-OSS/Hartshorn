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

package org.dockbox.hartshorn.util.function;

import org.dockbox.hartshorn.util.ApplicationException;

/**
 * Extension of {@link java.util.function.Supplier} with the addition of a
 * {@code throws ApplicationException} clause.
 *
 * @param <T> the type of results supplied by this supplier
 *
 * @see java.util.function.Supplier
 *
 * @since 0.4.1
 *
 * @author Guus Lieben
 */
@FunctionalInterface
public interface CheckedSupplier<T> {

    /**
     * Gets a result.
     *
     * @return a result
     * @throws ApplicationException if an error occurs during the execution of the supplier
     */
    T get() throws ApplicationException;
}
