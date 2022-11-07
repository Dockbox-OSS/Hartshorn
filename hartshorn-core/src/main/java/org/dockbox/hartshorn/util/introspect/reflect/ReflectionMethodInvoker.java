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

package org.dockbox.hartshorn.util.introspect.reflect;

import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.util.option.Attempt;

import java.lang.reflect.Method;

public class ReflectionMethodInvoker<T, P> implements MethodInvoker<T, P> {

    @Override
    public Attempt<T, Throwable> invoke(final MethodView<P, T> method, final P instance, final Object[] args) {
        final Attempt<T, Throwable> result = Attempt.of(() -> {
            final Method jlrMethod = method.method();
            // Do not use explicit casting here, as it will cause a ClassCastException if the method
            // returns a primitive type. Instead, use the inferred type from the method view.
            //noinspection unchecked
            return (T) jlrMethod.invoke(instance, args);
        }, Throwable.class);

        if (result.errorPresent()) {
            Throwable cause = result.error();
            if (result.error().getCause() != null) cause = result.error().getCause();
            return Attempt.of(result.orNull(), cause);
        }
        return result;
    }
}
