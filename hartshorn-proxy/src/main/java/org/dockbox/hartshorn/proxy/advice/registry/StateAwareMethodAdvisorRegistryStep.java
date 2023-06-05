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

import org.dockbox.hartshorn.proxy.advice.MethodAdvisorResolver;

/**
 * A {@link MethodAdvisorRegistryStep} that is aware of its state. This means that it can be used to resolve the
 * method advisors that are registered with it, and will notify the owning
 * {@link StateAwareAdvisorRegistry} of any changes to its state.
 *
 * @param <T> The type of the proxy instance as defined by the owning {@link AdvisorRegistry}
 * @param <R> The return type of the method
 *
 * @since 23.1
 * @author Guus Lieben
 */
public interface StateAwareMethodAdvisorRegistryStep<T, R> extends MethodAdvisorRegistryStep<T, R>, MethodAdvisorResolver<T, R> {
}
