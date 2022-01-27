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

package org.dockbox.hartshorn.core.context.element;

import org.dockbox.hartshorn.core.domain.Exceptional;

import java.lang.reflect.Method;

public class ReflectionMethodInvoker<T, P> implements MethodInvoker<T, P> {

    @Override
    public Exceptional<T> invoke(final MethodContext<T, P> method, final P instance, final Object[] args) {
        final Exceptional<T> result = Exceptional.of(() -> {
            final Method jlrMethod = method.method();
            return (T) jlrMethod.invoke(instance, args);
        });
        if (result.caught()) {
            Throwable cause = result.error();
            if (result.error().getCause() != null) cause = result.error().getCause();
            return Exceptional.of(result.orNull(), cause);
        }
        return result;
    }
}
