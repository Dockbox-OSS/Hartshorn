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

package org.dockbox.hartshorn.data.registry;

@Deprecated(forRemoval = true, since = "22.5")
public interface RegistryIdentifier {
    default boolean same(final Object o) {
        if (this == o) return true;
        if (!(o instanceof RegistryIdentifier that)) return false;
        return this.key().equals(that.key());
    }

    default String key() {
        if (this.getClass().isEnum()) {
            return ((Enum<?>) this).name();
        }
        throw new UnsupportedOperationException("Non-enum type " + this.getClass().getSimpleName() + " does not override key()");
    }
}
