/*
 * Copyright 2019-2026 the original author or authors.
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

package org.dockbox.hartshorn.support.jackson.modules;

import org.dockbox.hartshorn.util.collections.ArrayListMultiMap;
import org.dockbox.hartshorn.util.collections.MultiMap;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.deser.std.StdDeserializer;

/**
 * A custom deserializer for {@link MultiMap} instances. This deserializer reads JSON data and
 * constructs a {@link MultiMap} from it. The expected JSON format is an object where each key maps
 * to an array of values.
 *
 * <p>The serializer equivalent of this deserializer is {@link MultiMapSerializer}.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public class MultiMapDeserializer extends StdDeserializer<MultiMap<?, ?>> {

    public MultiMapDeserializer() {
        super(MultiMap.class);
    }

    @Override
    public MultiMap<?, ?> deserialize(
            JsonParser parser,
            DeserializationContext context
    ) throws JacksonException {
        MultiMap<Object, Object> map = new ArrayListMultiMap<>();
        while (parser.nextToken() != null) {
            String key = parser.currentName();
            parser.nextToken(); // Move to the value (array)
            while (parser.nextToken() != null && !parser.currentToken().isStructEnd()) {
                Object value = parser.readValueAs(Object.class);
                map.put(key, value);
            }
        }
        return map;
    }
}
