package org.dockbox.hartshorn.support.jackson.modules;

import org.dockbox.hartshorn.util.collections.MultiMap;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ser.std.StdSerializer;

import java.util.Collection;
import java.util.Map;

public class MultiMapSerializer extends StdSerializer<MultiMap<?, ?>> {

    public MultiMapSerializer() {
        super(MultiMap.class);
    }

    @Override
    public void serialize(MultiMap<?, ?> value, JsonGenerator gen, SerializationContext provider) throws JacksonException {
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
