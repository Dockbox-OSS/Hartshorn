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

import org.dockbox.hartshorn.util.collections.MultiMap;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ser.std.StdSerializer;

import java.util.Collection;
import java.util.Map;

/**
 * A custom serializer for {@link MultiMap} instances. This serializer converts a {@link MultiMap}
 * into a JSON object where each key maps to an array of values.
 *
 * <p>The deserializer equivalent of this serializer is {@link MultiMapDeserializer}.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public class MultiMapSerializer extends StdSerializer<MultiMap<?, ?>> {

    public MultiMapSerializer() {
        super(MultiMap.class);
    }

    @Override
    public void serialize(
            MultiMap<?, ?> value,
            JsonGenerator gen,
            SerializationContext provider
    ) throws JacksonException {
        gen.writeStartObject();
        for (Map.Entry<?, ? extends Collection<?>> entry : value.entrySet()) {
            gen.writeName(entry.getKey().toString());
            gen.writeStartArray();
            for (Object val : entry.getValue()) {
                gen.writePOJO(val);
            }
            gen.writeEndArray();
        }
        gen.writeEndObject();
    }
}
