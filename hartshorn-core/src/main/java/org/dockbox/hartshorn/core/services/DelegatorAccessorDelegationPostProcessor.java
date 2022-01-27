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

package org.dockbox.hartshorn.core.services;

import org.dockbox.hartshorn.core.annotations.activate.UseProxying;
import org.dockbox.hartshorn.core.annotations.activate.AutomaticActivation;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.core.proxy.DelegatorAccessor;
import org.dockbox.hartshorn.core.proxy.ProxyHandler;

@AutomaticActivation
public class DelegatorAccessorDelegationPostProcessor extends ProxyDelegationPostProcessor<DelegatorAccessor, UseProxying> {

    @Override
    public Class<UseProxying> activator() {
        return UseProxying.class;
    }

    @Override
    protected Class<DelegatorAccessor> parentTarget() {
        return DelegatorAccessor.class;
    }

    @Override
    protected DelegatorAccessor concreteDelegator(final ApplicationContext context, final ProxyHandler<DelegatorAccessor> handler, final TypeContext<? extends DelegatorAccessor> parent) {
        return context.get(AccessorFactory.class).delegatorAccessor(handler);
    }
}
