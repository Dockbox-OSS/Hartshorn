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

package org.dockbox.hartshorn.util.introspect.reflect;

import java.lang.reflect.Method;

import org.dockbox.hartshorn.util.introspect.MethodInvoker;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.util.option.Option;

public class ReflectionMethodInvoker<T, P> implements MethodInvoker<T, P> {

    @Override
    public Option<T> invoke(MethodView<P, T> method, P instance, Object[] args) throws Throwable {
        Option<Method> jlrMethod = method.method();
        if(jlrMethod.absent()) {
            return null;
        }

        // Do not use explicit casting here, as it will cause a ClassCastException if the method
        // returns a primitive type. Instead, use the inferred type from the method view.
        try {
            //noinspection unchecked
            return Option.of((T) jlrMethod.get().invoke(instance, args));
        }
        catch (Throwable throwable) {
            if (throwable.getCause() != null) {
                throw throwable.getCause();
            }
            throw throwable;
        }
    }
}
