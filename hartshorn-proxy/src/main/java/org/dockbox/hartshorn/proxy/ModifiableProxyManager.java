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

package org.dockbox.hartshorn.proxy;

/**
 * A {@link ProxyManager} implementation which allows for modification of the delegate instance.
 *
 * @param <T> The type of the delegate instance
 *
 * @since 22.4
 * @author Guus Lieben
 */
public interface ModifiableProxyManager<T> extends ProxyManager<T> {

    /**
     * Delegates all methods defined by the active type to the given delegate instance.
     * This targets an original instance, not the backing implementation.
     *
     * @param delegate The delegate instance
     * @return This factory
     */
    ModifiableProxyManager<T> delegate(T delegate);
}
