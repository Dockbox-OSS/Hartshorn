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

package org.dockbox.hartshorn.commands.context;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.dockbox.hartshorn.commands.definition.ArgumentConverter;
import org.dockbox.hartshorn.inject.binding.collection.ComponentCollection;
import org.dockbox.hartshorn.util.introspect.view.TypeView;
import org.dockbox.hartshorn.util.option.Option;

public class ComponentCollectionArgumentConverterRegistry implements ArgumentConverterRegistry {

    private final transient Map<String, ArgumentConverter<?>> converterMap = new ConcurrentHashMap<>();

    public ComponentCollectionArgumentConverterRegistry(ComponentCollection<ArgumentConverter<?>> converters) {
        for(ArgumentConverter<?> converter : converters) {
            registerConverter(converter);
        }
    }

    public void registerConverter(ArgumentConverter<?> converter) {
        for (String key : converter.keys()) {
            key = key.toLowerCase();
            if (this.converterMap.containsKey(key)) {
                throw new IllegalArgumentException("Duplicate converter key '" + key + "' found");
            }
            this.converterMap.put(key, converter);
        }
    }

    @Override
    public boolean hasConverter(String key) {
        return this.converter(key).present();
    }

    @Override
    public Option<ArgumentConverter<?>> converter(String key) {
        return Option.of(this.converterMap.get(key.toLowerCase()));
    }

    @Override
    public boolean hasConverter(TypeView<?> type) {
        return this.converter(type).present();
    }

    @Override
    public <T> Option<ArgumentConverter<T>> converter(TypeView<T> type) {
        return Option.of(this.converterMap.values().stream()
                .filter(converter -> type.isChildOf(converter.type()))
                .map(converter -> (ArgumentConverter<T>) converter)
                .findFirst());
    }
}
