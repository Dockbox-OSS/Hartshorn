package org.dockbox.hartshorn.support.jackson.modules;

import org.dockbox.hartshorn.util.option.Option;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ser.std.StdSerializer;

public class OptionSerializer extends StdSerializer<Option<?>> {

    public OptionSerializer() {
        super(Option.class);
    }

    @Override
    public void serialize(Option<?> value, JsonGenerator gen, SerializationContext provider) throws JacksonException {
        if (value.present()) {
            gen.writePOJO(value.get());
        } else {
            gen.writeNull();
        }
    }
}
