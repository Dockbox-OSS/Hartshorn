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

package org.dockbox.hartshorn.core.function;

import org.dockbox.hartshorn.core.exceptions.ApplicationException;

/**
 * Extension of {@link java.util.function.Function} with the addition of a
 * <code>throws ApplicationException</code> clause.
 *
 * @param <T> the type of the first argument to the function
 * @param <R> the type of the result of the function
 *
 * @see java.util.function.Function
 */
@FunctionalInterface
public interface CheckedFunction<T, R> {
    R apply(T t) throws ApplicationException;
}
