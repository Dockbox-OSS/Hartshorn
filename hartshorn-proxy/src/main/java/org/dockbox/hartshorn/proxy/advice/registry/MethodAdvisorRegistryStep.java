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

import org.dockbox.hartshorn.proxy.advice.intercept.MethodInterceptor;
import org.dockbox.hartshorn.proxy.advice.wrap.MethodWrapper;
import org.dockbox.hartshorn.proxy.advice.wrap.MethodWrapperFactory;

import java.util.function.Consumer;

public interface MethodAdvisorRegistryStep<T, R> {

    AdvisorRegistry<T> delegate(T delegateInstance);

    AdvisorRegistry<T> intercept(MethodInterceptor<T, R> interceptor);

    AdvisorRegistry<T> wrapAround(MethodWrapper<T> wrapper);

    AdvisorRegistry<T> wrapAround(Consumer<MethodWrapperFactory<T>> wrapper);

}
