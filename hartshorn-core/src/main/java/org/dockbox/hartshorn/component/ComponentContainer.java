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

package org.dockbox.hartshorn.component;

import org.dockbox.hartshorn.util.introspect.view.TypeView;

public interface ComponentContainer<T> {

    String id();

    String name();

    TypeView<T> type();

    boolean singleton();

    boolean lazy();

    @Deprecated(since = "0.6.0", forRemoval = true)
    ComponentType componentType();

    boolean permitsProxying();

    boolean permitsProcessing();

}
