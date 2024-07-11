/*
 * Copyright 2019-2024 the original author or authors.
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

package org.dockbox.hartshorn.inject.provider;

import org.dockbox.hartshorn.inject.ComponentRequestContext;
import org.dockbox.hartshorn.inject.InjectionCapableApplication;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.util.Tristate;
import org.dockbox.hartshorn.util.option.Option;

/**
 * TODO: #1060 Add documentation
 *
 * @param <T> ...
 *
 * @since 0.6.0
 *
 * @author Guus Lieben
 */
@FunctionalInterface
public interface PrototypeProvider<T> extends NonTypeAwareProvider<T> {

    @Override
    default Option<ObjectContainer<T>> provide(InjectionCapableApplication application, ComponentRequestContext requestContext) throws ApplicationException {
        return Option.of(ComponentObjectContainer.ofPrototype(this.get(requestContext)));
    }

    T get(ComponentRequestContext context) throws ApplicationException;

    @Override
    default LifecycleType defaultLifecycle() {
        return LifecycleType.PROTOTYPE;
    }

    @Override
    default Tristate defaultLazy() {
        return Tristate.TRUE;
    }

    static <T> PrototypeProvider<T> empty() {
        return context -> null;
    }
}
