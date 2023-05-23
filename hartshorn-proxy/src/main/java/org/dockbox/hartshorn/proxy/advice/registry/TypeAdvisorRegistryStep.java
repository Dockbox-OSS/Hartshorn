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
 * Configuration step for {@link AdvisorRegistry}s. This step is used to configure the registry by adding
 * advisors for specific types.
 *
 * @see AdvisorRegistry
 * @param <T> the type of the proxy object
 * @param <S> the advised type, which is assignable to T
 *
 * @since 23.1
 * @author Guus Lieben
 */
public interface TypeAdvisorRegistryStep<S, T> {

    /**
     * Delegates all methods defined by the given {@code type} to the given delegate instance. This
     * targets a backing implementation, not the original instance.
     *
     * @param delegateInstance the instance to which the method is delegated
     * @return the registry, for chaining
     */
    AdvisorRegistry<T> delegate(S delegateInstance);

    /**
     * Delegates all methods defined by the given {@code type} which are not implemented in the advised type
     * to the given delegate instance. This means any method which is still abstract at the top-level will be
     * delegated, and any method with a concrete implementation will invoke the default method without interception.
     * This targets a backing implementation, not the original instance.
     *
     * @param delegateInstance the instance to which the method is delegated
     * @return the registry, for chaining
     */
    AdvisorRegistry<T> delegateAbstractOnly(S delegateInstance);

}
