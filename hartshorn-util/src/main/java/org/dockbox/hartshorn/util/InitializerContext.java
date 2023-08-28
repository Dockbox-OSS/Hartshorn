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

package org.dockbox.hartshorn.util;

import org.dockbox.hartshorn.context.Context;

public interface InitializerContext<I> extends Context {
    I input();

    /**
     * Transforms the input into a new context. This is useful for when you want to keep the
     * same context that is attached to the input, but want to change the input itself. This
     * will return a new context with the new input, the original context will not be modified.
     *
     * @param input the new input
     * @return the new context, containing the new input and a copy of the original context
     * @param <T> the type of the new input
     */
    <T> InitializerContext<T> transform(T input);

}
