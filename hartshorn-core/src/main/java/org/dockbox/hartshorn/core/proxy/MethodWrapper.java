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

public interface MethodWrapper<T> {
    void acceptBefore(MethodContext<?, T> method, T instance, Object[] args);

    void acceptAfter(MethodContext<?, T> method, T instance, Object[] args);

    void acceptError(MethodContext<?, T> method, T instance, Object[] args, Throwable error);

    static <T> MethodWrapper<T> of(final ProxyCallback<T> before, final ProxyCallback<T> after, final ProxyCallback<T> afterThrowing) {
        return new MethodWrapper<>() {
            @Override
            public void acceptBefore(final MethodContext<?, T> method, final T instance, final Object[] args) {
                if (before != null) before.accept(method, instance, args);
            }

            @Override
            public void acceptAfter(final MethodContext<?, T> method, final T instance, final Object[] args) {
                if (after != null) after.accept(method, instance, args);
            }

            @Override
            public void acceptError(final MethodContext<?, T> method, final T instance, final Object[] args, final Throwable error) {
                if (afterThrowing != null) afterThrowing.accept(method, instance, args);
            }
        };
    }
}
