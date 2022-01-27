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

import org.dockbox.hartshorn.core.context.element.MethodContext;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class ProxyMethodCallbackImpl<T> implements ProxyMethodCallback<T> {

    @Getter final CallbackPhase phase;
    final ProxyCallback<T> function;

    @Override
    public void accept(final MethodContext<?, T> method, final T instance, final Object[] args, final ProxyContext context) {
        this.function.accept(method, instance, args, context);
    }
}
