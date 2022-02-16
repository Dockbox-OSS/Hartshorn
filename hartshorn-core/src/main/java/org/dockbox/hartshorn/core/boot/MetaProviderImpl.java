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

package org.dockbox.hartshorn.core.boot;

import org.dockbox.hartshorn.core.domain.TypedOwner;
import org.dockbox.hartshorn.core.domain.TypedOwnerImpl;
import org.dockbox.hartshorn.core.InjectorMetaProvider;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;

/**
 * An extension of {@link InjectorMetaProvider} which adds {@link Hartshorn}
 * as a valid {@link TypedOwner}. It is up to the final implementation to
 * decide whether this should be used.
 *
 * @author Guus Lieben
 * @since 21.9
 */
public class MetaProviderImpl extends InjectorMetaProvider {

    public MetaProviderImpl(final ApplicationContext context) {
        super(context);
    }

    @Override
    public TypedOwner lookup(final TypeContext<?> type) {
        if (type.is(Hartshorn.class)) {
            return TypedOwnerImpl.of(Hartshorn.PROJECT_ID);
        }
        return super.lookup(type);
    }
}
