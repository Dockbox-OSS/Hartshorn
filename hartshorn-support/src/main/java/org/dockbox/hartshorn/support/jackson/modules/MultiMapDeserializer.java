package org.dockbox.hartshorn.support.jackson.modules;

import org.dockbox.hartshorn.util.collections.ArrayListMultiMap;
import org.dockbox.hartshorn.util.collections.MultiMap;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.deser.std.StdDeserializer;

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
