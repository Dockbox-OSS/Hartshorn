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

package org.dockbox.hartshorn.proxy.advice;

import org.dockbox.hartshorn.proxy.advice.registry.StateAwareAdvisorRegistry;
import org.dockbox.hartshorn.proxy.advice.wrap.MethodWrapper;
import org.dockbox.hartshorn.proxy.advice.wrap.MethodWrapperList;
import org.dockbox.hartshorn.proxy.advice.wrap.ProxyCallbackContext;

import java.lang.reflect.Method;
import java.util.Collection;

/**
 * An implementation of {@link ProxyAdvisor} that uses a {@link StateAwareAdvisorRegistry} to resolve advisors.
 *
 * @param <T> the type of the proxy instance
 *
 * @since 0.5.0
 * @author Guus Lieben
 */
public class RegistryProxyAdvisor<T> implements ProxyAdvisor<T> {

    private final StateAwareAdvisorRegistry<T> advisors;

    public RegistryProxyAdvisor(StateAwareAdvisorRegistry<T> advisors) {
        this.advisors = advisors;
    }

    @Override
    public StateAwareAdvisorRegistry<T> resolver() {
        return this.advisors;
    }

    @Override
    public <U> U safeWrapIntercept(ProxyCallbackContext<T> context, ProxyInterceptFunction<U> interceptFunction) throws Throwable {
        Method method = context.method().method().orElseThrow(() -> null);
        Collection<MethodWrapper<T>> wrappers = this.resolver()
                .method(method)
                .wrappers();
        MethodWrapper<T> methodWrapper = new MethodWrapperList<>(wrappers);

        methodWrapper.acceptBefore(context);
        try {
            U result = interceptFunction.handleInterception();
            methodWrapper.acceptAfter(context);
            return result;
        }
        catch (Throwable e) {
            ProxyCallbackContext<T> errorContext = context.acceptError(e);
            methodWrapper.acceptError(errorContext);
            throw e;
        }
    }
}
