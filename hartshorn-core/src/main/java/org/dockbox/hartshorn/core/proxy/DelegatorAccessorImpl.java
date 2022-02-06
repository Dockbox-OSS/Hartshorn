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

package org.dockbox.hartshorn.core.proxy;

import org.dockbox.hartshorn.core.annotations.inject.Bound;
import org.dockbox.hartshorn.core.annotations.stereotype.Component;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.core.domain.Exceptional;

import javax.inject.Inject;

import lombok.RequiredArgsConstructor;

@Component(permitProxying = false)
@RequiredArgsConstructor(onConstructor_ = @Bound)
public class DelegatorAccessorImpl<T> implements DelegatorAccessor<T> {

    @Inject
    private ApplicationContext context;
    private final ProxyHandler<T> handler;

    @Override
    public <A> Exceptional<A> delegator(final Class<A> type) {
        if (!this.handler.type().childOf(type)) return Exceptional.empty();
        final TypeContext<A> typeContext = TypeContext.of(type);
        return this.context.environment().manager().delegator(typeContext, (ProxyHandler<A>) this.handler);
    }
}
