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

package org.dockbox.hartshorn.util;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.component.ComponentKey;

public final class Lazy<T> {

    private final ApplicationContext applicationContext;
    private final ComponentKey<T> type;
    private T value;

    private Lazy(ApplicationContext applicationContext, ComponentKey<T> type) {
        this.applicationContext = applicationContext;
        this.type = type;
    }

    public static <T> Lazy<T> of(ApplicationContext applicationContext, Class<T> type) {
        return new Lazy<>(applicationContext, ComponentKey.of(type));
    }

    public static <T> Lazy<T> of(ApplicationContext applicationContext, ComponentKey<T> type) {
        return new Lazy<>(applicationContext, type);
    }

    public static <T> Lazy<T> ofInstance(Class<T> type, T instance) {
        Lazy<T> lazy = of(null, type);
        lazy.value = instance;
        return lazy;
    }

    public T get() {
        if (this.value == null) {
            this.value = this.applicationContext.get(this.type);
        }
        return this.value;
    }
}
