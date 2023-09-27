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
import org.dockbox.hartshorn.util.option.Option;

public class SingletonProvider<T> implements NonTypeAwareProvider<T> {

    private final ObjectContainer<T> container;

    public SingletonProvider(T instance) {
        this.container = new ObjectContainer<>(instance);
    }

    @Override
    public Option<ObjectContainer<T>> provide(ApplicationContext context) {
        return Option.of(this.container);
    }

}
