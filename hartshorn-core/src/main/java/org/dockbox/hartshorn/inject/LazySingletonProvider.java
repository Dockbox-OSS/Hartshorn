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

package org.dockbox.hartshorn.inject;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.util.function.CheckedSupplier;
import org.dockbox.hartshorn.util.option.Option;

public class LazySingletonProvider<T> implements Provider<T> {

    private final CheckedSupplier<ObjectContainer<T>> supplier;
    private ObjectContainer<T> container;

    public LazySingletonProvider(final CheckedSupplier<ObjectContainer<T>> supplier) {
        this.supplier = supplier;
    }

    @Override
    public Option<ObjectContainer<T>> provide(final ApplicationContext context) throws ApplicationException {
        if (this.container == null) {
            this.container = this.supplier.get();
        }
        return Option.of(this.container);
    }
}
