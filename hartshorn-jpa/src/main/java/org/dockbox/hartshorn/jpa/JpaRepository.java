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

package org.dockbox.hartshorn.jpa;

import org.dockbox.hartshorn.context.ContextCarrier;
import org.dockbox.hartshorn.util.option.Option;

import java.util.Set;

public interface JpaRepository<T, ID> extends ContextCarrier {

    T refresh(T entity);

    T save(final T object);

    T update(T object);

    T updateOrSave(T object);

    void delete(T object);

    Set<T> findAll();

    Option<T> findById(ID id);

    void flush();
}
