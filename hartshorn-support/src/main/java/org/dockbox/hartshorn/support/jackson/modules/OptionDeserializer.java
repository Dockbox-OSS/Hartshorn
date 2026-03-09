package org.dockbox.hartshorn.support.jackson.modules;

import org.dockbox.hartshorn.util.option.Option;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.deser.std.StdDeserializer;

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
