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

package org.dockbox.hartshorn.core.binding;

import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.domain.Exceptional;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * A singleton-like provider, which uses an existing instance of type {@code T} to
 * provide the requested instance.
 *
 * @param <T> The type of the instance to provide.
 * @author Guus Lieben
 * @since 21.4
 * @see Provider
 * @see SupplierProvider
 * @see ContextDrivenProvider
 */
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
public class InstanceProvider<T> implements Provider<T> {

    private final T instance;

    @Override
    public Exceptional<T> provide(final ApplicationContext context) {
        return Exceptional.of(this.instance);
    }
}
