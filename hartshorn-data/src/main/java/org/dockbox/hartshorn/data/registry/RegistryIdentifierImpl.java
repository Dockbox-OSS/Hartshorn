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

import java.util.Objects;

public class RegistryIdentifierImpl implements RegistryIdentifier {

    private final String key;

    public RegistryIdentifierImpl(final String key) {
        this.key = key;
    }

    public String key() {
        return this.key;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.key());
    }

    // Intended to support RegistryIdentifier implementations
    @Override
    public boolean equals(final Object o) {
        return RegistryIdentifier.super.same(o);
    }
}
