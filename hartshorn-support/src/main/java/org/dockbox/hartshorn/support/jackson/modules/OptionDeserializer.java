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
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.deser.std.StdDeserializer;

/**
 * A custom deserializer for {@link Option} instances. This deserializer reads JSON data and
 * constructs an {@link Option} from it. If the JSON value is null, it returns an empty
 * {@link Option}. Otherwise, it wraps the value in an {@link Option}.
 *
 * <p>A value does not have to have been serialized with {@link OptionSerializer} to be deserialized
 * with this deserializer.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public class OptionDeserializer extends StdDeserializer<Option<?>> {

    public OptionDeserializer() {
        super(Option.class);
    }

    @Override
    public Option<?> deserialize(
            JsonParser parser,
            DeserializationContext context
    ) throws JacksonException {
        Object value = parser.readValueAs(Object.class);
        if (value == null) {
            return Option.empty();
        } else {
            return Option.of(value);
        }
    }
}
