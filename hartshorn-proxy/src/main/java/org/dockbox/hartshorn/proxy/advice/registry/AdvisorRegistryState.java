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

package org.dockbox.hartshorn.proxy.advice.registry;

/**
 * The state of the {@link AdvisorRegistry}. This is used to track whether the registry has been modified since its
 * initial creation. This is used to determine whether the proxy should be created. If the registry was never modified
 * then the proxy is not required, and the original instance can be returned by the caller of the
 * {@link org.dockbox.hartshorn.proxy.ProxyFactory}.
 *
 * @since 23.1
 * @author Guus Lieben
 */
public interface AdvisorRegistryState {

    /**
     * Configure whether the registry should track its state. If this is set to {@code false} then the registry will
     * not track whether it has been modified. This is useful for performance reasons and initial setup, as the registry
     * does not need to track its state if the proxy is not required.
     *
     * @param trackingState whether the registry should track its state
     */
    void trackState(boolean trackingState);

    /**
     * Returns whether the registry has been modified since its initial creation.
     *
     * @return whether the registry has been modified
     */
    boolean modified();

    /**
     * Marks the registry as modified. This is used to indicate that the registry has been modified since its initial
     * creation.
     */
    void modify();

}
