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

import org.dockbox.hartshorn.util.option.Option;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ser.std.StdSerializer;

/**
 * A custom serializer for {@link Option} instances. This serializer converts an {@link Option}
 * into a JSON value. If the {@link Option} is empty, it serializes to null. Otherwise, it
 * serializes the contained value.
 *
 * <p>The deserializer equivalent of this serializer is {@link OptionDeserializer}.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public class OptionSerializer extends StdSerializer<Option<?>> {

    public OptionSerializer() {
        super(Option.class);
    }

    @Override
    public void serialize(
            Option<?> value,
            JsonGenerator gen,
            SerializationContext provider
    ) throws JacksonException {
        if (value.present()) {
            gen.writePOJO(value.get());
        } else {
            gen.writeNull();
        }
    }
}
