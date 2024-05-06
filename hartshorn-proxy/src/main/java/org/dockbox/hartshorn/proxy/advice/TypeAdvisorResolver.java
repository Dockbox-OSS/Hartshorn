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

package org.dockbox.hartshorn.proxy.advice;

import org.dockbox.hartshorn.util.option.Option;

/**
 * A resolver to look up advisors for a given type. This resolver can be used by {@link ProxyMethodInterceptHandler}s
 * to determine which advisors should be applied to a given type. This resolver is inherently immutable, and can not
 * be used to add or remove advisors.
 *
 * @param <T> The type of the proxy instance
 *
 * @since 0.4.9
 * @author Guus Lieben
 */
public interface TypeAdvisorResolver<T> {

    /**
     * Returns the delegate instance to which method calls of the advised type should be delegated. If no delegate
     * is available, an empty {@link Option} is returned.
     *
     * @return The delegate instance, if available
     */
    Option<T> delegate();

    /**
     * Returns the type that is being advised by this resolver.
     *
     * @return The advised type
     */
    Class<T> advisedType();
}
