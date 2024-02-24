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

import java.util.function.Supplier;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.util.function.CheckedSupplier;
import org.dockbox.hartshorn.util.option.Option;

/**
 * A {@link Supplier} that is able to provide instances using the given {@link Supplier}. If the
 * {@link Supplier} is unable to provide an instance, an empty {@link Option} will be returned
 * without throwing an exception.
 *
 * @param <C> The type to be provided.
 *
 * @author Guus Lieben
 * @see Provider
 * @see ContextDrivenProvider
 * @since 0.4.3
 */
public record SupplierProvider<C>(CheckedSupplier<C> supplier) implements NonTypeAwareProvider<C> {

    @Override
    public Option<ObjectContainer<C>> provide(ApplicationContext context, ComponentRequestContext requestContext) throws ApplicationException {
        C instance = this.supplier.get();
        return Option.of(instance).map(ComponentObjectContainer::new);
    }

}
