package org.dockbox.hartshorn.util;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.SequencedMap;

public final class ObjectDescriber<T> {

    private final T object;
    private final ObjectDescriptionStyle style;
    private final SequencedMap<String, Object> fields = new LinkedHashMap<>();

    private ObjectDescriber(T object, ObjectDescriptionStyle style) {
        this.object = object;
        this.style = style;
    }

    public static <T> ObjectDescriber<T> of(T object) {
        return new ObjectDescriber<>(object, HartshornObjectDescriptionStyle.INSTANCE);
    }

    public static <T> ObjectDescriber<T> of(T object, ObjectDescriptionStyle style) {
        return new ObjectDescriber<>(object, style);
    }

    public ObjectDescriber<T> field(String name, Object value) {
        this.fields.put(name, value);
        return this;
    }

    public String describe() {
        StringBuilder builder = new StringBuilder();
        this.style.describeStart(builder, this.object);

        List<String> fieldNames = List.copyOf(fields.sequencedKeySet());
        for(int i = 0; i < fieldNames.size(); i++) {
            String fieldName = fieldNames.get(i);
            this.style.describeField(builder, this.object, fieldName, this.fields.get(fieldName));

            if(i < fieldNames.size() - 1) {
                this.style.describeFieldSeparator(builder, this.object);
            }
        }

        this.style.describeEnd(builder, this.object);
        return builder.toString();
    }
}
