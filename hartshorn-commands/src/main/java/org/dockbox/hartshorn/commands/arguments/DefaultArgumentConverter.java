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

package org.dockbox.hartshorn.commands.arguments;

import org.dockbox.hartshorn.commands.definition.ArgumentConverter;

import java.util.Arrays;
import java.util.List;

/**
 * The default (abstract) implementation for {@link ArgumentConverter argument converters}.
 *
 * @param <T> The type the argument is converted into
 */
public abstract class DefaultArgumentConverter<T> implements ArgumentConverter<T> {

    private final String[] keys;
    private final Class<T> type;
    private final int size;

    protected DefaultArgumentConverter(Class<T> type, String... keys) {
        this(type, 1, keys);
    }

    protected DefaultArgumentConverter(Class<T> type, int size, String... keys) {
        if (0 == keys.length) {
            throw new IllegalArgumentException("Cannot create argument converter without at least one key");
        }
        this.keys = keys;
        this.type = type;
        this.size = size;
    }

    @Override
    public Class<T> type() {
        return this.type;
    }

    @Override
    public List<String> keys() {
        return Arrays.asList(this.keys);
    }

    @Override
    public int size() {
        return this.size;
    }
}
